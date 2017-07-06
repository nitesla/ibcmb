package longbridge.controllers.admin;

import longbridge.services.implementations.CronJobServiceImpl;
import longbridge.utils.CronJobUtils;
//import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by Longbridge on 6/28/2017.
 */
@Controller
@RequestMapping("admin/job")
public class AdmJobController {
    @Autowired
    CronJobServiceImpl cronJobService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @GetMapping
    public String manage() {
        return "adm/job/manage-job";
    }

    @PostMapping
    public String getCronExpression(WebRequest webRequest, Principal principal) {
        String cronExpr = "";
        String username= "";
        String schedule  = webRequest.getParameter("scheduler");
        if (principal.getName() != null) {
            username = principal.getName();
        }
        logger.info("schedule {} and username {}",schedule,username);
        if((schedule != null)||(schedule.equalsIgnoreCase(""))){
            String cronExpression = CronJobUtils.getCronExpression(schedule, webRequest);
            if(!cronExpression.equalsIgnoreCase("")) {
                cronJobService.deleteRunningJob();
                cronJobService.saveRunningJob(username, cronExpression);
            }
        }


//        String second = webRequest.getParameter("second");
//        String minute = webRequest.getParameter("minute");
//        String[] hour = webRequest.getParameterValues("hour");
//        String[] week = webRequest.getParameterValues("week");
//        String[] month = webRequest.getParameterValues("month");
//        logger.info("second {} minute{} hour {} week {} month{}",second,minute,hour,week,month);
        return "adm/job/manage-job";
    }


}