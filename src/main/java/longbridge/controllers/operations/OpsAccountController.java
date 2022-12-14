//package longbridge.controllers.operations;
//
//import longbridge.dtos.AccountClassRestrictionDTO;
//import longbridge.dtos.AccountRestrictionDTO;
//import longbridge.dtos.CodeDTO;
//import longbridge.exception.DuplicateObjectException;
//import longbridge.exception.InternetBankingException;
//import longbridge.services.AccountConfigService;
//import longbridge.services.CodeService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
//import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
//import longbridge.utils.DataTablesUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.ObjectError;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.validation.Valid;
//import java.util.Locale;
//
//
///**
// * Created by Fortune on 4/28/2017.
// */
//
//@Controller
//@RequestMapping("/ops/accounts")
//public class OpsAccountController {
//
//    @Autowired
//    CodeService codeService;
//
//    @Autowired
//    AccountConfigService accountConfigService;
//
//    @Autowired
//    MessageSource messageSource;
//
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @ModelAttribute
//    public void init(Model model) {
//        Iterable<CodeDTO> accountClasses = codeService.getCodesByType("ACCOUNT_CLASS");
//        Iterable<CodeDTO> restrictionTypes = codeService.getCodesByType("RESTRICTION_TYPE");
//        model.addAttribute("accountClasses", accountClasses);
//        model.addAttribute("restrictionTypes", restrictionTypes);
//
//    }
//
package longbridge.controllers.operations;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.AccountConfigService;
import longbridge.services.CodeService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;


/**
 * Created by Fortune on 4/28/2017.
 */

@Controller
@RequestMapping("/ops/accounts")
public class OpsAccountController {

    @Autowired
    CodeService codeService;

    @Autowired
    AccountConfigService accountConfigService;

    @Autowired
    MessageSource messageSource;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void init(Model model) {
        Iterable<CodeDTO> accountClasses = codeService.getCodesByType("ACCOUNT_CLASS");
        Iterable<CodeDTO> restrictionTypes = codeService.getCodesByType("RESTRICTION_TYPE");
        model.addAttribute("accountClasses", accountClasses);
        model.addAttribute("restrictionTypes", restrictionTypes);

    }

//
//    @GetMapping("/restriction/class/new")
//    public String addAccountClassRestriction(Model model) {
//        AccountClassRestrictionDTO accountClassRestriction = new AccountClassRestrictionDTO();
//        model.addAttribute("accountClassRestriction", accountClassRestriction);
//        return "ops/account/restriction/class/add";
//    }
//
//    @PostMapping("/restriction/class")
//    public String CreateAccountClassRestriction(@ModelAttribute("accountClassRestriction") @Valid AccountClassRestrictionDTO accountClassRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
//        if (bindingResult.hasErrors()) {
//            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
//            return "ops/account/restriction/class/add";
//        }
//        try {
//           String message =  accountConfigService.addAccountClassRestriction(accountClassRestrictionDTO);
//            redirectAttributes.addFlashAttribute("message", message);
//            return "redirect:/ops/accounts/restriction/class";
//        } catch (DuplicateObjectException exc) {
//            logger.error("Could not create account class restriction", exc);
//            bindingResult.addError(new ObjectError("exception", exc.getMessage()));
//            return "ops/account/restriction/class/add";
//        } catch (InternetBankingException e) {
//            logger.error("Could not create account class restriction", e);
//            bindingResult.addError(new ObjectError("exception", e.getMessage()));
//            return "ops/account/restriction/class/add";
//        }
//
//    }
//
//
//    @GetMapping("/restriction/class/{id}/edit")
//    public String editAccountClassRestriction(Model model, @PathVariable Long id) {
//        AccountClassRestrictionDTO accountClassRestrictionDTO = accountConfigService.getAccountClassRestriction(id);
//        model.addAttribute("accountClassRestriction", accountClassRestrictionDTO);
//        return "/ops/account/restriction/class/edit";
//    }
//
//    @PostMapping("/restriction/class/update")
//    public String updateAccountClassRestriction(@ModelAttribute("accountClassRestriction") @Valid AccountClassRestrictionDTO accountClassRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
//        if (bindingResult.hasErrors()) {
//            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
//            return "ops/account/restriction/class/edit";
//        }
//        try {
//           String message =  accountConfigService.updateAccountClassRestriction(accountClassRestrictionDTO);
//            redirectAttributes.addFlashAttribute("message", message);
//            return "redirect:/ops/accounts/restriction/class";
//        }
//        catch (DuplicateObjectException exc) {
//            logger.error("Could not update account class restriction", exc);
//            bindingResult.addError(new ObjectError("exception", exc.getMessage()));
//            return "ops/account/restriction/class/edit";
//        }
//        catch (InternetBankingException e) {
//            logger.error("Could not update account class restriction", e);
//            bindingResult.addError(new ObjectError("exception", e.getMessage()));
//            return "ops/account/restriction/class/edit";
//
//        }
//
//    }
//
//    @GetMapping("/restriction/class")
//    public String getAccountClassRestrictions() {
//        return "ops/account/restriction/class/view";
//    }
//
//    @GetMapping("/restriction/class/all")
//    public
//    @ResponseBody
//    DataTablesOutput<AccountClassRestrictionDTO> getAccountClassRestrictions(DataTablesInput input) {
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Page<AccountClassRestrictionDTO> accountClassRestrictions = accountConfigService.getAccountClassRestrictions(pageable);
//        DataTablesOutput<AccountClassRestrictionDTO> out = new DataTablesOutput<AccountClassRestrictionDTO>();
//        out.setDraw(input.getDraw());
//        out.setData(accountClassRestrictions.getContent());
//        out.setRecordsFiltered(accountClassRestrictions.getTotalElements());
//        out.setRecordsTotal(accountClassRestrictions.getTotalElements());
//        return out;
//    }
//
//    @GetMapping("/restriction/class/{id}/delete")
//    public String deleteAccountClassRestriction(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
//        try {
//           String message = accountConfigService.deleteAccountClassRestriction(id);
//            redirectAttributes.addFlashAttribute("message", message);
//
//        } catch (InternetBankingException ibe) {
//            logger.error("Could not delete account class restriction", ibe);
//            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("account.class.restriction.remove", null, locale));
//
//        }
//        return "redirect:/ops/accounts/restriction/class";
//    }
//
//}

