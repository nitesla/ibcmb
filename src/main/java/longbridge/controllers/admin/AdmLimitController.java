package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.UserType;
import longbridge.services.CodeService;
import longbridge.services.TransactionLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
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
@RequestMapping("/admin/limits")
public class AdmLimitController {


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

    @GetMapping("/retail/global/new")
    public String getRetailGlobalLimit(Model model) {
        model.addAttribute("globalLimit", new GlobalLimitDTO());
        return "/adm/limit/retail/global/add";
    }

    @PostMapping("/retail/global")
    public String createRetailGlobalLimit(@ModelAttribute("globalLimit") @Valid GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/limit/retail/global/add";
        }
        if("".equals(globalLimitDTO.getMaxLimit())){
            globalLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(globalLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/adm/limit/retail/global/add";
            }
        }


        globalLimitDTO.setCustomerType(UserType.RETAIL.name());


        try {
            String message = transactionLimitService.addGlobalLimit(globalLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/limits/retail/global";
        }
        catch (DataAccessException exc){
            logger.error("Could not add global limit",exc.toString());
            result.addError(new ObjectError("exception",String.format("The global limit for %s already exists",globalLimitDTO.getChannel())));
            return "/adm/limit/retail/global/add";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while adding global limit");
            result.addError(new ObjectError("exception","Could not create global retail limit: "+e.getMessage()));
            return "/adm/limit/retail/global/add";
        }

    }


    @GetMapping("/retail/global")
    public String viewRetailGlobalLimits() {
        return "/adm/limit/retail/global/view";
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
        return "/adm/limit/corporate/global/view";
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
        return "/adm/limit/corporate/global/add";
    }

    @PostMapping("/corporate/global")
    public String createCorporateGlobalLimit(@ModelAttribute("globalLimit") @Valid  GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/limit/corporate/global/add";
        }

        if("".equals(globalLimitDTO.getMaxLimit())){
            globalLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(globalLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/adm/limit/corporate/global/add";
            }
        }


        globalLimitDTO.setCustomerType(UserType.CORPORATE.name());
        try {
            String message = transactionLimitService.addGlobalLimit(globalLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/limits/corporate/global";
        }
        catch (DataAccessException exc){
            logger.error("Could not add global limit",exc);
            result.addError(new ObjectError("exception",String.format("The global limit for %s already exists",globalLimitDTO.getChannel())));
            return "/adm/limit/corporate/global/add";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while adding global limit",e);
            result.addError(new ObjectError("exception","Could not create global corporate limit"));
            return "/adm/limit/corporate/global/add";
        }

    }


    @GetMapping("/corporate/global/{id}/edit")
    public String editCorporateGlobalLimit(Model model, @PathVariable Long id){
        GlobalLimitDTO globalLimit = transactionLimitService.getCorporateGlobalLimit(id);
         model.addAttribute("globalLimit",globalLimit);

        return "/adm/limit/corporate/global/edit";
    }

    @PostMapping("/corporate/global/update")
    public String updateCorporateGlobalLimit(@ModelAttribute("globalLimit") @Valid GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/limit/corporate/global/edit";
        }

        if("".equals(globalLimitDTO.getMaxLimit())){
            globalLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(globalLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/adm/limit/corporate/global/edit";
            }
        }

        try {
            String message = transactionLimitService.addGlobalLimit(globalLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/limits/corporate/global";
        }
        catch (InternetBankingException e) {
            logger.error("Exception while updating global limit",e);
            result.addError(new ObjectError("exception","Could not update global corporate limit"));
            return "/adm/limit/corporate/global/edit";
        }

    }

    @GetMapping("/corporate/global/{id}/delete")
    public String deleteCorporateGlobalLimit(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = transactionLimitService.deleteCorporateGlobalLimit(id);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to delete global limit",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
        }
        return "redirect:/admin/limits/corporate/global";
    }

    @GetMapping("/retail/global/{id}/edit")
    public String editRetailGlobalLimit(Model model, @PathVariable Long id){
        GlobalLimitDTO globalLimit = transactionLimitService.getCorporateGlobalLimit(id);
        model.addAttribute("globalLimit",globalLimit);
        return "/adm/limit/retail/global/edit";
    }

    @PostMapping("/retail/global/update")
    public String updateRetailGlobalLimit(@ModelAttribute("globalLimit") @Valid GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/limit/retail/global/edit";
        }

        if("".equals(globalLimitDTO.getMaxLimit())){
            globalLimitDTO.setMaxLimit("0");
        }
        else{
            try {
                BigDecimal amount = new BigDecimal(globalLimitDTO.getMaxLimit());
            }
            catch (NumberFormatException e){
                result.addError(new ObjectError("invalid", messageSource.getMessage("rule.amount.invalid", null, locale)));
                return "/adm/limit/retail/global/edit";
            }
        }
        try {
            String message = transactionLimitService.updateGlobalLimit(globalLimitDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/limits/retail/global";
        } catch (InternetBankingException e) {
            logger.error("Exception while updating global limit",e);
            result.addError(new ObjectError("exception","Could not update global retail limit"));
            return "/adm/limit/retail/global/edit";        }


    }

    @GetMapping("/retail/global/{id}/delete")
    public String deleteRetailGlobalLimit(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = transactionLimitService.deleteRetailGlobalLimit(id);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to delete account limit",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
        }
        return "redirect:/admin/limits/retail/global";
    }



}
