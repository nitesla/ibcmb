package longbridge.controllers.corporate;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.PasswordException;
import longbridge.exception.PasswordMismatchException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.exception.WrongPasswordException;
import longbridge.forms.AlertPref;
import longbridge.forms.ChangePassword;
import longbridge.forms.CustChangePassword;
import longbridge.models.CorporateUser;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.CorporateUserService;
import longbridge.services.PasswordPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/corporate")
public class CorpSettingController {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CodeService codeService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("/dashboard")
    public String getCorporateDashboard(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<AccountDTO> accountList = accountService.getAccountsForDebitAndCredit(corporateUser.getCorporate().getCustomerId());
        model.addAttribute("accountList", accountList);
        return "corp/dashboard";
    }

    @GetMapping("/change_password")
    public String ChangePaswordPage(ChangePassword changePassword, Model model)
    {
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("passwordRules", passwordPolicy);
        return "corp/settings/pword";

    }

   @PostMapping("/change_password")
    public String ChangePassword(@Valid CustChangePassword custChangePassword, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("failure","please provide the appropriate input");
            return "corp/settings/pword";
        }

        if(!custChangePassword.getNewPassword().equals(custChangePassword.getConfirmPassword())){
            logger.info("PASSWORD MISMATCH");
            model.addAttribute("failure","Please provide the appropriate input");
            return "corp/settings/pword";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

       try {
           String message =corporateUserService.changePassword(user,custChangePassword);
           redirectAttributes.addFlashAttribute("message", message);
           return "redirect:/corporate/change_password";
       } catch (WrongPasswordException wpe) {
           result.reject("oldPassword", wpe.getMessage());
           logger.error("Wrong password from corporate user {}", user.getUserName(), wpe.toString());
           List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
           logger.info("PASSWORD RULES {}", passwordPolicy);
           model.addAttribute("passwordRules", passwordPolicy);
           return "corp/settings/pword";
       } catch (PasswordPolicyViolationException pve) {
           result.reject("newPassword", pve.getMessage());
           logger.error("Password policy violation from corporate user {} error {}", user.getUserName(), pve.toString());
           List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
           logger.info("PASSWORD RULES {}", passwordPolicy);
           model.addAttribute("passwordRules", passwordPolicy);
           return "corp/settings/pword";
       } catch (PasswordMismatchException pme) {
           result.reject("confirmPassword", pme.getMessage());
           logger.error("New password mismatch from corporate user {}", user.getUserName(), pme.toString());
           return "corp/settings/pword";
       } catch (PasswordException pe) {
           result.addError(new ObjectError("error", pe.getMessage()));
           logger.error("Error changing password for corporate user {}", user.getUserName(), pe);
           List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
           logger.info("PASSWORD RULES {}", passwordPolicy);
           model.addAttribute("passwordRules", passwordPolicy);
           return "corp/settings/pword";
       }
    }

    @GetMapping("/reset_password")
    public String resetPaswordPage(CustChangePassword custChangePassword, Model model){
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("passwordRules", passwordPolicy);
        return "corp/settings/new-pword";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@Valid CustChangePassword custChangePassword, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("failure","Pls correct the errors");
            return "corp/settings/new-pword";
        }

        if(!custChangePassword.getNewPassword().equals(custChangePassword.getConfirmPassword())){
            logger.info("PASSWORD MISMATCH");
            model.addAttribute("failure","Pls correct the errors");
            return "corp/settings/new-pword";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        try {
            String message =corporateUserService.changePassword(user, custChangePassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corp/reset_password";
        } catch (WrongPasswordException wpe) {
            result.reject("oldPassword", wpe.getMessage());
            logger.error("Wrong password from retail user {}", user.getUserName(), wpe.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/new-pword";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword", pve.getMessage());
            logger.error("Password policy violation from retail user {} error {}", user.getUserName(), pve.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/new-pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from retail user {}", user.getUserName(), pme.toString());
            return "corp/settings/new-pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for retail user {}", user.getUserName(), pe);
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/new-pword";
        }
    }

    @GetMapping("/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model, Principal principal){
        CorporateUser user=corporateUserService.getUserByName(principal.getName());
        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertP",user.getAlertPreference());
        model.addAttribute("prefs", pref);
        return "corp/settings/alertpref";
    }

    @PostMapping("/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "corp/settings/alertpref";
        }

        CorporateUserDTO user = corporateUserService.getUserDTOByName(principal.getName());

        corporateUserService.changeAlertPreference(user, alertPref);

        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertP", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        model.addAttribute("message","Preference Change Successful successful");
        return "corp/settings/alertpref";
    }

    @GetMapping("/bvn")
    public String linkBVN(){
        return "abc";
    }

}
