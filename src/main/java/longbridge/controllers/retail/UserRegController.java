package longbridge.controllers.retail;

import longbridge.dtos.RetailUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Showboy on 18/04/2017.
 */
@Controller
@RequestMapping("/register")
public class UserRegController {

    @GetMapping
    public String registerPage(){
        return "cust/register/registration";
    }

    @PostMapping
    public String addUser(@ModelAttribute("requestForm") RetailUserDTO retailUserDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "cust/servicerequest/add";
        }

        model.addAttribute("success", "Request added successfully");
        return "redirect:/retail/requests";
    }
}
