package longbridge.controllers.corporate;

import longbridge.api.AccountDetails;
import longbridge.dtos.AccountDTO;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;

import longbridge.services.AccountService;
import longbridge.services.CorporateUserService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by SYLVESTER on 4/3/2017.
 */
@Controller
@RequestMapping("/corporate/account")
public class CorpAccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private IntegrationService integrationService;

    private Long customizeAccountId;

    @GetMapping
    public String listAccounts(){
        return "corp/account/index";
    }

    @GetMapping("{id}/view")
    public String viewAccount(@PathVariable Long id, Model model) {
        //fetch account details from Account Service
        AccountDTO accountDTO = accountService.getAccount(id);
        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());

        if(account == null){
            //account not found
            return "redirect:/corporate/account";
        }
        //send account to frontend
        model.addAttribute("account", account);
        return "corp/account/details";

    }


    @GetMapping("/customize")
    public String CustomizeAccountHome(Model model, Principal principal){
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCorporate().getCustomerId());
        model.addAttribute("accounts", accounts);
        return "corp/account/customizehome";
    }

    @GetMapping("/{id}/customize")
    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal, Model model, RedirectAttributes redirectAttributes){

       CorporateUser corporateUser= corporateUserService.getUserByName(principal.getName());

        AccountDTO accountDTO = accountService.getAccount(id);
        if (!corporateUser.getCorporate().getCustomerId().equals(accountDTO.getCustomerId())){
            redirectAttributes.addFlashAttribute("message", "Access Denied");
            return "redirect:/corporate/logout";
        }

        this.customizeAccountId = accountDTO.getId();
        model.addAttribute("account", accountDTO.getAccountName());
        return "corp/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result, Model model)throws Exception{
        if (result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "corp/account/customize";
        }

        accountService.customizeAccount(this.customizeAccountId, customizeAccount.getAccountName());

        return "redirect:/corporate/account/customize";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal){
       CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCorporate().getCustomerId());
        model.addAttribute("accounts", accounts);
        return "corp/account/setting";
    }

    @GetMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Model model, Principal principal){

        accountService.hideAccount(id);


        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> account = accountService.getAccounts(user.getCorporate().getCustomerId());
        model.addAttribute("accounts", account);
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Model model, Principal principal){

        accountService.unhideAccount(id);


        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCorporate().getCustomerId());
        model.addAttribute("accounts", accounts);
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/{id}/primary")
    public String makePrimary(@PathVariable Long id, Model model, Principal principal){

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<Account> accounts = accountService.getCustomerAccounts(user.getCorporate().getCustomerId());
        //Account accounts = accountService.getAccountByCustomerId(user.getCustomerId());
        accountService.makePrimaryAccount(id, user.getCorporate().getCustomerId());


        model.addAttribute("accounts", accounts);
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/officer")
    public String getAccountOfficer(){
        return "corp/account/officer";
    }
}
