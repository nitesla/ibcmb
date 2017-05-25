package longbridge.controllers.retail;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.RetailUserService;
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
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 * Modified by Wunmi
 */
@Controller
@RequestMapping("/retail")
public class SettingController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CodeService codeService;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("/dashboard")
    public String getRetailDashboard(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<AccountDTO> accountList = accountService.getAccountsForDebitAndCredit(retailUser.getCustomerId());
        model.addAttribute("accountList", accountList);
        return "cust/dashboard";
    }

    @GetMapping("/change_password")
    public String ChangePaswordPage(CustChangePassword custChangePassword){
        return "cust/settings/pword";
    }

    @PostMapping("/change_password")
    public String ChangePassword(@Valid CustChangePassword custChangePassword, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("failure","Pls correct the errors");
            return "cust/settings/pword";
        }

        if(!custChangePassword.getNewPassword().equals(custChangePassword.getConfirmPassword())){
            logger.info("PASSWORD MISMATCH");
            model.addAttribute("failure","Pls correct the errors");
            return "cust/settings/pword";
        }

        RetailUserDTO user = retailUserService.getUserDTOByName(principal.getName());

        retailUserService.changePassword(user, custChangePassword.getOldPassword(), custChangePassword.getNewPassword());

        redirectAttributes.addFlashAttribute("message","Password change successful");
        return "redirect:/retail/change_password";
    }

    @GetMapping("/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model, Principal principal){
        RetailUser user =  retailUserService.getUserByName(principal.getName());
        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertP", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        return "cust/settings/alertpref";
    }

    @PostMapping("/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("failure","Pls correct the errors");
            return "cust/settings/alertpref";
        }

        RetailUserDTO user = retailUserService.getUserDTOByName(principal.getName());
        retailUserService.changeAlertPreference(user, alertPref);

        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("alertP", user.getAlertPreference());
        model.addAttribute("prefs", pref);
        model.addAttribute("message","Preference Change Successful successful");
        return "cust/settings/alertpref";
    }


    @GetMapping("/bvn")
    public String linkBVN(){
        return "abc";
    }
}
