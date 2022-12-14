package longbridge.controllers.operations;

import longbridge.dtos.AccountRestrictionDTO;
import longbridge.dtos.CodeDTO;
import longbridge.exception.DuplicateObjectException;
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
 * Created by Longbridge on 08/07/2017.
 */
@Controller
@RequestMapping("/ops/accounts")
public class OpsAccountRestrictionController {
    @Autowired
    private CodeService codeService;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private MessageSource messageSource;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void init(Model model) {
        Iterable<CodeDTO> restrictedFor = codeService.getCodesByType("ACCOUNT_RESTRICTION");
        Iterable<CodeDTO> restrictionType = codeService.getCodesByType("RESTRICTION_TYPE");
        model.addAttribute("restrictedFor", restrictedFor);
        model.addAttribute("restrictionType", restrictionType);

    }

    @GetMapping("/restrictions")
    @ResponseBody
    public Iterable<CodeDTO> getRestrictions() {
        return codeService.getCodesByType("RESTRICTION_TYPE");
    }

    @GetMapping("/restriction/new")
    public String addAccountRestriction(Model model) {

        AccountRestrictionDTO accountRestriction = new AccountRestrictionDTO();
        model.addAttribute("accountRestriction", accountRestriction);
        return "ops/account/restriction/add";
    }

    @PostMapping("/restriction/account")
    public String CreateAccountRestriction(@ModelAttribute("accountRestriction") @Valid AccountRestrictionDTO accountRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "ops/account/restriction/add";
        }

        try {
            String message = accountConfigService.addAccountRestriction(accountRestrictionDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/accounts/restriction/account";
        } catch (DuplicateObjectException exc) {
            logger.error("Could not create account restriction: {}", exc);
            bindingResult.addError(new ObjectError("exception", exc.getMessage()));
            return "ops/account/restriction/add";
        } catch (InternetBankingException e) {
            logger.error("Could not create account restriction", e);
            bindingResult.addError(new ObjectError("exception", e.getMessage()));
            return "ops/account/restriction/add";
        }
    }


    @GetMapping("/restriction/account/{id}/edit")
    public String editAccountRestriction(Model model, @PathVariable Long id) {
        AccountRestrictionDTO accountRestrictionDTO = accountConfigService.getAccountRestriction(id);
        model.addAttribute("accountRestriction", accountRestrictionDTO);
        return "/ops/account/restriction/edit";
    }

    @PostMapping("/restriction/account/update")
    public String updateAccountRestriction(@ModelAttribute("accountRestriction") @Valid AccountRestrictionDTO accountRestrictionDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "ops/account/restriction/edit";
        }
        try {
            String message = accountConfigService.updateAccountRestriction(accountRestrictionDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/accounts/restriction/account";
        }
        catch (DuplicateObjectException exc) {
            logger.error("Could not update account restriction: {}", exc);
            bindingResult.addError(new ObjectError("exception", exc.getMessage()));
            return "ops/account/restriction/edit";
        }
        catch (InternetBankingException ibe) {
            logger.error("Could not update account restriction", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            return "ops/account/restriction/edit";
        }

    }

    @GetMapping("/restriction/account")
    public String getAccountRestrictions(Model model) {
        return "ops/account/restriction/view";
    }

    @GetMapping("/restriction/account/all")
    public
    @ResponseBody
    DataTablesOutput<AccountRestrictionDTO> getAccountRestrictions(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountRestrictionDTO> accountRestrictions = accountConfigService.getAccountRestrictions(pageable);
        DataTablesOutput<AccountRestrictionDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(accountRestrictions.getContent());
        out.setRecordsFiltered(accountRestrictions.getTotalElements());
        out.setRecordsTotal(accountRestrictions.getTotalElements());
        return out;
    }

    @GetMapping("/restriction/account/{id}/delete")
    public String deleteAccountRestriction(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = accountConfigService.deleteAccountRestriction(id);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Could not delete account restriction", ibe);
            redirectAttributes.addFlashAttribute("message",ibe.getMessage());
        }
        return "redirect:/ops/accounts/restriction/account";
    }

}
