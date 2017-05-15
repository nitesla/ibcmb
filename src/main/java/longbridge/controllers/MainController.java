package longbridge.controllers;


import longbridge.exception.UnknownResourceException;
import longbridge.forms.ResetPasswordForm;

import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
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
    public ModelAndView adminLogin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admlogin");
        return modelAndView;
    }

    @GetMapping(value = "/login/ops")
    public ModelAndView opsLogin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opslogin");
        return modelAndView;
    }

    @RequestMapping(value = {"/admin/dashboard", "/admin"})
    public String getAdminDashboard() {
        return "adm/dashboard";
    }

    @RequestMapping(value = {"/ops/dashboard", "/ops"})
    public String getOpsDashboard() {
        return "ops/dashboard";
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

    @GetMapping("/faqs")
    public String viewFAQs() {
        return "cust/faqs"; //TODO
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




    @GetMapping(value = {"/retail/{path:(?!static).*$}","/retail/{path:(?!static).*$}/**" })
    public String retailUnknown(Principal principal){
        if (principal!=null){
            System.out.println("YAHOO YAHOO");
            return "redirect:/retail/dashboard";

        }

       throw new UnknownResourceException();
     //   return "";
    }


    @GetMapping(value = {"/admin/{path:(?!static).*$}","/admin/{path:(?!static).*$}/**" })
    public String adminUnknown(Principal principal){
        if (principal!=null){

            return "redirect:/admin/dashboard";

        }

     throw new UnknownResourceException();
       // return "";
    }

    @GetMapping(value = {"/ops/{path:(?!static).*$}","/ops/{path:(?!static).*$}/**" })
    public String opsUnknown(Principal principal){
        if (principal!=null){

            return "redirect:/ops/dashboard";

        }

        throw new UnknownResourceException();
       // return "";
    }


}