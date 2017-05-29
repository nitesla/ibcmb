package longbridge.controllers.retail;

import longbridge.forms.TokenProp;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by Showboy on 28/05/2017.
 */
@Controller
@RequestMapping("/retail/token")
public class TokenManagementController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    SecurityService securityService;

    @GetMapping("/sync")
    public String syncToken(TokenProp tokenProp, Principal principal, Model model){
        return "cust/token/sync";
    }

    @PostMapping("/sync")
    public String synchroniseToken(@Valid TokenProp tokenProp, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "cust/token/sync";
        }

       // securityService.synchronizeToken(principal.getName());
        return "redirect:/retail/token/sync";
    }


    @GetMapping("/lost")
    public String lostToken(TokenProp tokenProp, Principal principal, Model model){
        return "cust/token/lost";
    }

    @PostMapping("/lost")
    public String blockToken(@Valid TokenProp tokenProp, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "cust/token/lost";
        }

      //  securityService.synchronizeToken(principal.getName());
        return "redirect:/retail/token/lost";
    }

}
