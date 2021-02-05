package longbridge.controllers;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.PasswordException;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
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

@Controller
@RequestMapping("/general/operations/users")
public class OperationsUserGeneralContoller {

    @Autowired
    private OperationsUserService operationsUserService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SecurityService securityService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PasswordPolicyService passwordService;



    @GetMapping("/password/reset/")
    public String getOpsUsername() {
        return "/ops/username";
    }


    @PostMapping("/password/reset/")
    public String validateOpsUsername(WebRequest request, Model model, Locale locale, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = request.getParameter("username");
        if (username == null || "".equals(username)) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/ops/username";
        }

        OperationsUser operationsUser=operationsUserService.getUserByNameWithoutAuthentication(username);
        if (operationsUser == null) {
            model.addAttribute("failure", messageSource.getMessage("username.invalid", null, locale));
            return "/ops/username";
        }
        else if("I".equals(operationsUser.getStatus())){
            model.addAttribute("failure", messageSource.getMessage("user.inactive", null, locale));
            return "/ops/username";
        }
        else if("L".equals(operationsUser.getStatus())){
            model.addAttribute("failure", messageSource.getMessage("user.locked", null, locale));
            return "/ops/username";
        }


        try {
            boolean result = securityService.sendOtp(operationsUser.getEntrustId(), operationsUser.getEntrustGroup());
            if (result) {
                session.setAttribute("username", username);
                session.setAttribute("entrustId", operationsUser.getEntrustId());
                session.setAttribute("entrustGrp", operationsUser.getEntrustGroup());
                session.setAttribute("redirectUrl", "/general/operations/users/reset");
                session.setAttribute("otpUrl", "/general/operations/users/otp");
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("otp.send.success", null, locale));
                return "redirect:/general/operations/users/otp";
            }
        } catch (InternetBankingSecurityException se) {
            logger.error("Error sending OTP to user", se);
        }
        model.addAttribute("failure", messageSource.getMessage("otp.send.failure", null, locale));
        return "/ops/username";
    }

    @GetMapping("/otp")
    public String getOperatinsOTP(){
        return "/ops/otp";

    }

    @GetMapping("/reset")
    public String resetOpsPassword(HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        String result = (String) session.getAttribute("result");
        if (username != null) {
            if ("Y".equals(result)) {
                try {
                    String message = operationsUserService.resetPassword(username);
                    redirectAttributes.addFlashAttribute("message", message);
                    session.removeAttribute("username");
                    session.removeAttribute("entrustId");
                    session.removeAttribute("entrustGrp");
                    session.removeAttribute("otpUrl");
                    session.removeAttribute("redirectUrl");
                    session.removeAttribute("result");
                    return "redirect:/login/ops";
                } catch (PasswordException pe) {
                    logger.error("Error due to ",pe);
                    redirectAttributes.addFlashAttribute("failure", pe.getMessage());
                }catch (Exception e){
                    logger.error("Error due to ",e);
                    redirectAttributes.addFlashAttribute("failure","Error resetting user's password");
                }
            }
        }
        return "redirect:/password/reset/";
    }
}
