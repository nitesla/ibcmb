package longbridge.controllers;

import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import longbridge.services.RetailUserService;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RetailUserService retailUserService;

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

    @RequestMapping("/retail/dashboard")
    public String getRetailDashboard() {
        return "cust/dashboard";
    }

    @GetMapping("/retail/forgot/username")
    public String showForgotUsername(){
        return "cust/forgotusername";
    }

    @PostMapping("/retail/forgot/username")
    public @ResponseBody String forgotUsername(WebRequest webRequest){
        String accountNumber = webRequest.getParameter("accountNumber");
        String securityQuestion = webRequest.getParameter("securityQuestion");
        String securityAnswer = webRequest.getParameter("securityAnswer");
        
        String username = retailUserService.retrieveUsername(accountNumber, securityQuestion, securityAnswer);
        logger.info("Username is: {}", username);
        return "Result";
    }

}