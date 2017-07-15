package longbridge.controllers.corporate;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.CorporateDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.CorporateUser;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/corporate")
public class CorpSettingController {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private CodeService codeService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private AccountService accountService;

    @Autowired
    ConfigurationService configService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/dashboard")
    public String getCorporateDashboard(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        //List<AccountDTO> accountList = accountService.getAccountsForDebitAndCredit(corporateUser.getCorporate().getCustomerId());
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(corporateUser.getCorporate().getCustomerId());
        model.addAttribute("accountList", accountList);

        boolean exp = passwordPolicyService.displayPasswordExpiryDate(corporateUser.getExpiryDate());
        logger.info("EXPIRY RESULT {} ", exp);
        if (exp){
            model.addAttribute("message", messageSource.getMessage("password.reset.notice", null, locale));
        }

        return "corp/dashboard";
    }

    @GetMapping("/settings/change_password")
    public String ChangePaswordPage( Model model)
    {
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("custChangePassword", new CustChangePassword());
        model.addAttribute("passwordRules", passwordPolicy);
        return "corp/settings/pword";

    }

    @PostMapping("/settings/change_password")
    public String ChangePassword(@ModelAttribute("custChangePassword") @Valid CustChangePassword custChangePassword, BindingResult result,Principal principal,  Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("failure","please fill in the required fields");
            model.addAttribute("passwordRules", passwordPolicy);

            return "corp/settings/pword";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        try {
            String message =corporateUserService.changePassword(user,custChangePassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corporate/logout";
        } catch (WrongPasswordException wpe) {
            model.addAttribute("failure",wpe.getMessage());
            logger.error("Wrong password from corporate user {}", user.getUserName(), wpe.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/pword";
        } catch (PasswordPolicyViolationException pve) {
            model.addAttribute("failure",pve.getMessage());
            logger.error("Password policy violation from corporate user {} error {}", user.getUserName(), pve.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/pword";
        } catch (PasswordMismatchException pme) {
            model.addAttribute("failure",pme.getMessage());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("passwordRules", passwordPolicy);
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
    public String resetPaswordPage(Model model){
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("passwordRules", passwordPolicy);
        model.addAttribute("custResetPassword", new CustResetPassword());
        return "corp/settings/new-pword";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@ModelAttribute("custResetPassword") @Valid CustResetPassword custResetPassword, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("failure","Please fill in the required fields");
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/new-pword";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        try {
            String message =corporateUserService.resetPassword(user, custResetPassword);
            redirectAttributes.addFlashAttribute("message", message);


            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }
            SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");
            boolean tokenAuth = false;
            if (setting != null && setting.isEnabled()) {
                tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
            }

            if (tokenAuth) {
                return "redirect:/corporate/token";
            }
            return "redirect:/corporate/dashboard";

        }
        catch (PasswordPolicyViolationException pve) {
            model.addAttribute("failure",pve.getMessage());
            logger.error("Password policy violation from retail user {} error {}", user.getUserName(), pve.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/new-pword";
        } catch (PasswordMismatchException pme) {
            model.addAttribute("failure",pme.getMessage());
            logger.error("New password mismatch from retail user {}", user.getUserName(), pme.toString());
            return "corp/settings/new-pword";
        } catch (PasswordException pe) {
            model.addAttribute("failure",pe.getMessage());
            logger.error("Error changing password for retail user {}", user.getUserName(), pe);
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "corp/settings/new-pword";
        }
    }

    @GetMapping("/settings/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model, Principal principal){
        CorporateUser user=corporateUserService.getUserByName(principal.getName());
        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertPref", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        return "corp/settings/alertpref";
    }

    @PostMapping("/settings/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "corp/settings/alertpref";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        System.out.println(alertPref);

        corporateUserService.changeAlertPreference(user, alertPref);

        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");

        model.addAttribute("alertPref", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        model.addAttribute("message","Preference Change Successful");
        return "redirect:/corporate/settings/alert_preference";
    }

    @GetMapping("/contact")
    public String contactUs(){
        return "corp/contact";
    }

    @PostMapping("/contact")
    public String sendContactForm(WebRequest webRequest, Principal principal, Model model, Locale locale, RedirectAttributes redirectAttributes){
        String message = webRequest.getParameter("message");
        if (message == null){
            model.addAttribute("failure", "Field is required");
            return "corp/contact";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(user.getCorporate().getId());
        try{
            messageService.sendCorporateContact(message, corporate);
            redirectAttributes.addFlashAttribute("message", "message sent successfully");
            return "redirect:/corporate/contact";
        }catch (InternetBankingException e){
            logger.error("Failed to send Email request", e);
            String mes = e.getMessage();
            model.addAttribute("failure", mes);
            return "corp/contact";
        }
    }

    @GetMapping("/bvn")
    public String linkBVN(){
        return "abc";
    }

}
