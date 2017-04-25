package longbridge.controllers.retail;

import longbridge.dtos.RetailUserDTO;
import longbridge.forms.AlertPref;
import longbridge.forms.ChangePassword;
import longbridge.models.AlertPreference;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("/retail")
public class SettingController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RetailUserService retailUserService;

    @GetMapping("/change_password")
    public String ChangePaswordPage(ChangePassword changePassword){
        return "cust/settings/pword";
    }

    @PostMapping("/change_password")
    public String ChangePassword(@Valid ChangePassword changePassword, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "redirect:/retail/change_password";
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())){
            logger.info("PASSWORD MISMATCH");
            return "redirect:/retail/change_password";
        }

        RetailUserDTO user = retailUserService.getUserByName(principal.getName());

        retailUserService.changePassword(user, changePassword.getOldPassword(), changePassword.getNewPassword());

        redirectAttributes.addFlashAttribute("message","Password change successful");
        return "redirect:/retail/change_password";
    }

    @GetMapping("/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model){
        model.addAttribute("prefs", AlertPreference.values());
        return "cust/settings/alertpref";
    }

    @PostMapping("/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "redirect:/retail/alert_preference";
        }

        RetailUserDTO user = retailUserService.getUserByName(principal.getName());

        retailUserService.changeAlertPreference(user, alertPref.getPreference());

        redirectAttributes.addFlashAttribute("message","Preference Change Successful successful");
        return "redirect:/retail/alert_preference";
    }
}
