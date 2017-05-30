package longbridge.controllers;

import longbridge.exception.PasswordException;
import longbridge.services.AdminUserService;
import longbridge.services.SecurityService;
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

        boolean result = securityService.sendOtp(username);
        if(result) {
            session.setAttribute("username", username);
            session.setAttribute("redirectUrl", "/password/reset");
            session.setAttribute("otpUrl","/adm/admin/otp");
            model.addAttribute("message",messageSource.getMessage("otp.send.failure",null,locale));
            return "/adm/admin/otp";
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
