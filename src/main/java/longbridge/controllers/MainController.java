package longbridge.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

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

}
