package longbridge.controllers;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.PasswordException;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Created by Fortune on 5/29/2017.
 */
@Controller
@RequestMapping("/general/admin/users")
public class AdminUserGeneralController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    SecurityService securityService;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/password/reset")
    public String getAdminUsername(){
        return "/adm/admin/username";
    }

    @PostMapping("/password/reset")
    public String validateAdminUsername(WebRequest request, Model model, Locale locale, HttpSession session){
        String username = request.getParameter("username");
        if(username==null||"".equals(username)){
            model.addAttribute("failure",messageSource.getMessage("form.fields.required",null,locale));
            return "/adm/admin/username";
        }
        if(!adminUserService.userExists(username)){
            model.addAttribute("failure",messageSource.getMessage("username.invalid",null,locale));
            return "/adm/admin/username";
        }

        try {
            AdminUser adminUser = adminUserService.getUserByName(username);
            boolean result = securityService.sendOtp(adminUser.getEntrustId(), adminUser.getEntrustGroup());
            if (result) {
                session.setAttribute("username", username);
                session.setAttribute("redirectUrl", "/password/reset");
                session.setAttribute("otpUrl", "/adm/admin/otp");
                model.addAttribute("message", messageSource.getMessage("otp.send.failure", null, locale));
                return "/adm/admin/otp";
            }
        }
        catch (InternetBankingSecurityException se){
            logger.error("Error sending OTP to user",se);
        }
        model.addAttribute("failure",messageSource.getMessage("otp.send.failure",null,locale));
        return "/adm/admin/username";
    }

    @GetMapping("/password/admin/reset")
    public String resetAdminPassword(HttpSession session, RedirectAttributes redirectAttributes){
        if(session.getAttribute("username")!=null){
            String username =(String)session.getAttribute("username");
            try {
                String message = adminUserService.resetPassword(username);
                redirectAttributes.addFlashAttribute("message",message);
                session.removeAttribute("username");
                session.removeAttribute("url");
                return "redirect:/login/admin";
            }
            catch (PasswordException pe){
                redirectAttributes.addFlashAttribute("failure",pe.getMessage());
            }
        }
        return "redirect:/password/reset";
    }

}
