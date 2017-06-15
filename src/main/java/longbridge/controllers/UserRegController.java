package longbridge.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.CustomerDetails;
import longbridge.dtos.RetailUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustResetPassword;
import longbridge.forms.RegistrationForm;
import longbridge.forms.ResetPasswordForm;
import longbridge.forms.RetrieveUsernameForm;
import longbridge.models.Account;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.models.SecurityQuestions;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Created by Wunmi Sowunmi on 18/04/2017.
 */
@Controller
public class UserRegController {

    @Autowired
    private IntegrationService integrationService;
    
    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MailService mailService;

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CodeService codeService;

    private Locale locale;

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Value("${antiphishingimagepath}")
    private String imagePath;

    @Value("${antiphishingimagepath2}")
    private String fullImagePath;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @GetMapping("/rest/accountdetails/{accountNumber}/{email}/{birthDate}")
    public @ResponseBody String getAccountDetailsFromNumber(@PathVariable String accountNumber, @PathVariable String email, @PathVariable String birthDate){
        String customerId = "";
        logger.info("Account nUmber : " + accountNumber);
        logger.info("Email : " + email);
        logger.info("BirthDate : " + birthDate);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        if (details != null){
              customerId = details.getCifId();
                logger.info("CustomerId", customerId);
//            RetailUser retailUser = retailUserService.getUserByCustomerId(details.getCifId());
//            if (retailUser != null) {
//               customerId = retailUser.getCustomerId();
//            }else {
//                customerId="";
//            }

        }else {
            //nothing
            customerId = "";
        }
        return customerId;
    }

    @GetMapping("/rest/accountexists/{accountNumber}/{email}/{birthDate}")
    public @ResponseBody String validateExists(@PathVariable String accountNumber, @PathVariable String email, @PathVariable String birthDate){
        String customerId = "";
        logger.info("Account nUmber : " + accountNumber);
        logger.info("Email : " + email);
        logger.info("BirthDate : " + birthDate);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        if (details != null){
            RetailUser user = retailUserService.getUserByCustomerId(details.getCifId());
            if (user != null) {
                customerId="";

            }else {
                customerId="does not exsist";
                logger.info("customer is null");
            }

        }else {
            //nothing
            customerId = "";
        }
        return customerId;
    }

    @GetMapping("/rest/retail/accountname/{accountNumber}")
    public @ResponseBody String getAccountNameFromNumber(@PathVariable String accountNumber){
        String customerId = "";
    	logger.info("Account nUmber : " + accountNumber);
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account != null){
            customerId = account.getCustomerId();
            logger.info("Account number : " + customerId);
        }else {
            //nothing
            customerId = "";
        }

