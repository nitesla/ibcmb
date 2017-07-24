//package longbridge.controllers;
//
//import longbridge.exception.InternetBankingException;
//import longbridge.exception.PasswordException;
//import longbridge.exception.PasswordMismatchException;
//import longbridge.exception.PasswordPolicyViolationException;
//import longbridge.forms.CustResetPassword;
//import longbridge.forms.ResetPasswordForm;
//import longbridge.models.Email;
//import longbridge.models.RetailUser;
//import longbridge.repositories.RetailUserRepo;
//import longbridge.services.*;
//import longbridge.utils.StringUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.mail.MailException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.servlet.http.HttpSession;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
///**
// * Created by Showboy on 20/07/2017.
// */
//@Controller
//public class RetrieveCorpCredentialController {
//    private Locale locale = LocaleContextHolder.getLocale();
//
//    private Logger logger= LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private CorporateUserService corporateUserService;
//
//    @Autowired
//    private CorporateService corporateService;
//
//    @Autowired
//    private SecurityService securityService;
//
//    @Autowired
//    private PasswordPolicyService passwordPolicyService;
//
//    @Autowired
//    private MessageSource messageSource;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private RetailUserRepo retailUserRepo;
//
//    @Autowired
//    private MailService mailService;
//
//    @GetMapping("/forgot/password")
//    public String showResetPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes){
//
//        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
//        resetPasswordForm.step = "1";
//        resetPasswordForm.username = (String) session.getAttribute("username");
//        if ((String) session.getAttribute("username") == null){
//            return "redirect:/login/retail";
//        }
//
//        try{
//            RetailUser retailUser = retailUserService.getUserByName((String) session.getAttribute("username"));
//            Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
//            if (qa != null && !qa.isEmpty()){
//                List<String> questions= qa.get("questions");
//                List<String> answers= qa.get("answers");
//                String secQuestion = questions.get(0);
//                if (questions == null){
//                    redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
//                    return "redirect:/login/retail";
//                }else{
//                    logger.info("the question size {} and values {} ",questions.size(),questions);
//                    session.setAttribute("secretAnswer", answers);
//                    model.addAttribute("secQuestion", questions);
//                    model.addAttribute("noOfQuestion", questions.size());
//                }
//
//            }else {
//                redirectAttributes.addFlashAttribute("failure", "Invalid Credentials");
//                return "redirect:/login/retail";
//            }
//
//
//            model.addAttribute("forgotPasswordForm", resetPasswordForm);
//            List<String> policies = passwordPolicyService.getPasswordRules();
//            model.addAttribute("policies", policies);
//            return "cust/passwordreset";
//        }catch (InternetBankingException e){
//            return "redirect:/login/retail";
//        }
//    }
//
//    @PostMapping("/forgot/password")
//    public @ResponseBody
//    String resetPassword(WebRequest webRequest, RedirectAttributes redirectAttributes, HttpSession session){
//        Iterator<String> iterator = webRequest.getParameterNames();
//
//        while(iterator.hasNext()){
//            logger.info(iterator.next());
//        }
//
//
//        String accountNumber = webRequest.getParameter("acct");
//        String securityQuestion = webRequest.getParameter("securityQuestion");
//        String securityAnswer = webRequest.getParameter("securityAnswer");
//        String password= webRequest.getParameter("password");
//        String confirmPassword = webRequest.getParameter("confirm");
//        String username = (String) session.getAttribute("username");
//
//        if (StringUtils.isBlank(username)){
//            return "false";
//        }
//
//        if ( StringUtils.isBlank(username) ){
//            return "false";
//        }
//
//        //confirm passwords are the same
//        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
//        if(!isValid){
//            logger.error("Passwords do not match");
//            return "false";
//        }
//
//        //if ()
//
//        //get Retail User by username
//        RetailUser retailUser = retailUserService.getUserByName(username);
//        if (retailUser == null){
//            return "false";
//        }
//
//        //change password
//        CustResetPassword custResetPassword = new CustResetPassword();
//        custResetPassword.setNewPassword(password);
//        custResetPassword.setConfirmPassword(confirmPassword);
//        try{
//            String message = retailUserService.resetPassword(retailUser,custResetPassword);
//            redirectAttributes.addAttribute("success", message);
//            return "true";
//        }catch (PasswordPolicyViolationException e){
//            return e.getMessage();
//        }catch (PasswordMismatchException e){
//            return e.getMessage();
//        }catch (PasswordException e){
//            return e.getMessage();
//        }
//    }
//
//    @GetMapping("/rest/secAns")
//    public @ResponseBody String getSecAns(WebRequest webRequest, HttpSession session){
//        try{
//            //confirm security question is correct
//            int noOfMismatch = 0;
//            String username=webRequest.getParameter("username");
//            logger.info("answer 1 {}",webRequest.getParameter("secAnswers"));
//            logger.info("user {}",webRequest.getParameter("username"));
//            List<String> answers = StringUtil.splitByComma(webRequest.getParameter("secAnswers"));
//            RetailUser retailUser = retailUserService.getUserByName(username);
//            Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
//            //List<String> sec = null;
//            logger.info("sec questions {}",qa);
//            if (qa != null){
//                List<String> entAnswers = qa.get("answers");
////                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");
//
//                logger.info("user answer {}", answers);
//                if((answers.size()>0)&&(entAnswers.size()>0)) {
//                    for(int i =0; i<answers.size();i++){
//                        if(!answers.get(i).equalsIgnoreCase(entAnswers.get(i))){
//                            noOfMismatch++;
//                        }
//                    }
//                    logger.info("no of mis match is {}",noOfMismatch);
//                    if(noOfMismatch==0){
//                        return "true";
//                    }
//                }
//            }
//            //return (String) session.getAttribute("username");
//        }catch (Exception e){
//            logger.info(e.getMessage());
//            return messageSource.getMessage("sec.ans.failed", null, locale);
//        }
//        return messageSource.getMessage("sec.ans.failed", null, locale);
//    }
//
//    @GetMapping("/rest/sendGenPass/{username}")
//    public @ResponseBody String sendGenPassword(@PathVariable String username){
//        try {
//            RetailUser retailUser = retailUserService.getUserByName(username);
//            String tempPassword = passwordPolicyService.generatePassword();
//            retailUser.setTempPassword(passwordEncoder.encode(tempPassword));
//            String fullName = retailUser.getFirstName() + " " + retailUser.getLastName();
//            retailUserRepo.save(retailUser);
//            Email email = new Email.Builder()
//                    .setRecipient(retailUser.getEmail())
//                    .setSubject(messageSource.getMessage("reset.password.subject", null, locale))
//                    .setBody(String.format(messageSource.getMessage("reset.password.message", null, locale), fullName, tempPassword))
//                    .build();
//            mailService.send(email);
//            return "true";
//        }catch (MailException me) {
//            return messageSource.getMessage("mail.failure", null, locale);
//        } catch (Exception e){
//            logger.info(e.getMessage());
//            return messageSource.getMessage("reset.send.password.failure", null, locale);
//        }
//
//    }
//
//    @GetMapping("/rest/verGenPass/{username}/{genpassword}")
//    public  @ResponseBody String verifyGenPassword(@PathVariable String username, @PathVariable String genpassword){
//        try {
//            RetailUser retailUser = retailUserService.getUserByName(username);
//            boolean match = passwordEncoder.matches(genpassword, retailUser.getTempPassword());
//            if (match){
//                return "true";
//            }
//            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
//        }catch (InternetBankingException e){
//            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
//        }
//    }
//
//    @GetMapping("/rest/tokenAuth/{username}/{token}")
//    public @ResponseBody String tokenAth(@PathVariable String username, @PathVariable String token){
//        try {
//            RetailUser retailUser = retailUserService.getUserByName(username);
//            boolean message = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
//            if (message){
//                return "true";
//            }
//            return messageSource.getMessage("token.auth.failed", null, locale);
//        }catch (InternetBankingException e){
//            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
//            return e.getMessage();
//        }
//    }
//}
