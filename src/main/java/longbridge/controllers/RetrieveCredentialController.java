package longbridge.controllers;

import longbridge.dtos.PasswordStrengthDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.exception.PasswordMismatchException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.forms.CustResetPassword;
import longbridge.forms.ResetPasswordForm;
import longbridge.forms.RetrieveUsernameForm;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static longbridge.utils.StringUtil.compareAnswers;

/**
 * Created by Showboy on 17/07/2017.
 */
@Controller
public class RetrieveCredentialController {
    private Locale locale = LocaleContextHolder.getLocale();

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RetailUserRepo retailUserRepo;

    @Autowired
    private MailService mailService;

    @GetMapping("/forgot/password")
    public String showResetPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes){

        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.step = "1";
        resetPasswordForm.username = (String) session.getAttribute("username");
        if ((String) session.getAttribute("username") == null){
            return "redirect:/login/retail";
        }

        try{
            RetailUser retailUser = retailUserService.getUserByName((String) session.getAttribute("username"));
            Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            if (qa != null && !qa.isEmpty()){
                session.removeAttribute("retSecQestnAndAns");
                session.setAttribute("retSecQestnAndAns",qa);
                List<String> questions= qa.get("questions");
                List<String> answers= qa.get("answers");
                String secQuestion = questions.get(0);
                if (questions == null){
                    redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
                    return "redirect:/login/retail";
                }else{
//                    logger.info("the question size {} and values {} ",questions.size(),questions);
                    session.setAttribute("secretAnswer", answers);
                    model.addAttribute("secQuestion", questions);
                    model.addAttribute("noOfQuestion", questions.size());
                }

            }else {
                redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
                return "redirect:/login/retail";
            }
            PasswordStrengthDTO passwordStrengthDTO = passwordPolicyService.getPasswordStrengthParams();
            logger.debug("Password Strength {}" + passwordStrengthDTO);
            model.addAttribute("passwordStrength", passwordStrengthDTO);

            model.addAttribute("forgotPasswordForm", resetPasswordForm);
            List<String> policies = passwordPolicyService.getPasswordRules();
            model.addAttribute("policies", policies);
            return "cust/passwordreset";
        }catch (InternetBankingException e){
            return "redirect:/login/retail";
        }
    }

    @PostMapping("/forgot/password")
    public @ResponseBody
    String resetPassword(WebRequest webRequest, RedirectAttributes redirectAttributes, HttpSession session){
        Iterator<String> iterator = webRequest.getParameterNames();

//        while(iterator.hasNext()){
//            logger.info(iterator.next());
//        }


        String accountNumber = webRequest.getParameter("acct");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");
        String password= webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String username = (String) session.getAttribute("username");

        if (StringUtils.isBlank(username)){
            return "false";
        }

        if ( StringUtils.isBlank(username) ){
            return "false";
        }

        //confirm passwords are the same
        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
        if(!isValid){
            logger.error("Passwords do not match");
            return "false";
        }

        RetailUser retailUser = retailUserService.getUserByName(username);
        if (retailUser == null){
            return "false";
        }

        CustResetPassword custResetPassword = new CustResetPassword();
        custResetPassword.setNewPassword(password);
        custResetPassword.setConfirmPassword(confirmPassword);
        try{
            String message = retailUserService.resetPassword(retailUser,custResetPassword);
            redirectAttributes.addAttribute("success", message);
            return "true";
        }catch (PasswordPolicyViolationException e){
            return e.getMessage();
        }catch (PasswordMismatchException e){
            return e.getMessage();
        }catch (PasswordException e){
            return e.getMessage();
        }
    }

    @PostMapping("/rest/secAns")
    public @ResponseBody String getSecAns(WebRequest webRequest, HttpSession session){
        try{
            //confirm security question is correct
            Map<String, List<String>> qa = null;
            int noOfMismatch = 0;
            String username=webRequest.getParameter("username");
            logger.info("user {}",webRequest.getParameter("username"));
            List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
            if(session.getAttribute("retSecQestnAndAns") == null) {
                RetailUser retailUser = retailUserService.getUserByName(username);
                qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            }else {

                qa = (Map<String, List<String>>) session.getAttribute("retSecQestnAndAns");
            }
            //List<String> sec = null;
//            logger.info("sec questions {}",qa.get("questions"));
            if (qa != null){
                List<String> entAnswers = qa.get("answers");
//                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");

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
            logger.error("Error validating security questions and answers",e);
            return messageSource.getMessage("sec.ans.failed", null, locale);
        }
        return messageSource.getMessage("sec.ans.failed", null, locale);
    }
    @PostMapping("/rest/secAns/cifId")
    public @ResponseBody String getSecAnsByCustomerId(WebRequest webRequest, HttpSession session){
        try{
            //confirm security question is correct
            int noOfMismatch = 0;
            Map<String, List<String>> qa = null;
            String customerId = webRequest.getParameter("customerId");
//            logger.info("answer 1 {}",webRequest.getParameter("secAnswers"));
            logger.debug("cid id {}",webRequest.getParameter("customerId"));
            List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
            if(session.getAttribute("retSecQestnAndAnsFU") == null) {
                RetailUser retailUser = retailUserService.getUserByCustomerId(customerId);
                qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            }else{
                qa = (Map<String, List<String>>) session.getAttribute("retSecQestnAndAnsFU");
            }
            if (qa != null){
                List<String> answer = qa.get("answers");
                logger.debug("compared answer {}", compareAnswers(answers,answer));
                if(compareAnswers(answers,answer).equalsIgnoreCase("true")){
                    return "true";
                };
            }
            //return (String) session.getAttribute("username");
        }catch (Exception e){
            logger.error("Error validating security questions and answers",e);
            return messageSource.getMessage("sec.ans.failed", null, locale);
        }
        return messageSource.getMessage("sec.ans.failed", null, locale);
    }

    @PostMapping("/rest/sendGenPass/username")
    public @ResponseBody String sendGenPassword(WebRequest webRequest){
        try {
            logger.info("Trying to send generated password");
            String username = webRequest.getParameter("username");
            RetailUser retailUser = retailUserService.getUserByName(username);
            String tempPassword = passwordPolicyService.generatePassword();
            retailUser.setTempPassword(passwordEncoder.encode(tempPassword));
            String fullName = retailUser.getFirstName() + " " + retailUser.getLastName();
            retailUserRepo.save(retailUser);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("resetCode",tempPassword);

            Email email = new Email.Builder()
                    .setRecipient(retailUser.getEmail())
                    .setSubject(messageSource.getMessage("reset.password.subject", null, locale))
                    .setTemplate("mail/forgotpassword")
                    .build();
            mailService.sendMail(email,context);
            return "true";
        }catch (MailException me) {
            logger.error("Mail error occurred", me);
            return messageSource.getMessage("mail.failure", null, locale);
        } catch (Exception e){
            logger.error("Error occurred", e);
            return messageSource.getMessage("reset.send.password.failure", null, locale);
        }

    }

    @PostMapping("/rest/verGenPass/username/genpassword")
    public  @ResponseBody String verifyGenPassword(WebRequest webRequest){
        try {
            String genpassword = webRequest.getParameter("genpassword");
            String username = webRequest.getParameter("username");
            RetailUser retailUser = retailUserService.getUserByName(username);
            boolean match = passwordEncoder.matches(genpassword, retailUser.getTempPassword());
            if (match){
                return "true";
            }
            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
        }catch (InternetBankingException e){
            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
        }
    }

    @PostMapping("/rest/tokenAuth/username/token")
    public @ResponseBody String tokenAth(WebRequest webRequest){
        try {
            String username = webRequest.getParameter("username");
            String token = webRequest.getParameter("token");
            RetailUser retailUser = retailUserService.getUserByName(username);
            boolean message = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
            if (message){
                return "true";
            }
            return messageSource.getMessage("token.auth.failed", null, locale);
        }catch (InternetBankingException e){
            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
            return e.getMessage();
        }
    }

    @GetMapping("/forgot/username")
    public String showForgotUsername(Model model) {
        logger.info("forget username 2");
        RetrieveUsernameForm retrieveUsernameForm= new RetrieveUsernameForm();
        retrieveUsernameForm.step = "1";
        model.addAttribute("retUsernameForm", retrieveUsernameForm);
        return "cust/forgotusername";
    }

    @PostMapping("/forgot/username")
    public
    @ResponseBody
    String forgotUsername(WebRequest webRequest, HttpSession session) {
        Iterator<String> iterator = webRequest.getParameterNames();
        logger.info("forget username");
        while(iterator.hasNext()){
            logger.info(iterator.next());
        }
        Map<String, List<String>> qa =  null;
        String customerId = webRequest.getParameter("customerId");
//        String userEmail = webRequest.getParameter("email");
        try {
            if (customerId == null) {
                logger.error("Account Number not valid");
                return "false";
            }

            RetailUser user = retailUserService.getUserByCustomerId(customerId);

            if(user == null){
                return "redirect:/login/retail";
            }

            //confirm security question is correct
            String secAnswer="";
            if(session.getAttribute("retSecQestnAndAnsFU") == null) {
                qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());
            }else{
                qa = (Map<String, List<String>>) session.getAttribute("retSecQestnAndAnsFU");
            }            //List<String> sec = null;Map<String, List<String>>
            if (qa != null){
//                List<String> questions= qa.get("questions");
                List<String> answers= qa.get("answers");
                String result = StringUtil.compareAnswers(webRequest,answers);
//                    secAnswer = answers.stream().filter(Objects::nonNull).findFirst().orElse("");

                if (result.equalsIgnoreCase("true")){
                    logger.debug("User Info {}:", user.getUserName());
                    //Send Username to Email

                    String fullName = user.getFirstName()+" "+user.getLastName();

                    Context context = new Context();
                    context.setVariable("fullName", fullName);
                    context.setVariable("username", user.getUserName());

                    Email email = new Email.Builder()
                            .setRecipient(user.getEmail())
                            .setSubject(messageSource.getMessage("retrieve.username.subject",null,locale))
                            .setTemplate("mail/usernameretrieval")
                            .build();
                    mailService.sendMail(email,context);
                    return "true";
                }

            }else {
                return "false";
            }



        }catch (InternetBankingException e){
            return "false";
        }

        return "false";
    }

//    @GetMapping("/rest/retail/{email}/{accountNumber}")
//    public @ResponseBody String[] getAccountNameFromNumber(@PathVariable String email, @PathVariable String accountNumber){
//        String customerId = "";
//        String userEmail = "";
//        String[] userDetails = new String[2];
//        userDetails[0] = "";
//        userDetails[1] = "";
//        logger.info("Account nUmber {} email {}",accountNumber,email);
//        Account account = accountService.getAccountByAccountNumber(accountNumber);
//        logger.info("this is the acc ", account);
//        if (account != null){
//            customerId = account.getCustomerId();
//            RetailUser retailUser = retailUserService.getUserByEmail(email);
//            if(retailUser != null){
//                userDetails[0] = customerId;
//                userDetails[1] = retailUser.getEmail();
//                //userEmail = retailUser.getEmail();
//                logger.info("Account number : " + userDetails[0]);
//                logger.info("this is the mail" + userDetails[1]);
//            }
//
//        }else {
//            //nothing
//            customerId = "";
//        }
//        logger.info("cif i {}",customerId);
//        return userDetails;
//    }


}
