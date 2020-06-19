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
    private MessageSource messageSource;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private SecurityService securityService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/password/reset")
    public String getAdminUsername(){
        return "/adm/admin/username";
    }

    @PostMapping("/password/reset")
    public String validateAdminUsername(WebRequest request, Model model, Locale locale, HttpSession session, RedirectAttributes redirectAttributes){
        String username = request.getParameter("username");
        if(username==null||"".equals(username)){
            model.addAttribute("failure",messageSource.getMessage("form.fields.required",null,locale));
            return "/adm/admin/username";
        }

        AdminUser adminUser = adminUserService.getUserByName(username);
        if(adminUser==null){
            model.addAttribute("failure",messageSource.getMessage("username.invalid",null,locale));
            return "/adm/admin/username";
        }
        else if("I".equals(adminUser.getStatus())){
            model.addAttribute("failure", messageSource.getMessage("user.inactive", null, locale));
            return "/adm/admin/username";

        }
        else if("L".equals(adminUser.getStatus())){
            model.addAttribute("failure", messageSource.getMessage("user.locked", null, locale));
            return "/adm/admin/username";
        }

        try {
            boolean result = securityService.sendOtp(adminUser.getEntrustId(), adminUser.getEntrustGroup());
            if (result) {
                session.setAttribute("username", adminUser.getUserName());
                session.setAttribute("entrustId", adminUser.getEntrustId());
                session.setAttribute("entrustGrp",adminUser.getEntrustGroup());
                session.setAttribute("redirectUrl", "/general/admin/users/reset");
                session.setAttribute("otpUrl", "/general/admin/users/otp");
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("otp.send.success", null, locale));
                return "redirect:/general/admin/users/otp";
            }
        }
        catch (InternetBankingSecurityException se){
            logger.error("Error sending OTP to user",se);
        }
        model.addAttribute("failure",messageSource.getMessage("otp.send.failure",null,locale));
        return "/adm/admin/username";
    }

    @GetMapping("/otp")
    public String getAdminOTP(){
        return "/adm/admin/otp";

    }

    @GetMapping("/reset")
    public String resetAdminPassword(HttpSession session, RedirectAttributes redirectAttributes){
        String username = (String)session.getAttribute("username");
        String result =  (String)session.getAttribute("result");
       if(username!=null){
           if(result!=null&&"Y".equals(result)) {
               try {
                   String message = adminUserService.resetPassword(username);
                   redirectAttributes.addFlashAttribute("message", message);
                   session.removeAttribute("username");
                   session.removeAttribute("entrustId");
                   session.removeAttribute("entrustGrp");
                   session.removeAttribute("otpUrl");
                   session.removeAttribute("redirectUrl");
                   session.removeAttribute("result");
                   return "redirect:/login/admin";
               } catch (PasswordException pe) {
                   redirectAttributes.addFlashAttribute("failure", pe.getMessage());
               }
           }
        }
        return "redirect:/general/admin/users/password/reset";
    }

}
