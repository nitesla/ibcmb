package longbridge.controllers;


import longbridge.exception.UnknownResourceException;
import longbridge.forms.ResetPasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Optional;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = {"/", "/home"})
    public String getHomePage() {
        return "index";
    }

    @RequestMapping(value = "/login/retail", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("retaillogin", "error", error);
    }

    @RequestMapping(value = "/login/corporate", method = RequestMethod.GET)
    public ModelAndView getCorpLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("corplogin", "error", error);
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




    @GetMapping("/faqs")
    public String viewFAQs() {
        return "cust/faqs"; //TODO
    }




    @GetMapping(value = {"/retail/{path:(?!static).*$}","/retail/{path:(?!static).*$}/**" })
    public String retailUnknown(Principal principal){
        if (principal!=null){
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