package longbridge.controllers.operations;

import longbridge.dtos.AccountLimitDTO;
import longbridge.dtos.ClassLimitDTO;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.UserType;
import longbridge.services.CodeService;
import longbridge.services.TransactionLimitService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/25/2017.
 */

@Controller
@RequestMapping("/ops/limits")
public class OpsLimitController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CodeService codeService;

    @Autowired
    private TransactionLimitService transactionLimitService;

    @Autowired
    private MessageSource messageSource;


    @ModelAttribute
    public void init(Model model) {
        Iterable<CodeDTO> transferChannels = codeService.getCodesByType("TRANSFER_CHANNEL");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        Iterable<CodeDTO> frequencies = codeService.getCodesByType("FREQUENCY");
        Iterable<CodeDTO> accountClasses = codeService.getCodesByType("ACCOUNT_CLASS");
        model.addAttribute("transferChannels", transferChannels);
        model.addAttribute("currencies", currencies);
        model.addAttribute("frequencies", frequencies);
        model.addAttribute("accountClasses", accountClasses);

    }


    @GetMapping("/retail/class/new")
    public String getRetailClassLimit(Model model) {
        model.addAttribute("classLimit", new ClassLimitDTO());
        return "/ops/limit/retail/class/add";
    }

    @PostMapping("/retail/class")
    public String createRetailClassLimit(@ModelAttribute("classLimit") @Valid ClassLimitDTO classLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/retail/class/add";
        }

        if("".equals(classLimitDTO.getMaxLimit())){
            classLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(classLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/retail/class/add";
            }
        }
        classLimitDTO.setCustomerType(UserType.RETAIL.name());
        try {
            String message= transactionLimitService.addClassLimit(classLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/retail/class";
        }
        catch (DataAccessException exc){
            logger.error("Could not add class limit",exc);
            result.addError(new ObjectError("exception",String.format("The class limit for %s already exists",classLimitDTO.getChannel())));
            return "/ops/limit/retail/class/add";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while adding class limi",e);
            result.addError(new ObjectError("exception","Could not create class retail limit"));
            return "/ops/limit/retail/class/add";        }

    }


    @GetMapping("/retail/class")
    public String viewRetailClassLimits() {
        return "ops/limit/retail/class/view";
    }

    @GetMapping("/retail/class/all")
    public
    @ResponseBody
    DataTablesOutput<ClassLimitDTO> getRetailClassLimits(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        List<ClassLimitDTO> classLimits = transactionLimitService.getRetailClassLimits();
        DataTablesOutput<ClassLimitDTO> out = new DataTablesOutput<ClassLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(classLimits);
        out.setRecordsFiltered(classLimits.size());
        out.setRecordsTotal(classLimits.size());
        return out;
    }

    @GetMapping("/corporate/class")
    public String viewCorporateClassLimits() {
        return "/ops/limit/corporate/class/view";
    }

    @GetMapping("/corporate/class/all")
    public
    @ResponseBody
    DataTablesOutput<ClassLimitDTO> getCorporateClassLimits(DataTablesInput input) {
        List<ClassLimitDTO> classLimits = transactionLimitService.getCorporateClassLimits();
        DataTablesOutput<ClassLimitDTO> out = new DataTablesOutput<ClassLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(classLimits);
        out.setRecordsFiltered(classLimits.size());
        out.setRecordsTotal(classLimits.size());
        return out;
    }


    @GetMapping("/corporate/class/new")
    public String getCorporateClassLimit(Model model) {
        model.addAttribute("classLimit", new ClassLimitDTO());
        return "/ops/limit/corporate/class/add";
    }

    @PostMapping("/corporate/class")
    public String createCorporateClassLimit(@ModelAttribute("classLimit") @Valid ClassLimitDTO classLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/corporate/class/add";
        }

        if("".equals(classLimitDTO.getMaxLimit())){
            classLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(classLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/corporate/class/add";
            }
        }

        classLimitDTO.setCustomerType(UserType.CORPORATE.name());
        try {
           String message = transactionLimitService.addClassLimit(classLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/corporate/class";
        }
        catch (DataAccessException exc){
            logger.error("Could not add class limit",exc);
            result.addError(new ObjectError("exception",String.format("The class limit for %s already exists",classLimitDTO.getChannel())));
            return "/ops/limit/corporate/class/add";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while adding class limit",e);
            result.addError(new ObjectError("exception","Could not create class corporate limit"));
            return "/ops/limit/corporate/class/add";
        }

    }


    @GetMapping("/corporate/class/{id}/edit")
    public String editCorporateClassLimit(Model model, @PathVariable Long id){
        ClassLimitDTO classLimit = transactionLimitService.getCorporateClassLimit(id);
        model.addAttribute("classLimit",classLimit);

        return "/ops/limit/corporate/class/edit";
    }

    @PostMapping("/corporate/class/update")
    public String updateCorporateClassLimit(@ModelAttribute("classLimit") @Valid ClassLimitDTO classLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));

            return "/ops/limit/corporate/class/edit";
        }

        if("".equals(classLimitDTO.getMaxLimit())){
            classLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(classLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/corporate/class/edit";
            }
        }

        try {
            String message = transactionLimitService.updateClassLimit(classLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/corporate/class";
        } catch (InternetBankingException e) {
            logger.error("Exception while updating class limit",e);
            result.addError(new ObjectError("exception","Could not update class corporate limit"));
            return "/ops/limit/corporate/class/edit";
        }

    }

    @GetMapping("/corporate/class/{id}/delete")
    public String deleteCorporateClassLimit(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = transactionLimitService.deleteCorporateClassLimit(id);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to delete class limit",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
        }
        return "redirect:/ops/limits/corporate/class";
    }

    @GetMapping("/retail/class/{id}/edit")
    public String editRetailClassLimit(Model model, @PathVariable Long id){
        ClassLimitDTO classLimit = transactionLimitService.getCorporateClassLimit(id);
        model.addAttribute("classLimit",classLimit);
        return "ops/limit/retail/class/edit";
    }

    @PostMapping("/retail/class/update")
    public String updateRetailClassLimit(@ModelAttribute("classLimit") @Valid ClassLimitDTO classLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/retail/class/edit";
        }

        if("".equals(classLimitDTO.getMaxLimit())){
            classLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(classLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/retail/class/edit";
            }
        }
        try {
            String message = transactionLimitService.updateClassLimit(classLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/retail/class";
        } catch (InternetBankingException e) {
            logger.error("Exception while updating class limit",e);
            result.addError(new ObjectError("exception","Could not update class retail limit"));
            return "/ops/limit/retail/class/edit";
        }


    }

    @GetMapping("/retail/class/{id}/delete")
    public String deleteRetailClassLimit(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = transactionLimitService.deleteCorporateClassLimit(id);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to delete class limit",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
        }
        return "redirect:/ops/limits/retail/class";
    }

    @GetMapping("/retail/account/new")
    public String getRetailAccountLimit(Model model) {
        model.addAttribute("accountLimit", new AccountLimitDTO());
        return "/ops/limit/retail/account/add";
    }

    @PostMapping("/retail/account")
    public String createRetailAccountLimit(@ModelAttribute("accountLimit") @Valid AccountLimitDTO accountLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/retail/account/add";
        }

        if("".equals(accountLimitDTO.getMaxLimit())){
            accountLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(accountLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/retail/account/add";
            }
        }
        accountLimitDTO.setCustomerType(UserType.RETAIL.name());
        try {
            String message = transactionLimitService.addAccountLimit(accountLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/retail/account";
        }
        catch (DataAccessException exc){
            logger.error("Could not add account limit",exc);
            result.addError(new ObjectError("exception",String.format("The account limit for %s already exists",accountLimitDTO.getChannel())));
            return "/ops/limit/retail/account/add";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while adding account limit",e);
            result.addError(new ObjectError("exception","Could not create account retail limit"));
            return "/ops/limit/retail/account/add";        }

    }


    @GetMapping("/retail/account")
    public String viewRetailAccountLimits() {
        return "ops/limit/retail/account/view";
    }

    @GetMapping("/retail/account/all")
    public
    @ResponseBody
    DataTablesOutput<AccountLimitDTO> getRetailAccountLimits(DataTablesInput input) {
        List<AccountLimitDTO> accountLimits = transactionLimitService.getRetailAccountLimits();
        DataTablesOutput<AccountLimitDTO> out = new DataTablesOutput<AccountLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(accountLimits);
        out.setRecordsFiltered(accountLimits.size());
        out.setRecordsTotal(accountLimits.size());
        return out;
    }

    @GetMapping("/corporate/account")
    public String viewCorporateAccountLimits() {
        return "/ops/limit/corporate/account/view";
    }

    @GetMapping("/corporate/account/all")
    public
    @ResponseBody
    DataTablesOutput<AccountLimitDTO> getCorporateAccountLimits(DataTablesInput input) {
        List<AccountLimitDTO> accountLimits = transactionLimitService.getCorporateAccountLimits();
        DataTablesOutput<AccountLimitDTO> out = new DataTablesOutput<AccountLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(accountLimits);
        out.setRecordsFiltered(accountLimits.size());
        out.setRecordsTotal(accountLimits.size());
        return out;
    }


    @GetMapping("/corporate/account/new")
    public String getCorporateAccountLimit(Model model) {
        model.addAttribute("accountLimit", new AccountLimitDTO());
        return "/ops/limit/corporate/account/add";
    }

    @PostMapping("/corporate/account")
    public String createCorporateAccountLimit(@ModelAttribute("accountLimit") @Valid AccountLimitDTO accountLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limits/corporate/account/add";
        }
        if("".equals(accountLimitDTO.getMaxLimit())){
            accountLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(accountLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/corporate/account/add";
            }
        }
        accountLimitDTO.setCustomerType(UserType.CORPORATE.name());
        try {
           String message = transactionLimitService.addAccountLimit(accountLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/corporate/account";
        }
        catch (DataAccessException exc){
            logger.error("Could not add global limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The account limit for %s already exists",accountLimitDTO.getChannel())));
            return "/ops/limit/corporate/account/add";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while adding account limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not create account corporate limit"));
            return "/ops/limit/corporate/account/add";
        }

    }


    @GetMapping("/corporate/account/{id}/edit")
    public String editCorporateAccountLimit(Model model, @PathVariable Long id){
        AccountLimitDTO accountLimit = transactionLimitService.getCorporateAccountLimit(id);
        model.addAttribute("accountLimit",accountLimit);

        return "/ops/limit/corporate/account/edit";
    }

    @PostMapping("/corporate/account/update")
    public String updateCorporateAccountLimit(@ModelAttribute("accountLimit") @Valid AccountLimitDTO accountLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/corporate/account/edit";
        }

        if("".equals(accountLimitDTO.getMaxLimit())){
            accountLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(accountLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/ops/limit/corporate/account/edit";
            }
        }

        try {
            String message = transactionLimitService.addAccountLimit(accountLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/corporate/account";
        } catch (InternetBankingException e) {
            logger.error("Exception while updating account limit",e);
            result.addError(new ObjectError("exception","Could not update account corporate limit"));
            return "/ops/limit/corporate/account/edit";
        }

    }

    @GetMapping("/corporate/account/{id}/delete")
    public String deleteCorporateAccountLimit(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = transactionLimitService.deleteCorporateAccountLimit(id);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to delete account limit",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
        }
        return "redirect:/ops/limits/corporate/account";
    }

    @GetMapping("/retail/account/{id}/edit")
    public String editRetailAccountLimit(Model model, @PathVariable Long id){
        AccountLimitDTO accountLimit = transactionLimitService.getCorporateAccountLimit(id);
        model.addAttribute("accountLimit",accountLimit);
        return "ops/limit/retail/account/edit";
    }



    @PostMapping("/retail/account/update")
    public String updateRetailAccountLimit(@ModelAttribute("accountLimit") @Valid AccountLimitDTO accountLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/retail/account/edit";
        }
        try {
            String message = transactionLimitService.addAccountLimit(accountLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/limits/retail/account";
        } catch (Exception e) {
            logger.error("Exception while updating account limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update account retail limit"));
            return "/ops/limit/retail/account/edit";        }



    }
    @GetMapping("/retail/account/{id}/delete")
    public String deleteRetailAccountLimit(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = transactionLimitService.deleteRetailAccountLimit(id);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to delete account limit",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
        }
        return "redirect:/ops/limits/retail/account";
    }
}
