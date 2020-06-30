package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.AccountCoverage;
import longbridge.models.Code;
import longbridge.models.CorporateUser;
import longbridge.models.FeedBackStatus;
import longbridge.services.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private ConfigurationService configService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    AccountCoverageService coverageService;


    @RequestMapping("/dashboard")
    public String getCorporateDashboard(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Long corpId = corporateUser.getCorporate().getId();

        if (corporateUser==null) {
            return "redirect:/login/corporate";
        }

        List<AccountDTO> accountList = accountService.getAccountsAndBalances(corporateUser.getCorporate().getAccounts());

        SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
       /* if (dto!=null && dto.isEnabled()){
            String []list= StringUtils.split(dto.getValue(),",");
            accountList=  accountList
                    .stream()
                    .filter(i-> ArrayUtils.contains(list,i.getAccountType()))
                    .collect(Collectors.toList());

        }

         accountList.stream().filter(Objects::nonNull)
                .forEach(i-> {

                    Code code =codeService.getByTypeAndCode("ACCOUNT_CLASS",i.getAccountType());
                            if (code!=null && code.getDescription()!=null) {
                                i.setAccountType(code.getDescription());
                            }
                        }
                );*/
        List<LoanDTO> loans = new ArrayList<>();
        if (dto != null && dto.isEnabled()) {
            String[] transactionalAccounts = StringUtils.split(dto.getValue(), ",");
            accountList = accountList.stream()
                    .filter(Objects::nonNull)
                    .map(i -> {

                                if ("LOAN".equalsIgnoreCase(i.getAccountType())) {
                                    LoanDTO loan = integrationService.getLoanDetails(i.getAccountNumber());
                                    loans.add(loan);
                                }
                                return i;
                            }
                    ).filter(i -> ArrayUtils.contains(transactionalAccounts, i.getAccountType()))
                    .map(i -> {
                        Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", i.getAccountType());
                        if (code != null && code.getDescription() != null) {
                            i.setAccountType(code.getDescription());
                        }
                        return i;
                    })
                    .collect(Collectors.toList());
        }
        List<AccountCoverage> enabledCoverageList = new ArrayList<>();
        if (coverageService.enabledCoverageExist(corpId)){
            enabledCoverageList = coverageService.getEnabledCoverageForCorporate(corpId);
            logger.info("check1",coverageService.getEnabledCoverageForCorporate(corpId));


        }
            logger.info("check",coverageService.getEnabledCoverageForCorporate(corpId));

            model.addAttribute("loans", loans);
            model.addAttribute("accountList", accountList);
            model.addAttribute("corpId",corpId);
            model.addAttribute("coverageList",enabledCoverageList);
            boolean exp = passwordPolicyService.displayPasswordExpiryDate(corporateUser.getExpiryDate());
        logger.info("EXPIRY RESULT {} ", exp);
        if (exp){
            model.addAttribute("message", messageSource.getMessage("password.reset.notice", null, locale));
        }

        return "corp/dashboard";
    }



    @GetMapping("/error")
    public String getCorporateErrorPage() {
        return "/corp/error";

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
            return "redirect:/corporate/dashboard";
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
    public String resetPasswordPage(Model model){
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
//            redirectAttributes.addFlashAttribute("message", message);


            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }

            if ("Y".equals(user.getResetSecurityQuestion())) {
                logger.debug("Redirecting user to change security question");
                return "redirect:/corporate/reset/securityquestion";
            }

            SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");
            boolean tokenAuth = false;
            if (setting != null && setting.isEnabled()) {
                tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
            }

            if (tokenAuth) {
                return "redirect:/corporate/token";
            }

            if ("Y".equals(user.getIsFirstTimeLogon())){
                return "redirect:/corporate/setup";
            }

            return "redirect:/corporate/logout";

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


    @GetMapping("/reset/securityquestion")
    public String getSecurityQuestionPage(Model model) {

        try {
            List<CodeDTO> secQues = codeService.getCodesByType("SECURITY_QUESTION");
            int noOfQuestions = securityService.getMinUserQA();
            ArrayList[] masterList = new ArrayList[noOfQuestions];
            int questionsPerSection = (secQues.size() - (secQues.size() % noOfQuestions)) / noOfQuestions;
            int questnPostn = 0;
            for (int i = 0; i < noOfQuestions; i++) {
                masterList[i] = new ArrayList<>();
                for (int j = 0; j < questionsPerSection; j++) {
                    masterList[i].add(secQues.get(questnPostn));
                    questnPostn++;

                }
            }

            model.addAttribute("secQuestions", masterList);
            model.addAttribute("noOfQuestions", noOfQuestions);
            return "corp/settings/securityquestion";
        }
        catch (InternetBankingSecurityException se){
            return "redirect:/corporate/logout";
        }
    }


    @PostMapping("/reset/securityquestion")
    public String resetSecurityQuestions(Principal principal, WebRequest webRequest, RedirectAttributes redirectAttributes) {

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        List<String> secQuestions = new ArrayList<>();
        List<String> securityAnswers = new ArrayList<>();

        String noOfQuestions = webRequest.getParameter("noOfQuestions");

        if (noOfQuestions != null) {
            for (int i = 0; i < Integer.parseInt(noOfQuestions); i++) {
                String question = webRequest.getParameter("securityQuestion" + i);
                String answer = webRequest.getParameter("securityAnswer" + i);
                if (question == null || "".equals(question)) {
                    redirectAttributes.addFlashAttribute("failure","Error processing request");
                    return "redirect:/corporate/reset/securityquestion";
                }

                if (answer == null || "".equals(answer)) {
                    redirectAttributes.addFlashAttribute("failure","Please provide all answers to the questions");
                    return "redirect:/corporate/reset/securityquestion";
                }

                secQuestions.add(question);
                securityAnswers.add(answer);

                logger.debug(" sec questions list {}", secQuestions);
            }

            try{
                securityService.setUserQA(user.getUserName(),user.getEntrustGroup(),secQuestions,securityAnswers);
                corporateUserService.setSecurityQuestion(user.getId());
                return "redirect:/corporate/token";
            }
            catch (InternetBankingSecurityException e){
                redirectAttributes.addFlashAttribute("failure",e.getMessage());
                return "redirect:/corporate/reset/securityquestion";
            }
            catch (InternetBankingException ibe){
                redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
                return "redirect:/corporate/reset/securityquestion";
            }
        }
        return "redirect:/corporate/reset/securityquestion";

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

    @GetMapping("/settings/customerfeedback")
    public String setFeedBackPage(@ModelAttribute("feedBackStatus") FeedBackStatus feedBackStatus, Model model, Principal principal) {
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<CodeDTO> feedBackCodes = codeService.getCodesByType("FEEDBACK_STATUS");
        logger.info("feedbackstatus {}",user.getFeedBackStatus());
        model.addAttribute("presentStatus",user.getFeedBackStatus());
        model.addAttribute("status", feedBackCodes);
        return "corp/settings/customer-feedback-corp";
    }
    @PostMapping("/settings/customerfeedback")
    public String ChangeFeedBackStatus(@ModelAttribute("feedBackStatus") FeedBackStatus feedBackStatus, Principal principal, Model model, RedirectAttributes redirectAttributes) throws Exception {

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        user.setFeedBackStatus(feedBackStatus.getCode());
        corporateUserService.changeFeedBackStatus(user);
        String message = messageSource.getMessage("feedback.status", null, locale);
        redirectAttributes.addFlashAttribute("message", message);
        Iterable<CodeDTO> feedBackCodes = codeService.getCodesByType("FEEDBACK_STATUS");
        logger.info("the codes are {}",feedBackCodes);
        model.addAttribute("status", feedBackCodes);
        return "redirect:/corporate/settings/customerfeedback";
    }


}
