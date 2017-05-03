package longbridge.controllers.operations;

import longbridge.dtos.AccountLimitDTO;
import longbridge.dtos.ClassLimitDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.models.UserType;
import longbridge.services.CodeService;
import longbridge.services.TransactionLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
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
    CodeService codeService;

    @Autowired
    TransactionLimitService transactionLimitService;

    @Autowired
    MessageSource messageSource;


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

    @GetMapping("/retail/global/new")
    public String getRetailGlobalLimit(Model model) {
        model.addAttribute("globalLimit", new GlobalLimitDTO());
        return "/ops/limit/retail/global/add";
    }

    @PostMapping("/retail/global")
    public String createRetailGlobalLimit(@ModelAttribute("globalLimit") @Valid GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));

            return "/ops/limit/retail/global/add";
        }
        globalLimitDTO.setCustomerType(UserType.RETAIL.name());


        try {
            transactionLimitService.addGlobalLimit(globalLimitDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not add global limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The global limit for %s already exists",globalLimitDTO.getChannel())));
            return "/ops/limit/retail/global/add";
        }
        catch (Exception e) {
            logger.error("Exception while adding global limit");
            result.addError(new ObjectError("exception","Could not create global retail limit: "+e.getMessage()));
            return "/ops/limit/retail/global/add";
        }
        redirectAttributes.addFlashAttribute("message", "Retail Global limit created successfully");
        return "redirect:/ops/limits/retail/global";
    }


    @GetMapping("/retail/global")
    public String viewRetailGlobalLimits() {
        return "ops/limit/retail/global/view";
    }

    @GetMapping("/retail/global/all")
    public
    @ResponseBody
    DataTablesOutput<GlobalLimitDTO> getRetailGlobalLimits(DataTablesInput input) {
        List<GlobalLimitDTO> globalLimits = transactionLimitService.getRetailGlobalLimits();
        DataTablesOutput<GlobalLimitDTO> out = new DataTablesOutput<GlobalLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(globalLimits);
        out.setRecordsFiltered(globalLimits.size());
        out.setRecordsTotal(globalLimits.size());
        return out;
    }

    @GetMapping("/corporate/global")
    public String viewCorporateGlobalLimits() {
        return "/ops/limit/corporate/global/view";
    }

    @GetMapping("/corporate/global/all")
    public
    @ResponseBody
    DataTablesOutput<GlobalLimitDTO> getCorporateGlobalLimits(DataTablesInput input) {
        List<GlobalLimitDTO> globalLimits = transactionLimitService.getCorporateGlobalLimits();
        DataTablesOutput<GlobalLimitDTO> out = new DataTablesOutput<GlobalLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(globalLimits);
        out.setRecordsFiltered(globalLimits.size());
        out.setRecordsTotal(globalLimits.size());
        return out;
    }


    @GetMapping("/corporate/global/new")
    public String getCorporateGlobalLimit(Model model) {
        model.addAttribute("globalLimit", new GlobalLimitDTO());
        return "/ops/limit/corporate/global/add";
    }

    @PostMapping("/corporate/global")
    public String createCorporateGlobalLimit(@ModelAttribute("globalLimit") @Valid  GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limits/corporate/global/add";
        }
        globalLimitDTO.setCustomerType(UserType.CORPORATE.name());
        try {
            transactionLimitService.addGlobalLimit(globalLimitDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not add global limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The global limit for %s already exists",globalLimitDTO.getChannel())));
            return "/ops/limit/corporate/global/add";
        }
        catch (Exception e) {
            logger.error("Exception while adding global limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not create global corporate limit"));
            return "/ops/limit/corporate/global/add";
        }
        redirectAttributes.addFlashAttribute("message", "Corporate global limit created successfully");
        return "redirect:/ops/limits/corporate/global";
    }


    @GetMapping("/corporate/global/{id}/edit")
    public String editCorporateGlobalLimit(Model model, @PathVariable Long id){
        GlobalLimitDTO globalLimit = transactionLimitService.getCorporateGlobalLimit(id);
         model.addAttribute("globalLimit",globalLimit);

        return "/ops/limit/corporate/global/edit";
    }

    @PostMapping("/corporate/global/update")
    public String updateCorporateGlobalLimit(@ModelAttribute("globalLimit") @Valid GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/corporate/global/edit";
        }

        try {
            transactionLimitService.addGlobalLimit(globalLimitDTO);
        }
        catch (Exception e) {
            logger.error("Exception while updating global limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update global corporate limit"));
            return "/ops/limit/corporate/global/edit";
        }
        redirectAttributes.addFlashAttribute("message", "Corporate global limit updated successfully");
        return "redirect:/ops/limits/corporate/global";
    }

    @GetMapping("/retail/global/{id}/edit")
    public String editRetailGlobalLimit(Model model, @PathVariable Long id){
        GlobalLimitDTO globalLimit = transactionLimitService.getCorporateGlobalLimit(id);
        model.addAttribute("globalLimit",globalLimit);
        return "ops/limit/retail/global/edit";
    }

    @PostMapping("/retail/global/update")
    public String updateRetailGlobalLimit(@ModelAttribute("globalLimit") @Valid GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/limit/retail/global/edit";
        }
        try {
            transactionLimitService.addGlobalLimit(globalLimitDTO);
        } catch (Exception e) {
            logger.error("Exception while updating global limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update global retail limit"));
            return "/ops/limit/retail/global/edit";        }

        redirectAttributes.addFlashAttribute("message", "Retail global limit updated successfully");
        return "redirect:/ops/limits/retail/global";
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
        classLimitDTO.setCustomerType(UserType.RETAIL.name());
        try {
            transactionLimitService.addClassLimit(classLimitDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not add class limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The class limit for %s already exists",classLimitDTO.getChannel())));
            return "/ops/limit/retail/class/add";
        }
        catch (Exception e) {
            logger.error("Exception while adding class limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not create class retail limit"));
            return "/ops/limit/retail/class/add";        }
        redirectAttributes.addFlashAttribute("message", "Retail Class limit created successfully");
        return "redirect:/ops/limits/retail/class";
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
            return "/ops/limits/corporate/class/add";
        }
        classLimitDTO.setCustomerType(UserType.CORPORATE.name());
        try {
            transactionLimitService.addClassLimit(classLimitDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not add class limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The class limit for %s already exists",classLimitDTO.getChannel())));
            return "/ops/limit/corporate/class/add";
        }
        catch (Exception e) {
            logger.error("Exception while adding class limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not create class corporate limit"));
            return "/ops/limit/corporate/class/add";
        }
        redirectAttributes.addFlashAttribute("message", "Corporate class limit created successfully");
        return "redirect:/ops/limits/corporate/class";
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

        try {
            transactionLimitService.addClassLimit(classLimitDTO);
        } catch (Exception e) {
            logger.error("Exception while updating class limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update class corporate limit"));
            return "/ops/limit/corporate/class/edit";
        }
        redirectAttributes.addFlashAttribute("message", "Corporate class limit updated successfully");
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
        try {
            transactionLimitService.addClassLimit(classLimitDTO);
        } catch (Exception e) {
            logger.error("Exception while updating class limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update class retail limit"));
            return "/ops/limit/retail/class/edit";
        }

        redirectAttributes.addFlashAttribute("message", "Retail class limit updated successfully");
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
        accountLimitDTO.setCustomerType(UserType.RETAIL.name());
        try {
            transactionLimitService.addAccountLimit(accountLimitDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not add account limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The account limit for %s already exists",accountLimitDTO.getChannel())));
            return "/ops/limit/retail/account/add";
        }
        catch (Exception e) {
            logger.error("Exception while adding account limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not create account retail limit"));
            return "/ops/limit/retail/account/add";        }
        redirectAttributes.addFlashAttribute("message", "Retail Account limit created successfully");
        return "redirect:/ops/limits/retail/account";
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
        accountLimitDTO.setCustomerType(UserType.CORPORATE.name());
        try {
            transactionLimitService.addAccountLimit(accountLimitDTO);
        }
        catch (DataAccessException exc){
            logger.error("Could not add global limit: {}",exc.toString());
            result.addError(new ObjectError("exception",String.format("The account limit for %s already exists",accountLimitDTO.getChannel())));
            return "/ops/limit/corporate/account/add";
        }
        catch (Exception e) {
            logger.error("Exception while adding account limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not create account corporate limit"));
            return "/ops/limit/corporate/account/add";
        }
        redirectAttributes.addFlashAttribute("message", "Corporate account limit created successfully");
        return "redirect:/ops/limits/corporate/account";
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

        try {
            transactionLimitService.addAccountLimit(accountLimitDTO);
        } catch (Exception e) {
            logger.error("Exception while updating account limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update account corporate limit"));
            return "/ops/limit/corporate/account/edit";
        }
        redirectAttributes.addFlashAttribute("message", "Corporate account limit updated successfully");
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
            transactionLimitService.addAccountLimit(accountLimitDTO);
        } catch (Exception e) {
            logger.error("Exception while updating account limit: {}",e.toString());
            result.addError(new ObjectError("exception","Could not update account retail limit"));
            return "/ops/limit/retail/account/edit";        }


        redirectAttributes.addFlashAttribute("message", "Retail account limit updated successfully");
        return "redirect:/ops/limits/retail/account";
    }
}
