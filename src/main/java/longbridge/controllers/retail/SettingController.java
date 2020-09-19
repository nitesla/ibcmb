package longbridge.controllers.retail;

import longbridge.api.ExchangeRate;
import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
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

/**
 * Created by Fortune on 4/5/2017.
 * Modified by Wunmi
 */
@Controller
@RequestMapping("/retail")
public class SettingController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private IntegrationService integrationService;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    private MailService mailService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SecurityService securityService;

    @Autowired
    ServiceRequestController serviceRequestController;

    @Autowired
    ServiceReqConfigService serviceReqConfigService;

    private final Locale locale = LocaleContextHolder.getLocale();


    @RequestMapping("/dashboard")
    public String getRetailDashboard(Model model, Principal principal, HttpServletRequest request) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Long retId = retailUser.getId();

        if (retailUser == null) {
            return "redirect:/login/retail";
        }

        logger.debug("Getting user {} accounts and balances", retailUser.getUserName());
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());

        logger.debug("Retrieved {} account balance(s) for user {}", accountList.size(), retailUser.getUserName());
        SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        /* if (dto != null && dto.isEnabled()) {
            String[] list = StringUtils.split(dto.getValue(), ",");
            accountList = accountList
                    .stream()
                    .filter(i -> ArrayUtils.contains(list, i.getAccountType()))
                    .collect(Collectors.toList());
        }

        accountList.stream().filter(Objects::nonNull)
                .forEach(i -> {
                            Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", i.getAccountType());
                            if (code != null && code.getDescription() != null) {
                                i.setAccountType(code.getDescription());
                            }
                        }
                );*/

        List<String> loansAccountList = new ArrayList<>();
        if (dto != null && dto.isEnabled()) {
            String[] transactionalAccounts = StringUtils.split(dto.getValue(), ",");
            accountList = accountList.stream()
                    .filter(Objects::nonNull)
                    .peek(i -> {

                        if ("LAA".equalsIgnoreCase(i.getAccountType())) {
                            loansAccountList.add(i.getAccountNumber());

                        }
                            }
                    ).filter(i -> ArrayUtils.contains(transactionalAccounts, i.getAccountType()))
                    .peek(i -> {
                        Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", i.getAccountType());
                        if (code != null && code.getDescription() != null) {
                            i.setAccountType(code.getDescription());
                        }
                    })
                    .collect(Collectors.toList());
        }
        List<Account> loanAccounts = accountService.getLoanAccounts(loansAccountList);
        model.addAttribute("accountList", accountList);
        model.addAttribute("retId",retId);
        model.addAttribute("loanAccounts",loanAccounts);
        model.addAttribute("mailLoanDTO",new MailLoanDTO());

        boolean expired = passwordPolicyService.displayPasswordExpiryDate(retailUser.getExpiryDate());
        if (expired) {
            model.addAttribute("message", messageSource.getMessage("password.reset.notice", null, locale));
        }

        logger.debug("Redirecting user {} to dashboard", retailUser.getUserName());
        return "cust/dashboard";
    }

    @GetMapping(path = "/dashboard/loans")
    public @ResponseBody
    DataTablesOutput<Account> getLoanAccount(DataTablesInput input) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<String> loansAccountList = new ArrayList<>();
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());

        SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        if (dto != null && dto.isEnabled()) {
            for (AccountDTO acc:accountList) {
                if ("LAA".equalsIgnoreCase(acc.getAccountType())) {
                    loansAccountList.add(acc.getAccountNumber());
                }
            }
        }

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Account> loanAccounts = accountService.getLoanAccounts(loansAccountList,pageable);
        DataTablesOutput<Account> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(loanAccounts.getContent());
        out.setRecordsFiltered(loanAccounts.getTotalElements());
        out.setRecordsTotal(loanAccounts.getTotalElements());
        return out;
    }



    @GetMapping("/error")
    public String getRetailErrorPage() {
        return "/cust/error";

    }


    @GetMapping("/settings/change_password")
    public String ChangePaswordPage(Model model) {
        List<String> passwordPolicy = passwordPolicyService.getPasswordRules();
        logger.info("PASSWORD RULES {}", passwordPolicy);
        model.addAttribute("passwordRules", passwordPolicy);
        model.addAttribute("custChangePassword", new CustChangePassword());
        return "cust/settings/pword";
    }

    @PostMapping("/settings/change_password")
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
           return "cust/settings/securityquestion";
       }
       catch (InternetBankingSecurityException se){

           return "redirect:/retail/logout";
       }
    }


    @PostMapping("/reset/securityquestion")
    public String resetSecurityQuestions(Principal principal, WebRequest webRequest, RedirectAttributes redirectAttributes) {

        RetailUser user = retailUserService.getUserByName(principal.getName());
        List<String> secQuestions = new ArrayList<>();
        List<String> securityAnswers = new ArrayList<>();

        String noOfQuestions = webRequest.getParameter("noOfQuestions");

        if (noOfQuestions != null) {
            for (int i = 0; i < Integer.parseInt(noOfQuestions); i++) {
                String question = webRequest.getParameter("securityQuestion" + i);
                String answer = webRequest.getParameter("securityAnswer" + i);
                if (question == null || "".equals(question)) {
                    redirectAttributes.addFlashAttribute("failure","Error processing request");
                    return "redirect:/retail/reset/securityquestion";
                }

                if (answer == null || "".equals(answer)) {
                    redirectAttributes.addFlashAttribute("failure","Please provide all answers to the questions");
                    return "redirect:/retail/reset/securityquestion";
                }

                secQuestions.add(question);
                securityAnswers.add(answer);

                logger.debug(" sec questions list {}", secQuestions);
            }

            try{
                securityService.setUserQA(user.getUserName(),user.getEntrustGroup(),secQuestions,securityAnswers);
                retailUserService.setSecurityQuestion(user.getId());

                return "redirect:/retail/token";
            } catch (InternetBankingException e){
                redirectAttributes.addFlashAttribute("failure",e.getMessage());
                return "redirect:/retail/reset/securityquestion";
            }
        }
        return "redirect:/retail/reset/securityquestion";

    }


    @GetMapping("/reset_password")
    public String resetPasswordPage(Model model) {
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
//            redirectAttributes.addFlashAttribute("message", message);

            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }

            if ("Y".equals(user.getResetSecurityQuestion())) {
                logger.debug("Redirecting user to change security question");
                return "redirect:/retail/reset/securityquestion";
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

    @GetMapping("/settings/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model, Principal principal) {
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertPref", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        return "cust/settings/alertpref";
    }

    @PostMapping("/settings/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("failure", "Pls correct the errors");
            return "cust/settings/alertpref";
        }

        RetailUserDTO user = retailUserService.getUserDTOByName(principal.getName());
        retailUserService.changeAlertPreference(user, alertPref);

        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertPref", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        model.addAttribute("message", "Preference Change Successful successful");
        return "redirect:/retail/settings/alert_preference";
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
        String fullname = user.getFirstName();
        String custemail = user.getEmail();
        String custId = user.getCustomerId();
        String acctNumber = request.getParameter("acctNumber");


        SettingDTO setting = configService.getSettingByName("CUSTOMER_CARE_EMAIL");
        if (setting != null && setting.isEnabled()) {
            try {
                Email email = new Email.Builder()
                        .setRecipient(setting.getValue())
                        .setSubject(messageSource.getMessage("customer.bvn.link.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("customer.bvn.link.message", null, locale), user.getUserName(), fullname, bvn, acctNumber, custId, custemail))
                        .build();
                mailService.send(email);
                String message = messageSource.getMessage("bvn.add.success", null, locale);
                logger.info("BVN request sent successfully");
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
    public String contactUs() {
        return "cust/contact";
    }

    @PostMapping("/contact")
    public String sendContactForm(WebRequest webRequest, Principal principal, Model model, Locale locale, RedirectAttributes redirectAttributes) {
        String message = webRequest.getParameter("message");
        if (message == null) {
            model.addAttribute("failure", "Field is required");
            return "cust/contact";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());

        try {
            messageService.sendRetailContact(message, user);
            redirectAttributes.addFlashAttribute("message", "message sent successfully");
            return "redirect:/retail/contact";
        } catch (InternetBankingException e) {
            logger.error("Failed to send Email request", e);
            String mes = e.getMessage();
            model.addAttribute("failure", mes);
            return "cust/contact";
        }
    }

    @GetMapping("/exchangerate")
    public String viewCurrencyExchangeRate(Model model) {
        List<ExchangeRate> rate = integrationService.getExchangeRate();
        model.addAttribute("rates", rate);
        return "cust/exchangerate";
    }

    @GetMapping("/faq")
    public String fAQ() {
        return "cust/faq";
    }

    @GetMapping("/settings/request/{reqId}")
    public String reDirectRequest(@PathVariable Long reqId, Model model, Principal principal){
        logger.info("routing setting request {}",reqId);
        return serviceRequestController.makeRequest(reqId,model,principal);
//        return "redirect:/retail/requests/"+id;

    }

    @GetMapping("/settings/customerfeedback")
    public String getFeedBackPage(@ModelAttribute("feedBackStatus") FeedBackStatus feedBackStatus,Model model,Principal principal) {
        RetailUserDTO user = retailUserService.getUserDTOByName(principal.getName());
        Iterable<CodeDTO> feedBackCodes = codeService.getCodesByType("FEEDBACK_STATUS");
        model.addAttribute("presentStatus",user.getFeedBackStatus());
        model.addAttribute("status", feedBackCodes);
        return "cust/settings/customer-feedback";
    }
    @PostMapping("/settings/customerfeedback")
    public String ChangeFeedBackStatus(@ModelAttribute("feedBackStatus") FeedBackStatus feedBackStatus, Principal principal, Model model, RedirectAttributes redirectAttributes) throws Exception {

        RetailUserDTO user = retailUserService.getUserDTOByName(principal.getName());
        user.setFeedBackStatus(feedBackStatus.getCode());
        retailUserService.changeFeedBackStatus(user);
        String message = messageSource.getMessage("feedback.status", null, locale);
        redirectAttributes.addFlashAttribute("message", message);
        Iterable<CodeDTO> feedBackCodes = codeService.getCodesByType("FEEDBACK_STATUS");
        model.addAttribute("status", feedBackCodes);
        return "redirect:/retail/settings/customerfeedback";
    }

    @GetMapping("/link/bvn")
    public String retailLinkBvn(Model model) {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
        model.addAttribute("requestConfig", serviceReqConfigService.getServiceReqConfigRequestName("LINK-BVN"));
        return "cust/bvn/linkbvn";
    }





}
