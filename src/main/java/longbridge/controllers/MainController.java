package longbridge.controllers;

import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

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
        return "Result";
    }

}