package longbridge.controllers.operations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.services.ConfigurationService;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Wunmi on 31/03/2017.
 */

@Controller
public class OperationsUserController {
    @Autowired
    OperationsUserService operationsUserService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SecurityService securityService;
    @Autowired
    private ConfigurationService configService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());



    @GetMapping("/ops/token")
    public String getOpsToken(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().setAttribute("2FA","2FA");

        return "/ops/token";
    }

    @PostMapping("/ops/token")
    public String performTokenAuthentication(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, Locale locale){

        String username = principal.getName();
        OperationsUser opsUser = operationsUserService.getUserByName(username);
        String tokenCode = request.getParameter("token");

        try{
            boolean result = securityService.performTokenValidation(opsUser.getEntrustId(),tokenCode);
            if(result){
                if( request.getSession().getAttribute("2FA") !=null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message",messageSource.getMessage("token.auth.success",null,locale)) ;

                if(opsUser.getExpiryDate()!=null) {
                    if (passwordPolicyService.displayPasswordExpiryDate(opsUser.getExpiryDate())) {
                        redirectAttributes.addFlashAttribute("expireDate", opsUser.getExpiryDate());
                    }
                }

                return "redirect:/ops/dashboard";
            }
        }
        catch (InternetBankingException ibe){
            logger.error("Error authenticating token");
        }
        redirectAttributes.addFlashAttribute("failure",messageSource.getMessage("token.auth.failure",null,locale));
        return "redirect:/ops/token";

    }


    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
    }

    @GetMapping("ops/users/password")
    public String changePassword(Model model){
        model.addAttribute("changePassword",new ChangePassword());
        return "/ops/pword";
    }

    @PostMapping("ops/users/password")
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes,Locale locale){
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required",null,locale)));
            return "/ops/pword";
        }

        OperationsUser user = operationsUserService.getUserByName(principal.getName());
        try {
            String message = operationsUserService.changePassword(user, changePassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/logout";
        } catch (WrongPasswordException wpe) {
            result.reject("oldPassword", wpe.getMessage());
            logger.error("Wrong password from operation user {}", user.getUserName(), wpe.toString());
            return "/ops/pword";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword",  pve.getMessage());
            logger.error("Password policy violation from operation user {}", user.getUserName(), pve.toString());
            return "/ops/pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from operation user {}", user.getUserName(), pme.toString());
            return "/ops/pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for operation user {}", user.getUserName(), pe);
            return "/ops/pword";
        }

    }

    @GetMapping("ops/users/password/new")
    public String changeDefaultPassword(Model model) {
        ChangeDefaultPassword changePassword = new ChangeDefaultPassword();
        model.addAttribute("changePassword", changePassword);
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
        return "/ops/new-pword";
    }


    @PostMapping("/ops/users/password/new")
    public String changeDefaultPassword(@ModelAttribute("changePassword") @Valid ChangeDefaultPassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes,Locale locale,HttpServletRequest httpServletRequest) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required",null,locale)));
            return "/ops/new-pword";
        }

        OperationsUser user = operationsUserService.getUserByName(principal.getName());
        try {
            String message = operationsUserService.changeDefaultPassword(user, changePassword);
            redirectAttributes.addFlashAttribute("message", message);
            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }

            SettingDTO setting = configService.getSettingByName("ENABLE_OPS_2FA");
            boolean tokenAuth = false;
            if (setting != null && setting.isEnabled()) {
                tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
            }
            if (tokenAuth) {
                return "redirect:/ops/token";
            }
            return "redirect:/ops/dashboard";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword", pve.getMessage());
            logger.error("Password policy violation from operations user {}", user.getUserName(), pve);
            return "/ops/new-pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from operations user {}", user.getUserName(), pme.toString());
            return "/ops/new-pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for admin user {}", user.getUserName(), pe);
            return "/ops/new-pword";
        }
    }

}