    @GetMapping("/restriction/class/new")
    public String addAccountClassRestriction(Model model) {
        AccountClassRestrictionDTO accountClassRestriction = new AccountClassRestrictionDTO();
        model.addAttribute("accountClassRestriction", accountClassRestriction);
        return "ops/account/restriction/class/add";
    }

    @PostMapping("/restriction/class")
    public String CreateAccountClassRestriction(@ModelAttribute("accountClassRestriction") @Valid AccountClassRestrictionDTO accountClassRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "ops/account/restriction/class/add";
        }
        try {
           String message =  accountConfigService.addAccountClassRestriction(accountClassRestrictionDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/accounts/restriction/class";
        } catch (InternetBankingException exc) {
            logger.error("Could not create account class restriction", exc);
            bindingResult.addError(new ObjectError("exception", exc.getMessage()));
            return "ops/account/restriction/class/add";
        }

    }


    @GetMapping("/restriction/class/{id}/edit")
    public String editAccountClassRestriction(Model model, @PathVariable Long id) {
        AccountClassRestrictionDTO accountClassRestrictionDTO = accountConfigService.getAccountClassRestriction(id);
        model.addAttribute("accountClassRestriction", accountClassRestrictionDTO);
        return "/ops/account/restriction/class/edit";
    }

    @PostMapping("/restriction/class/update")
    public String updateAccountClassRestriction(@ModelAttribute("accountClassRestriction") @Valid AccountClassRestrictionDTO accountClassRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "ops/account/restriction/class/edit";
        }
        try {
           String message =  accountConfigService.updateAccountClassRestriction(accountClassRestrictionDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/accounts/restriction/class";
        } catch (InternetBankingException exc) {
            logger.error("Could not update account class restriction", exc);
            bindingResult.addError(new ObjectError("exception", exc.getMessage()));
            return "ops/account/restriction/class/edit";
        }

    }

    @GetMapping("/restriction/class")
    public String getAccountClassRestrictions() {
        return "ops/account/restriction/class/view";
    }

    @GetMapping("/restriction/class/all")
    public
    @ResponseBody
    DataTablesOutput<AccountClassRestrictionDTO> getAccountClassRestrictions(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountClassRestrictionDTO> accountClassRestrictions = accountConfigService.getAccountClassRestrictions(pageable);
        DataTablesOutput<AccountClassRestrictionDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(accountClassRestrictions.getContent());
        out.setRecordsFiltered(accountClassRestrictions.getTotalElements());
        out.setRecordsTotal(accountClassRestrictions.getTotalElements());
        return out;
    }

    @GetMapping("/restriction/class/{id}/delete")
    public String deleteAccountClassRestriction(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
           String message = accountConfigService.deleteAccountClassRestriction(id);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Could not delete account class restriction", ibe);
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("account.class.restriction.remove", null, locale));

        }
        return "redirect:/ops/accounts/restriction/class";
    }

}

