package longbridge.controllers;

import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String getRetailDashboard(Model model, Principal principal) {
//        String greeting = "";
//        RetailUser user = retailUserService.getUserByName(principal.getName());
//        model.addAttribute("bvn", user.getBvn());
//
//
//
//        Calendar c = Calendar.getInstance();
//        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
//
//        if(timeOfDay >= 0 && timeOfDay < 12){
//            greeting = "Good morning, " + user.getFirstName() + " " + user.getLastName();
//        }else if(timeOfDay >= 12 && timeOfDay < 16){
//            greeting = "Good afternoon, " + user.getFirstName() + " " + user.getLastName();
//        }else if(timeOfDay >= 16 && timeOfDay < 24){
//            greeting = "Good evening, " + user.getFirstName() + " " + user.getLastName();
//        }
//        model.addAttribute("greeting", greeting);
        return "cust/dashboard";
    }

}
