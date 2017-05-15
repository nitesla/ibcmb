package longbridge.controllers.retail;

import longbridge.api.AccountDetails;
import longbridge.dtos.AccountDTO;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
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
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RetailUserService retailUserService;
    
    @Autowired
    private IntegrationService integrationService;

    private Long customizeAccountId;

    @GetMapping
    public String listAccounts(){
        return "cust/account/index";
    }
    @GetMapping("{id}/view")
    public String viewAccount(@PathVariable Long id, Model model){
        //fetch account details from Account Service
    	AccountDTO accountDTO = accountService.getAccount(id);
        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());

        if(account == null){
        	//account not found
        	return "redirect:/retail/account";
        }
        //send account to frontend
        model.addAttribute("account", account);
        return "cust/account/details";
    }


    @GetMapping("/customize")
    public String CustomizeAccountHome(Model model, Principal principal){
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
        model.addAttribute("accounts", accounts);
        return "cust/account/customizehome";
    } 

    @GetMapping("/{id}/customize")
    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal, Model model, RedirectAttributes redirectAttributes){

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        AccountDTO accountDTO = accountService.getAccount(id);
        if (!retailUser.getCustomerId().equals(accountDTO.getCustomerId())){
            redirectAttributes.addFlashAttribute("message", "Access Denied");
            return "redirect:/retail/logout";
        }

        this.customizeAccountId = accountDTO.getId();
        model.addAttribute("account", accountDTO.getAccountName());
        return "cust/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result, Model model)throws Exception{
        if (result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "cust/account/customize";
        }

        accountService.customizeAccount(this.customizeAccountId, customizeAccount.getAccountName());

        return "redirect:/retail/account/customize";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal){
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
        model.addAttribute("accounts", accounts);
        return "cust/account/setting";
    }

    @GetMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Model model, Principal principal){

        accountService.hideAccount(id);


        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> account = accountService.getAccounts(user.getCustomerId());
        model.addAttribute("accounts", account);
        return "redirect:/retail/account/settings";
    }

    @GetMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Model model, Principal principal){

        accountService.unhideAccount(id);


        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
        model.addAttribute("accounts", accounts);
        return "redirect:/retail/account/settings";
    }

    @GetMapping("/{id}/primary")
    public String makePrimary(@PathVariable Long id, Model model, Principal principal){

        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<Account> accounts = accountService.getCustomerAccounts(user.getCustomerId());
        //Account accounts = accountService.getAccountByCustomerId(user.getCustomerId());
        accountService.makePrimaryAccount(id, user.getCustomerId());


        model.addAttribute("accounts", accounts);
        return "redirect:/retail/account/settings";
    }

    @GetMapping("/officer")
    public String getAccountOfficer(){
        return "cust/account/officer";
    }
}
