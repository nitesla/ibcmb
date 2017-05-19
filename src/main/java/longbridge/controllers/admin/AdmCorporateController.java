package longbridge.controllers.admin;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CorpTransferRuleDTO;
import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/corporates")
public class AdmCorporateController {

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    IntegrationService integrationService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/new")
    public String addCorporate(Model model){
        model.addAttribute("corporate", new CorporateDTO());
        return "adm/corporate/add";
    }

    @PostMapping
    public String createCorporate(@ModelAttribute("corporate") CorporateDTO corporate, BindingResult result, RedirectAttributes redirectAttributes,Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "adm/corporate/add";
        }
        try {
            String message = corporateService.addCorporate(corporate);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates";
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to create corporate entity",ibe);
            result.addError(new ObjectError("invalid",ibe.getMessage()));
            return "adm/corporate/add";

        }
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(id);
        model.addAttribute("corporate", corporate);
        return "adm/corporate/edit";
    }

    @GetMapping("/{corporateId}")
    public String getCorporate(@PathVariable Long corporateId, Model model){
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        model.addAttribute("corporate",corporate);
        return "adm/corporates/details";
    }

    @GetMapping
    public String getAllCorporates(){
        return "adm/corporate/view";
    }

//    @GetMapping("/{reqId}/view")
//    public String  viewRole(@PathVariable Long reqId, Model model){
//        CorporateDTO corporate = corporateService.getCorporate(reqId);
//        model.addAttribute("corporate",corporate);
//        return "/adm/corporate/details";
//    }

    @GetMapping("/{reqId}/view")
    public String  viewRole(@PathVariable Long reqId, Model model){
        CorporateDTO corporate = corporateService.getCorporate(reqId);
        model.addAttribute("corporate",corporate);
        return "/adm/corporate/viewdetails";
    }

    @GetMapping(path = "/{corpId}/users")
    public @ResponseBody
    DataTablesOutput<CorporateUserDTO> getUsers(@PathVariable Long corpId, DataTablesInput input){
//        CorporateDTO corporate = corporateService.getCorporate(corpId);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateUserDTO> users = corporateUserService.getUsers(corpId, pageable);
        DataTablesOutput<CorporateUserDTO> out = new DataTablesOutput<CorporateUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(users.getContent());
        out.setRecordsFiltered(users.getTotalElements());
        out.setRecordsTotal(users.getTotalElements());
        return out;
    }

    @GetMapping(path = "/{corpId}/accounts")
    public @ResponseBody
    DataTablesOutput<AccountDTO> getAccounts(@PathVariable Long corpId, DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountDTO> accounts = corporateService.getAccounts(corpId, pageable);
        DataTablesOutput<AccountDTO> out = new DataTablesOutput<AccountDTO>();
        out.setDraw(input.getDraw());
        out.setData(accounts.getContent());
        out.setRecordsFiltered(accounts.getTotalElements());
        out.setRecordsTotal(accounts.getTotalElements());
        return out;
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<CorporateDTO> getCorporates(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateDTO> corps = corporateService.getCorporates(pageable);
        DataTablesOutput<CorporateDTO> out = new DataTablesOutput<CorporateDTO>();
        out.setDraw(input.getDraw());
        out.setData(corps.getContent());
        out.setRecordsFiltered(corps.getTotalElements());
        out.setRecordsTotal(corps.getTotalElements());
        return out;
    }

    @PostMapping("/update")
    public String updateCorporate(@ModelAttribute("corporate") CorporateDTO corporate, BindingResult result,RedirectAttributes redirectAttributes,Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "adm/corporate/edit";
        }
        try {
            String message = corporateService.updateCorporate(corporate);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates";
        }
        catch (InternetBankingException ibe){
            logger.error("Failed to update corporate entity",ibe);
            result.addError(new ObjectError("invalid",ibe.getMessage()));
            return "adm/corporate/edit";

        }
    }

    @GetMapping("/{id}/activation")
    public String changeActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = corporateService.changeActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing corporate activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/corporates";
    }

    @GetMapping("/{corporateId}/delete")
    public String deleteCorporate(@PathVariable Long corporateId, RedirectAttributes redirectAttributes){
       try {
           String message = corporateService.deleteCorporate(corporateId);
           redirectAttributes.addFlashAttribute("message", message);

       }catch (InternetBankingException ibe){
           logger.error("Failed to delete corporate",ibe);
           redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
       }
        return "redirect:/admin/corporates";
    }


    @GetMapping("/{corporateId}/account/new")
    public String linkAccount(@PathVariable Long corporateId, Model model){
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        AccountDTO account = new AccountDTO();
        account.setCustomerId(corporate.getCustomerId());
        model.addAttribute("account", account);
        model.addAttribute("corporate", corporate);
        return "adm/corporate/addAccount";
    }

    @PostMapping("/account/new")
    public String linkAccountPost(AccountDTO accountDTO, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/corporate/new";
        }

        //integrationService.fetchAccount(accountDTO.getAccountNumber());

        Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
        String message = corporateService.addAccount(corporate, accountDTO);
        Long corporateId = corporate.getId();
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/admin/corporates/"+corporateId+"/view";
    }

    @GetMapping("{corpId}/rules/new")
    public String addCorporateRule(@PathVariable Long corpId, Model model){
        CorporateDTO corporate = corporateService.getCorporate(corpId);
        List<CorporateUserDTO> authorizers = corporateService.getAuthorizers(corpId);
        logger.info("AUTHORIZERS {}:", authorizers);
        model.addAttribute("corporate", corporate);
        model.addAttribute("authUserList",authorizers);
        model.addAttribute("corporateRule",new CorpTransferRuleDTO());
        return "adm/corporate/addrule";
    }

    @PostMapping("/rules")
    public String createCorporateRule(@ModelAttribute("corporateRule") @Valid CorpTransferRuleDTO transferRuleDTO, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes,Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/corporate/add-rule";
        }
        try {
            String message = corporateService.addCorporateRule(transferRuleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates/";
        } catch (InternetBankingException ibe) {
            logger.error("Failed to create transfer rule", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            return "adm/corporate/addrule";
        }
    }

    @GetMapping("/rules/{id}/edit")
    public String editCorporateRule(@PathVariable Long id, Model model){
        CorporateDTO corporate = corporateService.getCorporate(id);
        List<CorporateUserDTO> authorizers = corporateService.getAuthorizers(id);
        CorpTransferRuleDTO transferRuleDTO = corporateService.getCorporateRule(id);
        logger.info("AUTHORIZERS", authorizers);
        model.addAttribute("corporate", corporate);
        model.addAttribute("authorizers",authorizers);
        model.addAttribute("corporateRule",transferRuleDTO);
        return "adm/corporate/editrule";
    }

    @PostMapping("/rules/update")
    public String updateCorporateRule(@ModelAttribute("corporateRule") @Valid CorpTransferRuleDTO transferRuleDTO, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes,Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/corporate/editrule";
        }
        try {
            String message = corporateService.updateCorporateRule(transferRuleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates/";
        } catch (InternetBankingException ibe) {
            logger.error("Failed to update transfer rule", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            return "adm/corporate/editrule";
        }
    }

    @GetMapping("/{id}/rules")
    public @ResponseBody DataTablesOutput<CorpTransferRuleDTO> getCorporateRules(@PathVariable Long id, DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        List<CorpTransferRuleDTO> transferRules = corporateService.getCorporateRules(id);
        DataTablesOutput<CorpTransferRuleDTO> out = new DataTablesOutput<CorpTransferRuleDTO>();
        out.setDraw(input.getDraw());
        out.setData(transferRules);
        out.setRecordsFiltered(transferRules.size());
        out.setRecordsTotal(transferRules.size());
        return out;
    }

    @GetMapping("/rules/{id}/delete")
    public String deleteCorporateRule(@PathVariable Long id, RedirectAttributes redirectAttributes){
       try {
           String message = corporateService.deleteCorporateRule(id);
           redirectAttributes.addFlashAttribute("message",message);
       }
       catch (InternetBankingException ibe){
           logger.error("Failed to delete transfer rule", ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());
       }
        return "adm/corporate/";

    }


}

