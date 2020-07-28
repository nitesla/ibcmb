package longbridge.controllers.retail;

import longbridge.dtos.SettingDTO;
import longbridge.models.Email;
import longbridge.services.ConfigurationService;
import longbridge.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by ayoade_farooq@yahoo.com on 5/19/2017.
 */
@Controller
@RequestMapping(value = "/retail/contact")
public class ContactController {

    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private MailService mailService;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    public ContactController() {

    }

    @PostMapping("/callback")
    public String requestCallback(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String name = webRequest.getParameter("name");
        String phone = webRequest.getParameter("phone");
        if (phone == null) {
            model.addAttribute("failure", "Field is required");
            return "index";
        }
        SettingDTO setting = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
        logger.info("SETTING RETRIEVED");
        if (setting != null && setting.isEnabled()) {
            try {
                Email mail = new Email.Builder()
                        .setRecipient(setting.getValue())
                        .setSubject("Call back Request from " + name)
                        .setBody("Preferred phone number for call back is " + phone)
                        .build();
                mailService.send(mail);
                redirectAttributes.addFlashAttribute("message", "Message sent successfully");

            } catch (Exception ex) {
                logger.error("Failed to send Email", ex);
                redirectAttributes.addFlashAttribute("failure", "Failed to send message");
            }
        }
        return "redirect:/#contact_us";
    }















}
