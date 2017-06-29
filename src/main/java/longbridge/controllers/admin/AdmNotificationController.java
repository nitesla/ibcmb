package longbridge.controllers.admin;

import longbridge.dtos.NotificationsDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.NotificationsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Showboy on 24/06/2017.
 */
@Controller
@RequestMapping("/admin/notification")
public class AdmNotificationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private NotificationsService notificationsService;

    @GetMapping
    public String getSecurityQuestions() {
        return "/adm/notification/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<NotificationsDTO> getAllsecQuestions(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<NotificationsDTO> sq = notificationsService.getNotifications(pageable);
        DataTablesOutput<NotificationsDTO> out = new DataTablesOutput<NotificationsDTO>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/new")
    public String addFaq(NotificationsDTO notificationsDTO) {
        return "adm/notification/add";
    }

    @PostMapping
    public String createFaq(@ModelAttribute("notificationsDTO") @Valid NotificationsDTO notificationsDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "adm/notification/add";
        }

        try {
            String message = notificationsService.addNotification(notificationsDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/notification";
        }
        catch (InternetBankingException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            logger.error("Error creating code",ibe);
            return "adm/notification/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editCode(@PathVariable Long id, Model model) {
        NotificationsDTO notificationsDTO = notificationsService.getNotification(id);
        model.addAttribute("notification", notificationsDTO);
        return "adm/notification/edit";
    }

    @PostMapping("/update")
    public String updateCode(@ModelAttribute("notification") @Valid NotificationsDTO notificationsDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        if(result.hasErrors()){
            logger.error("Error occurred creating Notification{}", result.toString());
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "adm/notification/edit";

        }
        try {
            String message = notificationsService.updateNotification(notificationsDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/notification";
        }
        catch (InternetBankingException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            logger.error("Error updating Code",ibe);
            return "adm/notification/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteCode(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = notificationsService.deleteNotification(id);
            redirectAttributes.addFlashAttribute("message", message);
        }
        catch (InternetBankingException ibe){
            logger.error("Error deleting Code",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/notification";
    }
}
