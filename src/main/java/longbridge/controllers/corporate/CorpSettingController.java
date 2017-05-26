package longbridge.controllers.corporate;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.forms.AlertPref;
import longbridge.forms.ChangePassword;
import longbridge.models.CorporateUser;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.CorporateUserService;
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


@Controller
@RequestMapping("/corporate")
public class CorpSettingController {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CodeService codeService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("/dashboard")
    public String getCorporateDashboard(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<AccountDTO> accountList = accountService.getAccountsForDebitAndCredit(corporateUser.getCorporate().getCustomerId());
        model.addAttribute("accountList", accountList);
        return "corp/dashboard";
    }

    @GetMapping("/change_password")
    public String ChangePaswordPage(ChangePassword changePassword)
    {
        return "corp/settings/pword";

    }

   @PostMapping("/change_password")
    public String ChangePassword(@Valid ChangePassword changePassword, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("message","please provide the appropriate input");
            return "redirect:/corporate/change_password";
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())){
            logger.info("PASSWORD MISMATCH");
            return "redirect:/corporate/change_password";
        }

        CorporateUserDTO user = corporateUserService.getUserDTOByName(principal.getName());

        corporateUserService.changePassword(user, changePassword.getOldPassword(), changePassword.getNewPassword());

        redirectAttributes.addFlashAttribute("message","Password change successful");
        return "redirect:/corporate/change_password";
    }


    @GetMapping("/alert_preference")
    public String AlertPreferencePage(AlertPref alertPref, Model model){
        Iterable<CodeDTO> pref = codeService.getCodesByType("ALERT_PREFERENCE");
        model.addAttribute("prefs", pref);
        return "corp/settings/alertpref";
    }

    @PostMapping("/alert_preference")
    public String ChangeAlertPreference(@Valid AlertPref alertPref, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "redirect:/corporate/alert_preference";
        }

        CorporateUserDTO user = corporateUserService.getUserDTOByName(principal.getName());

        corporateUserService.changeAlertPreference(user, alertPref);

        redirectAttributes.addFlashAttribute("message","Preference Change Successful successful");
        return "redirect:/corporate/alert_preference";
    }

    @GetMapping("/bvn")
    public String linkBVN(){
        return "abc";
    }

}
