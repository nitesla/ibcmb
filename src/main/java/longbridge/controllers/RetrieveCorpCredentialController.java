package longbridge.controllers;

import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.exception.PasswordMismatchException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.forms.CustResetPassword;
import longbridge.forms.ResetPasswordForm;
import longbridge.models.CorporateUser;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.repositories.CorporateUserRepo;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        String username =(String) session.getAttribute("corpUsername");
        String corpKey = (String) session.getAttribute("corpKey");
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.step = "1";
        resetPasswordForm.username = username;
        if (username == null){
            return "redirect:/login/corporate";
        }
        logger.info("the username and corpKey is {} and {}",username,corpKey);

        try{
            CorporateUser corporateUser = corporateUserService.getUserByNameAndCorpCif(username, corpKey);
            logger.info("the corporateUsername group {} and id {}",corporateUser.getEntrustGroup(),corporateUser.getEntrustId());
            Map<String, List<String>> qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
            logger.info("the question and answer {}",qa.get("questions"));
                if (qa != null && !qa.isEmpty()){
                List<String> questions= qa.get("questions");
                logger.info("questions {}",questions);
                logger.info("answers {}",questions);
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
        String username=webRequest.getParameter("username");
        logger.info("answer 1 {}",webRequest.getParameter("secAnswers"));
        logger.info("user {}",webRequest.getParameter("username"));
        List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
        CorporateUser corporateUser = corporateUserService.getUserByName(username);
        Map<String, List<String>> qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
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

        String accountNumber = webRequest.getParameter("acct");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");
        String password= webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String username =(String) session.getAttribute("corpUsername");
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
}
