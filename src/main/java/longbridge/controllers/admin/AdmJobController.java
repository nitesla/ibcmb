package longbridge.controllers.admin;

import longbridge.services.implementations.CronJobServiceImpl;
import longbridge.utils.CronJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Locale;

/**
 * Created by Longbridge on 6/28/2017.
 */
@Controller
@RequestMapping("admin/job")
public class AdmJobController {
    @Autowired
    CronJobServiceImpl cronJobService;
    @Autowired
    private MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @GetMapping
    public String manage() {

        return "adm/job/manage-job";
    }

    @PostMapping
    public String getCronExpression(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        String username= "";
        String category= webRequest.getParameter("category");
        String schedule  = webRequest.getParameter("scheduler");
        if (principal.getName() != null) {
            username = principal.getName();
        }
        logger.info("schedule {} and username {} category {}",schedule,username,category);
        if((schedule != null)&&(!schedule.equalsIgnoreCase(""))){
            String cronExpression = CronJobUtils.getCronExpression(schedule, webRequest).get("value");
            String cronExprValue = CronJobUtils.getCronExpression(schedule, webRequest).get("desc");
            logger.info("expression value {} and description {}",cronExpression,cronExprValue);
            if(!cronExpression.equalsIgnoreCase("")) {
                cronJobService.deleteRunningJob(category);
                cronJobService.keepCronJobEprsDetials(username, cronExpression,cronExprValue,category);
                logger.info("the category {}",category);
                redirectAttributes.addFlashAttribute("initCategory", category);
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("job.update.success", null, locale));

                return "redirect:/admin/job";
            }
        }
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("job.update.failed", null, locale));
        return "redirect:/admin/job";
    }
    @PostMapping("/current/description")
    @ResponseBody
    public String getCurrentCronExpre(WebRequest webRequest){
        String category = webRequest.getParameter("category");
        logger.debug("the selected category is {}", category);
        String currentExpression = cronJobService.getCurrentJobDesc(category);
        logger.debug("the current sron expression of category {} is {}",category,currentExpression);
        return currentExpression;
    }
}