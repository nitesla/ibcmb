package longbridge.controllers;

import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.exception.PasswordMismatchException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.forms.CustResetPassword;
import longbridge.forms.ResetPasswordForm;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.MailService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
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
import java.util.*;

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
                List<String> questions= qa.get("questions");
                List<String> answers= qa.get("answers");
                String secQuestion = questions.get(0);

                if (secQuestion == null || secQuestion.equals("")){
                    redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
                    return "redirect:/login/retail";
                }else{
                    session.setAttribute("secretAnswer", answers);
                    model.addAttribute("secQuestion", secQuestion);
                }

            }else {
                redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
                return "redirect:/login/retail";
            }


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

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }


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

        //if ()

        //get Retail User by username
        RetailUser retailUser = retailUserService.getUserByName(username);
        if (retailUser == null){
            return "false";
        }

        //change password
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

    @GetMapping("/rest/secAns/{answer}")
    public @ResponseBody String getSecAns(@PathVariable String answer, HttpSession session){
        try{
            //confirm security question is correct
            String secAnswer="";
            RetailUser retailUser = retailUserService.getUserByName((String) session.getAttribute("username"));
            Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            //List<String> sec = null;
            if (qa != null){
                List<String> question = qa.get("answers");
                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");
                logger.info("user answer {}", answer);
                logger.info("answer {}", secAnswer);

                if (!secAnswer.equalsIgnoreCase(answer)){
                    return "";
                }else {
                    return "true";
                }

            }else {
                return "";
            }
            //return (String) session.getAttribute("username");
        }catch (Exception e){
            logger.info(e.getMessage());
            return "";
        }
    }

    @GetMapping("/rest/sendGenPass/{username}")
    public @ResponseBody String sendGenPassword(@PathVariable String username){
        try {
            RetailUser retailUser = retailUserService.getUserByName(username);
            String tempPassword = passwordPolicyService.generatePassword();
            retailUser.setTempPassword(passwordEncoder.encode(tempPassword));
            String fullName = retailUser.getFirstName() + " " + retailUser.getLastName();
            retailUserRepo.save(retailUser);
            Email email = new Email.Builder()
                    .setRecipient(retailUser.getEmail())
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

    @GetMapping("/rest/verGenPass/{username}/{genpassword}")
    private @ResponseBody String verifyGenPassword(@PathVariable String username, @PathVariable String genpassword){
        try {
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

}
