package longbridge.controllers.operations;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.dtos.CodeDTO;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


/**
 * Created by Fortune on 4/28/2017.
 */

@Controller
@RequestMapping("/ops/accounts")
public class OpsAccountController {

    @Autowired
    CodeService codeService;

    @Autowired
    AccountService accountService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void init(Model model){
        Iterable<CodeDTO> accountClasses = codeService.getCodesByType("ACCOUNT_CLASS");
        Iterable<CodeDTO> restrictionTypes = codeService.getCodesByType("RESTRICTION_TYPE");
        model.addAttribute("accountClasses",accountClasses);
        model.addAttribute("restrictionTypes",restrictionTypes);

    }

    @GetMapping("/restrictions")
    @ResponseBody
    public Iterable<CodeDTO> getRestrictions(){
        Iterable<CodeDTO> restrictionTypes = codeService.getCodesByType("RESTRICTION_TYPE");
return restrictionTypes;
    }

    @GetMapping("/restriction/account/new")
    public String addAccountRestriction(Model model) {
        AccountRestrictionDTO accountRestriction = new AccountRestrictionDTO();
        model.addAttribute("accountRestriction",accountRestriction);
        return "ops/account/restriction/account/add";
    }

    @PostMapping("/restriction/account")
    public  String CreateAccountRestriction(@ModelAttribute("accountRestriction") @Valid AccountRestrictionDTO accountRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "ops/account/restriction/account/add";
        }
        try {
            accountService.addAccountRestriction(accountRestrictionDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not create account restriction: {}",exc.toString());
            bindingResult.addError(new ObjectError("exception", String.format("A restriction on the account %s already exists",accountRestrictionDTO.getAccountNumber())));
            return "ops/account/restriction/account/add";
        }
        catch (Exception e) {
            logger.error("Could not create account restriction: {}",e.toString());
            bindingResult.addError(new ObjectError("exception", "Could not create restriction on the account"));
            return "ops/account/restriction/account/add";
        }
        redirectAttributes.addFlashAttribute("message","Account restriction created successfully");
        return "redirect:/ops/accounts/restriction/account";
    }


    @GetMapping("/restriction/account/{id}/edit")
    public String editAccountRestriction(Model model, @PathVariable Long id) {
        AccountRestrictionDTO accountRestrictionDTO = accountService.getAccountRestriction(id);
        model.addAttribute("accountRestriction",accountRestrictionDTO);
        return "/ops/account/restriction/account/edit";
    }

    @PostMapping("/restriction/account/update")
    public  String updateAccountRestriction(@ModelAttribute("accountRestriction") @Valid AccountRestrictionDTO accountRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "ops/account/restriction/account/edit";
        }
        try {
            accountService.updateAccountRestriction(accountRestrictionDTO);
        }
        catch (Exception e) {
            logger.error("Could not update account restriction: {}",e.toString());
            bindingResult.addError(new ObjectError("exception", "Could not update restriction on the account"));
            return "ops/account/restriction/account/edit";
        }
        redirectAttributes.addFlashAttribute("message","Account restriction updated successfully");
        return "redirect:/ops/accounts/restriction/account";
    }

    @GetMapping("/restriction/account")
    public String getAccountRestrictions(Model model){
        return "ops/account/restriction/account/view";
    }

    @GetMapping("/restriction/account/all")
    public @ResponseBody
    DataTablesOutput<AccountRestrictionDTO> getAccountRestrictions(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountRestrictionDTO> accountRestrictions = accountService.getRestrictedAccounts(pageable);
        DataTablesOutput<AccountRestrictionDTO> out = new DataTablesOutput<AccountRestrictionDTO>();
        out.setDraw(input.getDraw());
        out.setData(accountRestrictions.getContent());
        out.setRecordsFiltered(accountRestrictions.getTotalElements());
        out.setRecordsTotal(accountRestrictions.getTotalElements());
        return out;
    }

    @GetMapping("/restriction/account/{id}/remove")
    public String removeAccountRestriction(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        accountService.removeAccountRestriction(id);
        redirectAttributes.addFlashAttribute("message","Account restriction removed successfully");
        return "redirect:/ops/account/restriction/account/remove";
    }

    @GetMapping("/restriction/class/new")
    public String addAccountClassRestriction(Model model) {
        AccountClassRestrictionDTO accountClassRestriction = new AccountClassRestrictionDTO();
        model.addAttribute("accountClassRestriction",accountClassRestriction);
        return "ops/account/restriction/class/add";
    }

    @PostMapping("/restriction/class")
    public  String CreateAccountClassRestriction(@ModelAttribute("accountClassRestriction") @Valid  AccountClassRestrictionDTO accountClassRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "ops/account/restriction/class/add";
        }
        try {
            accountService.addAccountClassRestriction(accountClassRestrictionDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not create account restriction: {}",exc.toString());
            bindingResult.addError(new ObjectError("exception", String.format("A restriction on the Account Class %s already exists",accountClassRestrictionDTO.getAccountClass())));
            return "ops/account/restriction/class/add";
        }
        catch (Exception e) {
            logger.error("Could not create account class restriction: {}",e.toString());
            bindingResult.addError(new ObjectError("exception", "Could not create restriction on the Account Class"));
            return "ops/account/restriction/class/add";
        }
        redirectAttributes.addFlashAttribute("message","Account Class restriction created successfully");
        return "redirect:/ops/accounts/restriction/class";
    }


    @GetMapping("/restriction/class/{id}/edit")
    public String editAccountClassRestriction(Model model, @PathVariable Long id) {
        AccountClassRestrictionDTO accountClassRestrictionDTO = accountService.getAccountClassRestriction(id);
        model.addAttribute("accountClassRestriction",accountClassRestrictionDTO);
        return "/ops/account/restriction/class/edit";
    }

    @PostMapping("/restriction/class/update")
    public  String updateAccountClassRestriction(@ModelAttribute("accountClassRestriction") @Valid AccountClassRestrictionDTO accountClassRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "ops/account/restriction/class/edit";
        }
        try {
            accountService.updateAccountClassRestriction(accountClassRestrictionDTO);
        }
        catch (Exception e) {
            logger.error("Could not update account class restriction: {}",e.toString());
            bindingResult.addError(new ObjectError("exception", "Could not update restriction on the Account Class"));
            return "ops/account/restriction/class/edit";
        }
        redirectAttributes.addFlashAttribute("message","Account Class restriction updated successfully");
        return "redirect:/ops/accounts/restriction/class";
    }

    @GetMapping("/restriction/class")
    public String getAccountClassRestrictions(Model model){
        return "ops/account/restriction/class/view";
    }

    @GetMapping("/restriction/class/all")
    public @ResponseBody
    DataTablesOutput<AccountClassRestrictionDTO> getAccountClassRestrictions(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountClassRestrictionDTO> accountClassRestrictions = accountService.getRestrictedAccountClasses(pageable);
        DataTablesOutput<AccountClassRestrictionDTO> out = new DataTablesOutput<AccountClassRestrictionDTO>();
        out.setDraw(input.getDraw());
        out.setData(accountClassRestrictions.getContent());
        out.setRecordsFiltered(accountClassRestrictions.getTotalElements());
        out.setRecordsTotal(accountClassRestrictions.getTotalElements());
        return out;
    }

    @GetMapping("/restriction/class/{id}/remove")
    public String removeAccountClassRestriction(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        accountService.removeAccountClassRestriction(id);
        redirectAttributes.addFlashAttribute("message","Account Class restriction removed successfully");
        return "redirect:/ops/accounts/restriction/class/remove";
    }

}