        return customerId;
    }

    @GetMapping("/rest/secQues/{cifId}")
    public @ResponseBody String getSecQuestionFromNumber(@PathVariable String cifId, HttpSession session){
        String secQuestion = "";
        logger.info("cifId : " + cifId);

        RetailUser user = retailUserService.getUserByCustomerId(cifId);
        logger.info("USER NAME {}", user);

        if (user != null){
            logger.info("USER NAME {}", user.getUserName());
            session.setAttribute("username", user.getUserName());
            Map<String, List<String>> qa = securityService.getUserQA(user.getUserName());
            //List<String> sec = null;
            if (qa != null || !qa.isEmpty()){
                List<String> question = qa.get("questions");
                secQuestion = question.stream().filter(Objects::nonNull).findFirst().orElse("");
                logger.info("question {}", secQuestion);

            }else {
                secQuestion = "";
            }
        }
        else {
            secQuestion = "";
        }

        return secQuestion;
    }

    @GetMapping("/rest/secAns/{answer}")
    public @ResponseBody String getSecAns(@PathVariable String answer, HttpSession session){

        //confirm security question is correct
        String secAnswer="";
        Map<String, List<String>> qa = securityService.getUserQA((String) session.getAttribute("username"));
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
    }

    @GetMapping("/rest/regCode/{accountNumber}/{email}/{birthDate}")
    public @ResponseBody String sendRegCode(@PathVariable String accountNumber, @PathVariable String email, @PathVariable String birthDate, HttpSession session){
        String code = "";
        logger.info("Account nUmber : " + accountNumber);
        logger.info("Email : " + email);
        logger.info("BirthDate : " + birthDate);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        if (details != null){
            logger.info("Reg Code : " + details);
            String contact = details.getPhone();
            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            logger.info("Reg Code : " + n);
            String message = "Your Registration Code is : ";
            message += n;
            session.setAttribute("regCode", n);

            ObjectNode sent = integrationService.sendSMS(message, contact +
                    "" +
                    " ", "Internet Banking Registration Code");
            if (sent != null){


                return String.valueOf(n);
            }

        }else {
            //nothing
            code = "";
        }

        return code;
    }

    @GetMapping("/rest/regCode/check/{code}")
    public @ResponseBody String checkRegCode(@PathVariable Integer code, HttpSession session){
        Integer regCode = (Integer) session.getAttribute("regCode");
        if (!code.equals(regCode)){
            return "false";
        }
        return "true";
    }


    @GetMapping("/rest/username/check/{username}")
    public @ResponseBody String checkUsername(@PathVariable String username){
        RetailUser user = retailUserService.getUserByName(username);
        logger.info("USER RETURNED{}", user);
        if(user == null){
            return "true";
        }
        return "false";
    }

    @GetMapping("/rest/password/check/{password}")
    public @ResponseBody String checkPassword(@PathVariable String password){
        String message = passwordPolicyService.validate(password, null);

        if (!"".equals(message)){
            return message;
        }
        return "true";
    }






    @GetMapping("/forgot/username")
    public String showForgotUsername(Model model) {
        RetrieveUsernameForm retrieveUsernameForm= new RetrieveUsernameForm();
        retrieveUsernameForm.step = "1";
        model.addAttribute("retUsernameForm", retrieveUsernameForm);
        return "cust/forgotusername";
    }

    @PostMapping("/forgot/username")
    public
    @ResponseBody
    String forgotUsername(WebRequest webRequest) {
        Iterator<String> iterator = webRequest.getParameterNames();

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }


        String accountNumber = webRequest.getParameter("acct");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");
        String customerId = webRequest.getParameter("customerId");

        try {
            if ("".equals(customerId) || customerId == null) {
                logger.error("Account Number not valid");
                return "false";
            }

            RetailUser user = retailUserService.getUserByCustomerId(customerId);

            //confirm security question is correct
            String secAnswer="";
            Map<String, List<String>> qa = securityService.getUserQA(user.getUserName());
            //List<String> sec = null;
            if (qa != null){
                List<String> questions= qa.get("questions");
                List<String> answers= qa.get("answers");


                    secAnswer = answers.stream().filter(Objects::nonNull).findFirst().orElse("");
                    logger.info("answerSec {}", secAnswer);


                if (secAnswer.equalsIgnoreCase(securityAnswer)){
                    logger.debug("User Info {}:", user.getUserName());
                    //Send Username to Email
                    Email email = new Email.Builder()
                            .setRecipient(user.getEmail())
                            .setSubject(messageSource.getMessage("retrieve.username.subject",null,locale))
                            .setBody(String.format(messageSource.getMessage("retrieve.username.message",null,locale),user.getFirstName(), user.getUserName()))
                            .build();
                    mailService.send(email);
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


//
//    @PostMapping("/token/authenticate")
//    public
//    @ResponseBody
//    String performTokenAuthentication(WebRequest webRequest, HttpServletResponse webResponse) {
//        String username = webRequest.getParameter("username");
//        String tokenString = webRequest.getParameter("tokenString");
//        //TODO
//        boolean result = securityService.performTokenValidation(username, tokenString);
//        webResponse.addHeader("contentType", "application/json");
//        return "{'success': " + result + "}";
//    }


    @GetMapping("/register")
    public String registerPage(Model model){
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.step = "1";
        model.addAttribute("registrationForm", registrationForm);

        List<String> images = new ArrayList<String>();
        images.add("dog.jpg");
        images.add("cheetah.jpg");
        images.add("benz.jpg");

        model.addAttribute("images", images);
        model.addAttribute("imagePath", imagePath);

        List<String> policies = passwordPolicyService.getPasswordRules();
        model.addAttribute("policies", policies);

        List<SecurityQuestions> securityQuestions = securityQuestionService.getSecQuestions();
        model.addAttribute("secQuestions", securityQuestions);
        return "cust/register/registration";
    }

    @PostMapping("/register")
    public @ResponseBody String addUser(WebRequest webRequest, RedirectAttributes redirectAttributes){
        Iterator<String> iterator = webRequest.getParameterNames();

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }

        String accountNumber = webRequest.getParameter("accountNumber");
        String email = webRequest.getParameter("email");
        String dob = webRequest.getParameter("birthDate");
        String userName = webRequest.getParameter("userName");
        String password = webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String secQuestion = webRequest.getParameter("securityQuestion");
        String secAnswer = webRequest.getParameter("securityAnswer");
        String customerId = webRequest.getParameter("customerId");
        String phishing = webRequest.getParameter("phishing");
        String caption = webRequest.getParameter("caption");

        String bvn ="";
        logger.info("Customer Id {}:", customerId);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, dob);


        if (details.getCifId() == null||details.getCifId().isEmpty() ){
            logger.error("Account Number not valid");
            return "false";
        }

        doesUserExist(customerId);

        if (details.getBvn() != null && !details.getBvn().isEmpty() ){
            logger.error("No Bvn found");
            bvn=details.getBvn();
        }


        //confirm passwords are the same
        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
        if(!isValid){
            logger.error("Passwords do not match");
            return "false";
        }

        //password meets policy


        //security questions
        String securityQuestion = secQuestion;
        String securityAnswer = secAnswer;

        logger.info("Question" + secQuestion);
        logger.info("Answer" + secAnswer);

        //phishing image
        //byte[] encodedBytes = Base64.encodeBase64(phishing.getBytes());
        File image = new File(fullImagePath, phishing);
        Long length = image.length();
       // length <= Integer.MAX_VALUE;
//TODO: check file is not bigger than max int
        byte buffer[] = new byte[length.intValue()];


        try {
            FileInputStream fis = new FileInputStream(image);
                int cnt = fis.read(buffer);
                //ensure cnt == length
        }catch (Exception e){
            //TODO: handle exception
        }

        String encPhishImage = java.util.Base64.getEncoder().encodeToString(buffer);
        logger.info("ENCODED STRING " + encPhishImage);

//        System.out.println("encodedBytes " + new String(encodedBytes));
//        String encPhishImage = new String(encodedBytes);

        RetailUserDTO retailUserDTO = new RetailUserDTO();
        retailUserDTO.setUserName(userName);
        retailUserDTO.setEmail(email);
        retailUserDTO.setPassword(password);
        retailUserDTO.setCustomerId(customerId);
        retailUserDTO.setBvn(bvn);
        retailUserDTO.setSecurityQuestion(securityQuestion);
        retailUserDTO.setSecurityAnswer(securityAnswer);
        retailUserDTO.setPhishingSec(encPhishImage);
        retailUserDTO.setCaptionSec(caption);
        try {
            String message = retailUserService.addUser(retailUserDTO, details);
            logger.info("MESSAGE", message);
            redirectAttributes.addAttribute("success", "true");
            return "true";
        }
        catch (InternetBankingException e){
            logger.error("Error creating retail user", e);
            redirectAttributes.addFlashAttribute(messageSource.getMessage("user.add.failure", null, locale));
        }

        return "false";
    }

    private void doesUserExist(String customerId){
        RetailUser user = retailUserService.getUserByCustomerId(customerId);
        if (user != null){
            throw new InternetBankingException(messageSource.getMessage("user.reg.exists", null, locale));
        }

//        Iterable<AccountDTO> account = accountService.getAccounts(customerId);
//        if (account != null || account.isEmpty){
//            throw new InternetBankingException(messageSource.getMessage("user.reg.exists", null, locale));
//        }
    }

    @GetMapping("/forgot/password")
    public String showResetPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes){

        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.step = "1";
        resetPasswordForm.username = (String) session.getAttribute("username");
        try{
            Map<String, List<String>> qa = securityService.getUserQA((String) session.getAttribute("username"));
            if (qa != null || !qa.isEmpty()){
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
            return "cust/passwordreset";
        }catch (InternetBankingException e){
            return "redirect:/login/retail";
        }
    }

    @PostMapping("/forgot/password")
    public @ResponseBody  String resetPassword(WebRequest webRequest,  RedirectAttributes redirectAttributes, HttpSession session){
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

        if (username.equals("")||username==null){
            return "false";
        }

//    	String username = retailUserService.retrieveUsername(accountNumber, securityQuestion, securityAnswer);
//        RetailUser retailUser = retailUserService.getUserByName(username);

        //confirm security question is correct
//    	isValid &= securityService.validateSecurityQuestion(retailUser, securityQuestion, securityAnswer);
//    	if(isValid){
//    		logger.error("Invalid security question / answer");
//    		return "false";
//    	}


        //confirm passwords are the same
        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
        if(!isValid){
            logger.error("Passwords do not match");
            return "false";
        }

        //if ()

        //get Retail User by customerId
        RetailUser retailUser = retailUserService.getUserByName(username);
        if (retailUser == null){
            return "false";
        }
        //change password
        CustResetPassword custResetPassword = new CustResetPassword();
        custResetPassword.setNewPassword(password);
        custResetPassword.setConfirmPassword(confirmPassword);
        retailUserService.resetPassword(retailUser,custResetPassword);
        redirectAttributes.addAttribute("success", true);

        return "true";

    }

}
