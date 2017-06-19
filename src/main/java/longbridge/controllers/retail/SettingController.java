package longbridge.controllers.retail;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.PasswordException;
import longbridge.exception.PasswordMismatchException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.exception.WrongPasswordException;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.Email;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

/**
 * Created by Fortune on 4/5/2017.
 * Modified by Wunmi
 */
@Controller
@RequestMapping("/retail")
public class SettingController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;
    @Autowired
    private ConfigurationService configService;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    private MailService mailService;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/dashboard")
    public String getRetailDashboard(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());
        model.addAttribute("accountList", accountList);
        return "cust/dashboard";
    }

    @GetMapping("/change_password")
    public String ChangePaswordPage(Model model) {
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("passwordRules", passwordPolicy);
        model.addAttribute("custChangePassword", new CustChangePassword());
        return "cust/settings/pword";
    }

    @PostMapping("/change_password")
    public String ChangePassword(@ModelAttribute("custChangePassword") @Valid CustChangePassword custChangePassword, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes) throws Exception {
        if (result.hasErrors()) {
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("passwordRules", passwordPolicy);
            model.addAttribute("failure", "Please fill in the required fields");
            return "cust/settings/pword";
        }


        RetailUser user = retailUserService.getUserByName(principal.getName());

        try {
            String message = retailUserService.changePassword(user, custChangePassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/retail/dashboard";
        } catch (WrongPasswordException wpe) {
            model.addAttribute("failure", wpe.getMessage());
            logger.error("Wrong password from retail user {}", user.getUserName(), wpe.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "cust/settings/pword";
        } catch (PasswordPolicyViolationException pve) {
            model.addAttribute("failure", pve.getMessage());
            logger.error("Password policy violation from retail user {} error {}", user.getUserName(), pve.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "cust/settings/pword";
        } catch (PasswordMismatchException pme) {
            model.addAttribute("failure", pme.getMessage());
            logger.error("New password mismatch from retail user {}", user.getUserName(), pme.toString());
            return "cust/settings/pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for retail user {}", user.getUserName(), pe);
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "cust/settings/pword";
        }
    }

    @GetMapping("/reset_password")
    public String resetPaswordPage(Model model) {
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("passwordRules", passwordPolicy);
        model.addAttribute("custResetPassword", new CustResetPassword());
        return "cust/settings/new-pword";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@ModelAttribute("custResetPassword") @Valid CustResetPassword custResetPassword, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws Exception {
        if (result.hasErrors()) {
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("passwordRules", passwordPolicy);
            model.addAttribute("failure", "Please fill in the required fields");
            return "cust/settings/new-pword";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());

        try {
            String message = retailUserService.resetPassword(user, custResetPassword);
            redirectAttributes.addFlashAttribute("message", message);
            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }

            SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
            boolean tokenAuth = false;
            if (setting != null && setting.isEnabled()) {
                tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
            }

            if (tokenAuth) {
                return "redirect:/retail/token";
            }
            return "redirect:/retail/dashboard";
        } catch (PasswordPolicyViolationException pve) {
            model.addAttribute("failure", pve.getMessage());
            logger.error("Password policy violation from retail user {} error {}", user.getUserName(), pve.toString());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "cust/settings/new-pword";
        } catch (PasswordMismatchException pme) {
            model.addAttribute("failure", pme.getMessage());
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            model.addAttribute("passwordRules", passwordPolicy);
            logger.error("New password mismatch from retail user {}", user.getUserName(), pme.toString());
            return "cust/settings/new-pword";
        } catch (PasswordException pe) {
            model.addAttribute("failure", pe.getMessage());
            logger.error("Error changing password for retail user {}", user.getUserName(), pe);
            List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
            logger.info("PASSWORD RULES {}", passwordPolicy);
            model.addAttribute("passwordRules", passwordPolicy);
            return "cust/settings/new-pword";
        }
    }

    @GetMapping("/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model, Principal principal) {
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertP", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        return "cust/settings/alertpref";
    }

    @PostMapping("/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("failure", "Pls correct the errors");
            return "cust/settings/alertpref";
        }

        RetailUserDTO user = retailUserService.getUserDTOByName(principal.getName());
        retailUserService.changeAlertPreference(user, alertPref);

        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertP", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        model.addAttribute("message", "Preference Change Successful successful");
        return "cust/settings/alertpref";
    }


    @GetMapping("/bvn")
    public String linkBVN(Model model) {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));

        return "cust/account/linkbvn";
    }

    @PostMapping("/bvn")
    public String addBVN(Model model, Principal principal, HttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
        String bvn = request.getParameter("bvn");
        logger.info("BVN:" + bvn);
        String beneBank = request.getParameter("beneficiaryBank");
        logger.info("Bank:" + beneBank);


        RetailUser user = retailUserService.getUserByName(principal.getName());
        String fullname=user.getFirstName()+' '+user.getLastName();
        String custemail=user.getEmail();
        String custId=user.getCustomerId();
        String acctNumber=request.getParameter("acctNumber");



            SettingDTO setting = configService.getSettingByName("CUSTOMER_CARE_EMAIL");
            if (setting != null && setting.isEnabled()) {
                try {
                Email email = new Email.Builder()
                        .setRecipient(setting.getValue())
                        .setSubject(messageSource.getMessage("customer.bvn.link.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("customer.bvn.link.message", null, locale),user.getUserName(),fullname, bvn,acctNumber,custId,custemail))
                        .build();
                mailService.send(email);
                    String message =  messageSource.getMessage("bvn.add.success", null, locale);
                    redirectAttributes.addFlashAttribute("message", message);

                } catch (Exception ex) {
                    logger.error("Failed to send BVN request", ex);
                    String message = messageSource.getMessage("bvn.add.failure", null, locale);
                    redirectAttributes.addFlashAttribute("failure", message);
                }
            }

            return "redirect:/retail/dashboard";
    }


    @GetMapping("/contact")
    public String contactUs(){
        return "cust/contact";
    }

    @PostMapping("/contact")
    public String sendContactForm(WebRequest webRequest, Principal principal, Model model){
        String message = webRequest.getParameter("message");
        if (message == null){
            model.addAttribute("failure", "Field is required");
            return "cust/contact";
        }
        RetailUser user = retailUserService.getUserByName(principal.getName());
        messageService.sendRetailContact(message, user);
        return "redirect:/retail/dashboard";

    }



}
