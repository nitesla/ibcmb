package longbridge.controllers;

import longbridge.api.CustomerDetails;
import longbridge.dtos.RetailUserDTO;
import longbridge.forms.RegistrationForm;
import longbridge.forms.ResetPasswordForm;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

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

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordPolicyService passwordPolicyService;
    
    @GetMapping("/rest/json/phishingimages")
    public @ResponseBody String antiPhishingImages(){
    	StringBuilder builder = new StringBuilder();
    	builder.append("<option value=''>Select Anti Phishing Image</option>");
    	builder.append("<option value='/assets/phishing/dog.jpg'>Dog</option>");
    	builder.append("<option value='/assets/phishing/cheetah.jpg'>Cheetah</option>");
    	builder.append("<option value='/assets/phishing/benz.jpg'>Car</option>");
    	return builder.toString();
    }

    @GetMapping("/rest/accountdetails/{accountNumber}/{email}/{birthDate}")
    public @ResponseBody String getAccountDetailsFromNumber(@PathVariable String accountNumber, @PathVariable String email, @PathVariable String birthDate){
        String customerId = "";
        logger.info("Account nUmber : " + accountNumber);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        if (details != null){
            customerId = details.getCifId();
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
        }else {
            //nothing
            customerId = "";
        }

        return customerId;
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
            return "false";
        }
        return "true";
    }



    @GetMapping("/forgot/username")
    public String showForgotUsername() {
        return "cust/forgotusername";
    }

    @PostMapping("/forgot/username")
    public
    @ResponseBody
    String forgotUsername(WebRequest webRequest) {
        String accountNumber = webRequest.getParameter("accountNumber");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");

        String username = retailUserService.retrieveUsername(accountNumber, securityQuestion, securityAnswer);
        logger.info("Username is: {}", username);
        return username;
    }


    @GetMapping("/token/synchronize")
    public String synchronizeTokenView() {
        return "cust/settings/synchronizetoken";
    }

    @PostMapping("/token/synchronize")
    public String synchronizeToken(WebRequest webRequest, RedirectAttributes redirectAttributes) {
        String username = webRequest.getParameter("username");
        //TODO
        try {
            securityService.synchronizeToken(username);
            redirectAttributes.addFlashAttribute("message", "Synchronize Token successful");
        } catch (Exception exc) {
            logger.error("Error", exc);
            redirectAttributes.addFlashAttribute("message", "Synchronize Token failed");
        }
        return "redirect:/token/synchronize";
    }

    @PostMapping("/token/authenticate")
    public
    @ResponseBody
    String performTokenAuthentication(WebRequest webRequest, HttpServletResponse webResponse) {
        String username = webRequest.getParameter("username");
        String tokenString = webRequest.getParameter("tokenString");
        //TODO
        boolean result = securityService.performTokenValidation(username, tokenString);
        webResponse.addHeader("contentType", "application/json");
        return "{'success': " + result + "}";
    }


    @GetMapping("/register")
    public String registerPage(Model model){
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.step = "1";
        model.addAttribute("registrationForm", registrationForm);
        return "cust/register/registration";
    }

    @PostMapping("/register")
    public @ResponseBody String addUser(WebRequest webRequest, RedirectAttributes redirectAttributes){
        Iterator<String> iterator = webRequest.getParameterNames();

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }

        String accountNumber = webRequest.getParameter("acct");
        String email = webRequest.getParameter("email");
        String dob = webRequest.getParameter("birthDate");
        String userName = webRequest.getParameter("userName");
        String password = webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String customerId = webRequest.getParameter("customerId");

        logger.info("Customer Id {}:", customerId);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, dob);


        if (details.getCifId() == "" || details.getCifId() == null){
            logger.error("Account Number not valid");
            return "false";
        }


        //confirm passwords are the same
        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
        if(!isValid){
            logger.error("Passwords do not match");
            return "false";
        }

        //password meets policy

        //security questions

        //phishing image


        RetailUserDTO retailUserDTO = new RetailUserDTO();
        retailUserDTO.setUserName(userName);
        retailUserDTO.setEmail(email);
        retailUserDTO.setPassword(password);
        retailUserDTO.setCustomerId(customerId);
        String message = retailUserService.addUser(retailUserDTO, details);
        logger.info("MESSAGE", message);
        redirectAttributes.addAttribute("success", "true");
        return "true";
    }

    @GetMapping("/forgot/password")
    public String showResetPassword(Model model){
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.step = "1";
        model.addAttribute("forgotPasswordForm", resetPasswordForm);
        return "cust/passwordreset";
    }

    @PostMapping("/forgot/password")
    public @ResponseBody  String resetPassword(WebRequest webRequest,  RedirectAttributes redirectAttributes){
        Iterator<String> iterator = webRequest.getParameterNames();

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }


        String accountNumber = webRequest.getParameter("acct");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");
        String password= webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String customerId = webRequest.getParameter("customerId");

        if ("".equals(customerId) || customerId == null){
            logger.error("Account Number not valid");
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
        RetailUser retailUser = retailUserService.getUserByCustomerId(customerId);
        //change password
        retailUserService.resetPassword(retailUser, password);
        redirectAttributes.addAttribute("success", true);

        return "true";

    }

}
