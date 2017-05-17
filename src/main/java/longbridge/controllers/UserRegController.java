package longbridge.controllers;

import longbridge.api.CustomerDetails;
import longbridge.dtos.RetailUserDTO;
import longbridge.forms.ResetPasswordForm;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/register")
    public String registerPage(){
        return "cust/register/registration";
    }

    @PostMapping("/register")
    public String addUser(@ModelAttribute("requestForm") RetailUserDTO retailUserDTO, BindingResult result, Model model, WebRequest webRequest){
        if(result.hasErrors()){
            return "cust/servicerequest/add";
        }


        logger.info(retailUserDTO.toString());
        String accountNumber = webRequest.getParameter("acct");
        String email = webRequest.getParameter("email");
        String dob = webRequest.getParameter("birthDate");

        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, dob);
        retailUserService.addUser(retailUserDTO, details);
        model.addAttribute("success", "Registration successful");
        return "redirect:/retail/requests";
    }
    
    @GetMapping("/rest/json/phishingimages")
    public @ResponseBody String antiPhishingImages(){
    	StringBuilder builder = new StringBuilder();
    	builder.append("<option value=''>Select Anti Phishing Image</option>");
    	builder.append("<option value='/assets/phishing/dog.jpg'>Dog</option>");
    	builder.append("<option value='/assets/phishing/cheetah.jpg'>Cheetah</option>");
    	builder.append("<option value='/assets/phishing/benz.jpg'>Car</option>");
    	return builder.toString();
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

        if (customerId == "" || customerId == null){
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

        //get Retail User by customerId
        RetailUser retailUser = retailUserService.getUserByCustomerId(customerId);
        //change password
        retailUserService.resetPassword(retailUser, password);
        redirectAttributes.addAttribute("success", true);

        return "true";

    }

}
