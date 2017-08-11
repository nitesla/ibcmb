package longbridge.controllers;

import longbridge.exception.*;
import longbridge.forms.CustResetPassword;
import longbridge.forms.ResetPasswordForm;
import longbridge.forms.RetrieveUsernameForm;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.Email;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.*;
import longbridge.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static longbridge.utils.StringUtil.compareAnswers;

/**
 * Created by Showboy on 20/07/2017.
 */
@Controller
public class RetrieveCorpCredentialController {
    private Locale locale = LocaleContextHolder.getLocale();

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorporateUserRepo corporateUserRepo;

    @Autowired
    private MailService mailService;

    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/forgot/password/corporate")
    public String showResetPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        session.removeAttribute("corpSecQestnAndAns");
        String username =(String) session.getAttribute("corpUsername");
        String corporateId = (String) session.getAttribute("corporateId");
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.step = "1";
        resetPasswordForm.username = username;
        if (username == null){
            return "redirect:/login/corporate";
        }
        logger.info("the username and corporateId is {} and {}",username,corporateId);

        try{
            CorporateUser corporateUser = corporateUserService.getUserByNameAndCorporateId(username, corporateId);
//            logger.info("the corporateUsername group {} and id {}",corporateUser.getEntrustGroup(),corporateUser.getEntrustId());
            Map<String, List<String>> qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
            logger.info("the question and answer {}",qa.get("questions"));
                if (qa != null && !qa.isEmpty()){
                    session.setAttribute("corpSecQestnAndAns",qa);
                List<String> questions= qa.get("questions");
                logger.info("questions {}",questions);
                if (questions == null){
                    redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
                    return "redirect:/login/corporate";
                }else{
//                    logger.info("the question size {} and values {} ",questions.size(),questions);
//                    session.setAttribute("secretAnswer", answers);
                    model.addAttribute("secQuestion", questions);
                    model.addAttribute("noOfQuestion", questions.size());
                }

            }else {
                redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
                return "redirect:/login/corporate";
            }


            model.addAttribute("forgotPasswordForm", resetPasswordForm);
            List<String> policies = passwordPolicyService.getPasswordRules();
            model.addAttribute("policies", policies);
            return "corp/passwordreset";
        }catch (InternetBankingException e){
            e.printStackTrace();
            return "redirect:/login/corporate";
        }
//        return "corp/passwordreset";
    }
