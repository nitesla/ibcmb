package longbridge.controllers;

import longbridge.dtos.PasswordStrengthDTO;
import longbridge.exception.*;
import longbridge.forms.CustResetPassword;
import longbridge.forms.ResetPasswordForm;
import longbridge.forms.RetrieveUsernameForm;
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
import org.thymeleaf.context.Context;

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


    @GetMapping("/forgot/password/corporate")
    public String showResetPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes){
       logger.info("here");
        session.removeAttribute("corpSecQestnAndAns");
        String username =(String) session.getAttribute("corpUsername");
        String corporateId = (String) session.getAttribute("corporateId");
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.step = "1";
        resetPasswordForm.username = username;
        if (username == null){
            redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
            return "redirect:/login/corporate";
        }
        logger.info("the username and corporateId is {} and {}",username,corporateId);

        try{
            CorporateUser corporateUser = corporateUserService.getUserByNameAndCorporateId(username, corporateId);
//            logger.info("the corporateUsername group {} and id {}",corporateUser.getEntrustGroup(),corporateUser.getEntrustId());
            Map<String, List<String>> qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
//            logger.info("the question {}",qa.get("questions"));
                if (qa != null && !qa.isEmpty()){
                    session.setAttribute("corpSecQestnAndAns",qa);
                List<String> questions= qa.get("questions");
                if (questions == null){
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("corp.reg.inconplete.failed", null, locale));
                    return "redirect:/login/corporate";
                }else{
//                    logger.info("the question size {} and values {} ",questions.size(),questions);
//                    session.setAttribute("secretAnswer", answers);
                    model.addAttribute("secQuestion", questions);
                    model.addAttribute("noOfQuestion", questions.size());
                }

            }else {
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("corp.reg.inconplete.failed", null, locale));
                return "redirect:/login/corporate";
            }
            PasswordStrengthDTO passwordStrengthDTO = passwordPolicyService.getPasswordStrengthParams();
            logger.debug("Password Strength {}" + passwordStrengthDTO);
            model.addAttribute("passwordStrength", passwordStrengthDTO);

            model.addAttribute("forgotPasswordForm", resetPasswordForm);

            model.addAttribute("forgotPasswordForm", resetPasswordForm);
            List<String> policies = passwordPolicyService.getPasswordRules();
            model.addAttribute("policies", policies);
            return "corp/passwordreset";
        }catch (InternetBankingException e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("corp.reg.inconplete.failed", null, locale));
            return "redirect:/login/corporate";
        }
//        return "corp/passwordreset";
    }
