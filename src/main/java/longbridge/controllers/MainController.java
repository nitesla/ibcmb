package longbridge.controllers;

import org.springframework.stereotype.Controller;
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
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("login", "error", error);
    }


    @RequestMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";
    }

    @RequestMapping("/account")
    public String getAdminUser() {
        return "admin/add";
    }
}