@GetMapping("/rest/corp/secAns")
public @ResponseBody String getSecAns(WebRequest webRequest, HttpSession session){
    try{
        //confirm security question is correct
        int noOfMismatch = 0;
        Map<String, List<String>> qa = null;
        String username=webRequest.getParameter("username");
        logger.info("answer 1 {}",webRequest.getParameter("secAnswers"));
        logger.info("user {}",webRequest.getParameter("username"));
        List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
        CorporateUser corporateUser = corporateUserService.getUserByName(username);
        if(session.getAttribute("corpSecQestnAndAns") != null) {
            qa = (Map<String, List<String>>) session.getAttribute("corpSecQestnAndAns");
        }else {
            qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
        }
        //List<String> sec = null;
        logger.info("sec questions {}",qa);
        if (qa != null){
            List<String> entAnswers = qa.get("answers");
//                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");

            logger.info("user answer {}", answers);
            if((answers.size()>0)&&(entAnswers.size()>0)) {
                for(int i =0; i<answers.size();i++){
                    if(!answers.get(i).equalsIgnoreCase(entAnswers.get(i))){
                        noOfMismatch++;
                    }
                }
                logger.info("no of mis match is {}",noOfMismatch);
                if(noOfMismatch==0){
                    return "true";
                }
            }
        }
        //return (String) session.getAttribute("username");
    }catch (Exception e){
        logger.info(e.getMessage());
        return messageSource.getMessage("sec.ans.failed", null, locale);
    }
    return messageSource.getMessage("sec.ans.failed", null, locale);
}
    @GetMapping("/rest/corp/sendGenPass/{username}")
    public @ResponseBody String sendGenPassword(@PathVariable String username){
        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(username);
            String tempPassword = passwordPolicyService.generatePassword();
            corporateUser.setTempPassword(passwordEncoder.encode(tempPassword));
            String fullName = corporateUser.getFirstName() + " " + corporateUser.getLastName();
            corporateUserRepo.save(corporateUser);
            Email email = new Email.Builder()
                    .setRecipient(corporateUser.getEmail())
                    .setSubject(messageSource.getMessage("reset.password.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("reset.password.message", null, locale), fullName, tempPassword))
                    .build();
            mailService.send(email);
            return "true";
        }catch (MailException me) {
            return messageSource.getMessage("mail.failure", null, locale);
        } catch (Exception e){
            logger.info(e.getMessage());
            return messageSource.getMessage("reset.send.password.failure", null, locale);
        }

    }
    @GetMapping("/rest/corporate/verGenPass/{username}/{genpassword}")
    public  @ResponseBody String verifyGenPassword(@PathVariable String username, @PathVariable String genpassword){
        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(username);
            boolean match = passwordEncoder.matches(genpassword, corporateUser.getTempPassword());
            if (match){
                return "true";
            }
            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
        }catch (InternetBankingException e){
            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
        }
    }
    @GetMapping("/rest/corporate/password/check/{password}")
    public @ResponseBody String checkPassword(@PathVariable String password){
        String message = passwordPolicyService.validate(password, null);

        if (!"".equals(message)){
            return message;
        }
        return "true";
    }
    @GetMapping("/rest/corporate/tokenAuth/{username}/{token}")
    public @ResponseBody String tokenAth(@PathVariable String username, @PathVariable String token){
        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(username);
            boolean message = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
            logger.info("The message {}",message);
            if (message){
                return "true";
            }
            return messageSource.getMessage("token.auth.failed", null, locale);
        }catch (InternetBankingException e){
            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
            return e.getMessage();
        }
    }
    @PostMapping("/forgot/password/corporate")
    public @ResponseBody
    String resetPassword(WebRequest webRequest, RedirectAttributes redirectAttributes, HttpSession session){
        Iterator<String> iterator = webRequest.getParameterNames();

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }
//
//        String accountNumber = webRequest.getParameter("acct");
//        String securityQuestion = webRequest.getParameter("securityQuestion");
//        String securityAnswer = webRequest.getParameter("securityAnswer");
        String password= webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String username =(String) session.getAttribute("corpUsername");
        if (StringUtils.isBlank(username)){
            return "false";
        }

        //confirm passwords are the same
        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
        if(!isValid){
            logger.error("Passwords do not match");
            return "false";
        }

        //if ()

        //get Retail User by username
        CorporateUser corporateUser = corporateUserService.getUserByName(username);
        if (corporateUser == null){
            return "false";
        }

        //change password
        CustResetPassword custResetPassword = new CustResetPassword();
        custResetPassword.setNewPassword(password);
        custResetPassword.setConfirmPassword(confirmPassword);
        session.removeAttribute("corpUsername");
        session.removeAttribute("corpKey");
        session.removeAttribute("corpSecQestnAndAns");
        try{
            String message = corporateUserService.resetPassword(corporateUser,custResetPassword);
            logger.info("password {}",message);
            redirectAttributes.addAttribute("success", message);
            return "true";
        }catch (PasswordPolicyViolationException e){
            e.printStackTrace();
            return e.getMessage();
        }catch (PasswordMismatchException e){
            e.printStackTrace();
            return e.getMessage();
        }catch (PasswordException e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @GetMapping("/forgot/corporate/username")
    public String showForgotUsername(Model model) {
        RetrieveUsernameForm retrieveUsernameForm= new RetrieveUsernameForm();
        retrieveUsernameForm.step = "1";
        model.addAttribute("retUsernameForm", retrieveUsernameForm);
        return "corp/forgetusername";
    }

    @PostMapping("/forgot/corporate/username")
    public
    @ResponseBody
    String forgotUsername(WebRequest webRequest,HttpSession session) {
        Iterator<String> iterator = webRequest.getParameterNames();
        logger.info("forget username");
        String userEmail = webRequest.getParameter("email");
        String entityId = webRequest.getParameter("entityId");
        String entityGroup = webRequest.getParameter("entityGroup");
        String firstName = webRequest.getParameter("firstName");
        String userName = webRequest.getParameter("userName");
        while(iterator.hasNext()){
            logger.info(iterator.next());
        }

//        String accountNumber = webRequest.getParameter("acct");
//        String securityQuestion = webRequest.getParameter("securityQuestion");

        try {
            if (entityId ==null || entityGroup == null) {
                logger.error("Account Number not valid");
                return "false";
            }

//            RetailUser user = retailUserService.getUserByCustomerId(customerId);
            //confirm security question is correct
            if(session.getAttribute("corpSecQestnAndAns") !=null) {
                Map<String, List<String>> qa = (Map<String, List<String>>) session.getAttribute("corpSecQestnAndAns");
//            Map<String, List<String>> qa = securityService.getUserQA(entityId, entityGroup);
            //List<String> sec = null;
            if (qa != null){
//                List<String> questions= qa.get("questions");
                List<String> answers= qa.get("answers");
                String result = StringUtil.compareAnswers(webRequest,answers);
//                    secAnswer = answers.stream().filter(Objects::nonNull).findFirst().orElse("");

                if (result.equalsIgnoreCase("true")){
                    logger.debug("User Info {}:",userEmail );
                    session.removeAttribute("corpSecQestnAndAns");
                    //Send Username to Email
                    Email email = new Email.Builder()
                            .setRecipient(userEmail)
                            .setSubject(messageSource.getMessage("retrieve.username.subject",null,locale))
                            .setBody(String.format(messageSource.getMessage("retrieve.username.message",null,locale),firstName, userName))
                            .build();
                    mailService.send(email);
                    return "true";
                }
            }
            }else {
                return "false";
            }

        }catch (InternetBankingException e){
            return "false";
        }
        return "false";
    }

    @GetMapping("/rest/corporate/{email}/{accountNumber}")
    public @ResponseBody String[] getAccountNameFromNumber(@PathVariable String email,@PathVariable String accountNumber){
        logger.info("Account nUmber {} email {}",accountNumber,email);
        String customerId = "";
        String[] userDetails =  new String[4];
        userDetails[0] = "";
        userDetails[1] = "";
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        logger.info("this is the acc corpp ", account);
        if (account != null){
            customerId = account.getCustomerId();
            Corporate corporate = corporateService.getCorporateByCustomerId(customerId);
            if(corporate != null) {
                CorporateUser corporateUser = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, email);
                if(corporateUser != null){
                if (corporateUser.getEntrustGroup() != null && corporateUser.getEntrustId() != null) {
                    userDetails[0] = corporateUser.getEntrustId();
                    userDetails[1] = corporateUser.getEntrustGroup();
                    userDetails[2] = corporateUser.getFirstName();
                    userDetails[3] = corporateUser.getUserName();
                }
                logger.info("Cid id : " + customerId);
                logger.info("entrust id {} entrust group {} ", corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
                }
            }
        }else {
            //nothing
            customerId = "";
        }

        return userDetails;
    }

    @GetMapping("/rest/corporate/secQues/{userDetails}")
    public @ResponseBody List<String> getSecQuestionFromNumber(@PathVariable String[] userDetails, HttpSession session){
        String secQuestion = "";
        logger.info("user details : {} and {} ", userDetails[0],userDetails[1]);
//        logger.info(userDetails[0]);
        List<String> question = null;
        try {
            if (!("".equalsIgnoreCase(userDetails[0]))&&!("".equalsIgnoreCase(userDetails[1]))){
                Map<String, List<String>> qa = securityService.getUserQA(userDetails[0], userDetails[1]);
                //List<String> sec = null;
                session.removeAttribute("corpSecQestnAndAns");
                session.setAttribute("corpSecQestnAndAns",qa);
                if (qa != null && !qa.isEmpty()){
    //                logger.info("qs {}",qa);
                    question = qa.get("questions");
                    logger.info("questions {}", question);

                }else {
                    secQuestion = "";
                }
            }
            else {
                secQuestion = "";
            }
        }catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }catch (InternetBankingSecurityException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
logger.info("out of the try");
        return question;
    }
    @GetMapping("/rest/corporate/validate/secAns/{acctDetails}")
    public @ResponseBody String getSecAnsByCustomerId(@PathVariable String[] acctDetails, WebRequest webRequest, HttpSession session){
        try{
            //confirm security question is correct
            int noOfMismatch = 0;
            logger.info("answer 1 {}",webRequest.getParameter("secAnswers"));
            logger.info("acctDetails {}",acctDetails);
            List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
//            Map<String, List<String>> qa = securityService.getUserQA(acctDetails[0], acctDetails[1]);
            if(session.getAttribute("corpSecQestnAndAns") !=null) {
                Map<String, List<String>> qa = (Map<String, List<String>>) session.getAttribute("corpSecQestnAndAns");
                //List<String> sec = null;
                logger.info("sec questions {}", answers);
                if (qa != null) {
                    List<String> answer = qa.get("answers");
//                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");

                    logger.info("user answer {}", answer);
                    if (compareAnswers(answers, answer).equalsIgnoreCase("true")) {
                        return "true";
                    }
                    ;
                }
            }
            //return (String) session.getAttribute("username");
        }catch (Exception e){
            logger.info(e.getMessage());
            return messageSource.getMessage("sec.ans.failed", null, locale);
        }
        return messageSource.getMessage("sec.ans.failed", null, locale);
    }
}