@PostMapping("/rest/corp/secAns")
public @ResponseBody String getSecAns(WebRequest webRequest, HttpSession session){
    try{
        //confirm security question is correct
        int noOfMismatch = 0;
        Map<String, List<String>> qa = null;
        String username=webRequest.getParameter("username");
        logger.debug("user {}",webRequest.getParameter("username"));
        List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
        CorporateUser corporateUser = corporateUserService.getUserByName(username);
        if(session.getAttribute("corpSecQestnAndAns") != null) {
            qa = (Map<String, List<String>>) session.getAttribute("corpSecQestnAndAns");
        }else {
            qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
        }
        //List<String> sec = null;
//        logger.info("sec questions {}",qa);
        if (qa != null){
            List<String> entAnswers = qa.get("answers");
//                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");

            if((answers.size()>0)&&(entAnswers.size()>0)) {
                for(int i =0; i<answers.size();i++){
                    if(!answers.get(i).equalsIgnoreCase(entAnswers.get(i))){
                        noOfMismatch++;
                    }
                }
                logger.debug("no of mis match is {}",noOfMismatch);
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

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("resetCode",tempPassword);

            Email email = new Email.Builder()
                    .setRecipient(corporateUser.getEmail())
                    .setSubject(messageSource.getMessage("reset.password.subject", null, locale))
                    .setTemplate("mail/forgotpassword")
                    .build();
            mailService.sendMail(email,context);
            return "true";
        }catch (MailException me) {
            return messageSource.getMessage("mail.failure", null, locale);
        } catch (Exception e){
            logger.info(e.getMessage());
            return messageSource.getMessage("reset.send.password.failure", null, locale);
        }

    }
    @PostMapping("/rest/corporate/verGenPass/username/genpassword")
    public  @ResponseBody String verifyGenPassword(WebRequest webRequest){
        try {
            String genpassword = webRequest.getParameter("genpassword");
            String username = webRequest.getParameter("username");
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

    @PostMapping("/rest/corporate/password/check/password")
    public @ResponseBody String checkPassword(WebRequest webRequest){
        String password  = webRequest.getParameter("password");
        String username  = webRequest.getParameter("username");
        CorporateUser user = corporateUserService.getUserByName(username);
        String message = passwordPolicyService.validate(password, user);

        if (!"".equals(message)){
            return message;
        }
        return "true";
    }
    @PostMapping("/rest/corporate/tokenAuth/username/token")
    public @ResponseBody String tokenAth(WebRequest webRequest){
        try {
            String username = webRequest.getParameter("username");
            String token = webRequest.getParameter("token");
            CorporateUser corporateUser = corporateUserService.getUserByName(username);
            boolean message = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
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
//        Iterator<String> iterator = webRequest.getParameterNames();

//        while(iterator.hasNext()){
//            logger.info(iterator.next());
//        }
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

                    CorporateUser user = corporateUserService.getUserByName(userName);

                    if(user == null){
                        return "false";
                    }

                    Context context = new Context();
                    context.setVariable("fullName", user.getFirstName()+" "+user.getLastName());
                    context.setVariable("username", user.getUserName());

                    Email email = new Email.Builder()
                            .setRecipient(user.getEmail())
                            .setSubject(messageSource.getMessage("retrieve.username.subject",null,locale))
                            .setTemplate("mail/usernameretrieval")
                            .build();
                    mailService.sendMail(email,context);
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

    @PostMapping("/rest/corporate/email/corporateId")
    public @ResponseBody String[] getAccountNameFromNumber(WebRequest webRequest){
        String email = webRequest.getParameter("email");
        String corporateId = webRequest.getParameter("corporateId");
//        logger.info("corporateId {} email {}",corporateId,email);
        String customerId = "";
        String[] userDetails =  new String[5];
        userDetails[0] = "";
        userDetails[1] = "";
//        Account account = accountService.getAccountByAccountNumber(corporateId);
//        logger.info("this is the acc corpp {}", corporateId);
//            customerId = account.getCustomerId();
            Corporate corporate = corporateService.getCorporateByCorporateId(corporateId);
//        logger.info("this is the acc corporate{}", corporate);

        if(corporate != null) {
                CorporateUser corporateUser = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, email);
                if(corporateUser != null){
                if (corporateUser.getEntrustGroup() != null && corporateUser.getEntrustId() != null) {
                    userDetails[0] = corporateUser.getEntrustId();
                    userDetails[1] = corporateUser.getEntrustGroup();
                    userDetails[2] = corporateUser.getFirstName();
                    userDetails[3] = corporateUser.getUserName();
                    userDetails[4] = corporateUser.getIsFirstTimeLogon();
                }
//                logger.info("Cid id : " + customerId);
//                logger.info("entrust id {} entrust group {} ", corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
                }
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
//                    logger.info("questions {}", question);

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
//            logger.info("security question exception {}",e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }
        return question;
    }
    @GetMapping("/rest/corporate/validate/secAns/{acctDetails}")
    public @ResponseBody String getSecAnsByCustomerId(@PathVariable String[] acctDetails, WebRequest webRequest, HttpSession session){
        try{
            //confirm security question is correct
            int noOfMismatch = 0;
//            logger.info("answer 1 {}",webRequest.getParameter("secAnswers"));
//            logger.info("acctDetails {}",acctDetails);
            List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
//            Map<String, List<String>> qa = securityService.getUserQA(acctDetails[0], acctDetails[1]);
            if(session.getAttribute("corpSecQestnAndAns") !=null) {
                Map<String, List<String>> qa = (Map<String, List<String>>) session.getAttribute("corpSecQestnAndAns");
                //List<String> sec = null;
                if (qa != null) {
                    List<String> answer = qa.get("answers");
//                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");

                    if (compareAnswers(answers, answer).equalsIgnoreCase("true")) {
                        return "true";
                    }
                    ;
                }
            }
            //return (String) session.getAttribute("username");
        }catch (Exception e){
            logger.error("Error validating security questions and answers",e);
            return messageSource.getMessage("sec.ans.failed", null, locale);
        }
        return messageSource.getMessage("sec.ans.failed", null, locale);
    }

    @PostMapping("/rest/corp/password")
    public @ResponseBody String checkRegPassword(WebRequest webRequest){
        try {
            String password =webRequest.getParameter("password");
            String message = passwordPolicyService.validate(password, null);
            if (!"".equals(message)){
                return message;
            }
            return "true";
        }catch (InternetBankingException e){
            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
            return e.getMessage();
        }
    }
}
