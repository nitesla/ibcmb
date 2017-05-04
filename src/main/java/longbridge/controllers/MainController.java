package longbridge.controllers;

import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RetailUserService retailUserService;
    
    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = {"/", "/home"})
    public String getHomePage() {
        return "index";
    }

    @RequestMapping(value = "/login/retail", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("retaillogin", "error", error);
    }

    @GetMapping(value = "/login/admin")
    public ModelAndView adminLogin(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admlogin");
        return modelAndView;
    }
    @GetMapping(value = "/login/ops")
    public ModelAndView opsLogin(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opslogin");
        return modelAndView;
    }

    @RequestMapping("/admin/dashboard")
    public String getAdminDashboard() {
        return "adm/dashboard";
    }

    @RequestMapping("/ops/dashboard")
    public String getOpsDashboard() {
        return "ops/dashboard";
    }



    @GetMapping("/forgot/username")
    public String showForgotUsername(){
        return "cust/forgotusername";
    }

    @PostMapping("/forgot/username")
    public @ResponseBody String forgotUsername(WebRequest webRequest){
        String accountNumber = webRequest.getParameter("accountNumber");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");
        
        String username = retailUserService.retrieveUsername(accountNumber, securityQuestion, securityAnswer);
        logger.info("Username is: {}", username);
        return username;
    }
    
   
    
    @GetMapping("/token/synchronize")
    public String synchronizeTokenView(){
    	return "cust/settings/synchronizetoken"; 
	}	
    
    @PostMapping("/token/synchronize")
    public String synchronizeToken(WebRequest webRequest, RedirectAttributes redirectAttributes){
    	String username = webRequest.getParameter("username");
    	//TODO
    	try{
    		securityService.synchronizeToken(username);
        	redirectAttributes.addFlashAttribute("message","Synchronize Token successful");
    	}catch(Exception exc){
    		logger.error("Error", exc);
    		redirectAttributes.addFlashAttribute("message", "Synchronize Token failed");
    	}
    	return "redirect:/token/synchronize";
    }
    
    @PostMapping("/token/authenticate")
    public @ResponseBody String performTokenAuthentication(WebRequest webRequest, HttpServletResponse webResponse){
    	String username = webRequest.getParameter("username");
    	String tokenString = webRequest.getParameter("tokenString");
    	//TODO
    	boolean result = securityService.performTokenValidation(username, tokenString);
    	webResponse.addHeader("contentType", "application/json");
    	return "{'success': "+ result + "}";
    }
    
    @GetMapping("/faqs")
    public String viewFAQs(){		
    	return "cust/faqs"; //TODO
    } 
    
    @GetMapping("/forgot/password")
    public String showResetPassword(){
    	return "cust/passwordreset";
    }
    
    @PostMapping("/forgot/password")
    public String resetPassword(WebRequest webRequest,  RedirectAttributes redirectAttributes){
    	Iterator<String> iterator = webRequest.getParameterNames();
    	
    	while(iterator.hasNext()){
    		logger.info(iterator.next());
    	}
    	
    	String accountNumber = webRequest.getParameter("acct");
    	String securityQuestion = webRequest.getParameter("securityQuestion");
    	String securityAnswer = webRequest.getParameter("securityAnswer");
    	String password= webRequest.getParameter("password");
    	String confirmPassword = webRequest.getParameter("confirm");
    	
    	//confirm passwords are the same
    	boolean isValid = password.equals(confirmPassword);
    	
    	if(isValid){
    		logger.error("Passwords do not match");
    	}
    	
    	String username = retailUserService.retrieveUsername(accountNumber, securityQuestion, securityAnswer);
        RetailUser retailUser = retailUserService.getUserByName(username);

        //confirm security question is correct
    	isValid &= securityService.validateSecurityQuestion(retailUser, securityQuestion, securityAnswer);
    	if(isValid){
    		logger.error("Invalid security question / answer");
    	}
    	//change password	
    	retailUserService.resetPassword(retailUser, password);
    	redirectAttributes.addAttribute("success", true);
    	
    	return "cust/passwordreset";
    }

}