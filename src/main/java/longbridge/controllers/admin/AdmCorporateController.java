package longbridge.controllers.admin;

import longbridge.api.AccountInfo;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.TransferRuleException;
import longbridge.models.*;
import longbridge.services.*;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

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
    CodeService codeService;

    @Autowired
    RoleService roleService;

    @Autowired
    IntegrationService integrationService;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @ModelAttribute
    public void init(Model model) {
        List<CodeDTO> corporateTypes = codeService.getCodesByType("CORPORATE_TYPE");
        model.addAttribute("corporateTypes", corporateTypes);

        Iterable<RoleDTO> roles = roleService.getRoles();
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()) {
            RoleDTO roleDTO = roleDTOIterator.next();
            if (roleDTO.getName().equals("Sole Admin")) {
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles", roles);

    }

    @GetMapping("/new")
    public String addCorporate(Model model) {
        model.addAttribute("corporate", new CorporateDTO());
        return "/adm/corporate/add";
    }

    @PostMapping
    public String createCorporate(@ModelAttribute("corporate") @Valid CorporateDTO corporate, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/corporate/add";
        }

        boolean exists = corporateService.corporateExists(corporate.getCustomerId());
        if(exists){
            result.addError(new ObjectError("invalid", messageSource.getMessage("corporate.exist", null, locale)));
            return "/adm/corporate/add";
        }

       List<AccountInfo> accountInfos = integrationService.fetchAccounts(corporate.getCustomerId());

        if (accountInfos == null || accountInfos.isEmpty()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("cifid.invalid", null, locale)));
            return "/adm/corporate/add";
        } else {
            String accountName = integrationService.viewCustomerDetails(accountInfos.get(0).getAccountNumber()).getCustomerName();
            corporate.setName(accountName);
            session.setAttribute("corporate", corporate);
            return "redirect:/admin/corporates/user/first";
        }

//        try{
//
//            String message = corporateService.addCorporate(corporate);
//            redirectAttributes.addFlashAttribute("message", message);
//            return "redirect:/admin/corporates";
//        } catch (InternetBankingException ibe) {
//            logger.error("Failed to create corporate entity", ibe);
//            result.addError(new ObjectError("invalid", ibe.getMessage()));
//            return "adm/corporate/add";
//
//        }
    }

    @GetMapping("/user/first")
    public String addFirstCorporateUser(Model model, HttpSession session) {
        CorporateDTO corporateDTO = (CorporateDTO) session.getAttribute("corporate");
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "/adm/corporate/addUser";
    }

    /**
     * Edit an existing user
     *
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(id);
        model.addAttribute("corporate", corporate);
        return "/adm/corporate/edit";
    }

    @GetMapping("/{corporateId}")
    public String getCorporate(@PathVariable Long corporateId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        model.addAttribute("corporate", corporate);
        return "/adm/corporates/details";
    }

    @GetMapping
    public String getAllCorporates(HttpSession session) {
        session.removeAttribute("corporate");
        return "/adm/corporate/view";
    }

//    @GetMapping("/{reqId}/view")
//    public String  viewRole(@PathVariable Long reqId, Model model){
//        CorporateDTO corporate = corporateService.getCorporate(reqId);
//        model.addAttribute("corporate",corporate);
//        return "/adm/corporate/details";
//    }

    @GetMapping("/{reqId}/view")
    public String viewRole(@PathVariable Long reqId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(reqId);
        model.addAttribute("corporate", corporate);
        return "/adm/corporate/viewdetails";
    }

    @GetMapping(path = "/{corpId}/users")
    public
    @ResponseBody
    DataTablesOutput<CorporateUserDTO> getUsers(@PathVariable Long corpId, DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateUserDTO> users = corporateUserService.getUsers(corpId, pageable);
        DataTablesOutput<CorporateUserDTO> out = new DataTablesOutput<CorporateUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(users.getContent());
        out.setRecordsFiltered(users.getTotalElements());
        out.setRecordsTotal(users.getTotalElements());
        return out;
    }

    @GetMapping(path = "/{corpId}/roles")
    public
    @ResponseBody
    DataTablesOutput<CorporateRoleDTO> getRoles(@PathVariable Long corpId, DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateRoleDTO> roles = corporateService.getRoles(corpId, pageable);
        DataTablesOutput<CorporateRoleDTO> out = new DataTablesOutput<CorporateRoleDTO>();
        out.setDraw(input.getDraw());
        out.setData(roles.getContent());
        out.setRecordsFiltered(roles.getTotalElements());
        out.setRecordsTotal(roles.getTotalElements());
        return out;
    }

    @GetMapping(path = "/{corpId}/accounts")
    public
    @ResponseBody
    DataTablesOutput<AccountDTO> getAccounts(@PathVariable Long corpId, DataTablesInput input) {

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
    public
    @ResponseBody
    DataTablesOutput<CorporateDTO> getCorporates(DataTablesInput input) {

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
    public String updateCorporate(@ModelAttribute("corporate") CorporateDTO corporate, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/corporate/edit";
        }
        try {
            String message = corporateService.updateCorporate(corporate);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates";
        } catch (InternetBankingException ibe) {
            logger.error("Failed to update corporate entity", ibe);
            result.addError(new ObjectError("invalid", ibe.getMessage()));
            return "/adm/corporate/edit";

        }
    }

    @GetMapping("/{id}/activation")
    public String changeCorporateActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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
    public String deleteCorporate(@PathVariable Long corporateId, RedirectAttributes redirectAttributes) {
        try {
            String message = corporateService.deleteCorporate(corporateId);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Failed to delete corporate", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/corporates";
    }


    @GetMapping("/{corporateId}/account/new")
    public String linkAccount(@PathVariable Long corporateId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        AccountDTO account = new AccountDTO();
        account.setCustomerId(corporate.getCustomerId());
        model.addAttribute("account", account);
        model.addAttribute("corporate", corporate);
        return "/adm/corporate/addAccount";
    }

    @PostMapping("/account/new")
    public String linkAccountPost(@ModelAttribute("account") @Valid AccountDTO accountDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
            model.addAttribute("corporate", corporate);
            return "/adm/corporate/addAccount";
        }

        try {
            Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
            String message = corporateService.addAccount(corporate, accountDTO);
            Long corporateId = corporate.getId();
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates/" + corporateId + "/view";
        } catch (InternetBankingException ibe) {
            Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
            model.addAttribute("account", accountDTO);
            model.addAttribute("corporate", corporate);
            result.addError(new ObjectError("exception", messageSource.getMessage("corporate.account.add.failure", null, locale)));
            return "/adm/corporate/addAccount";
        }

    }

    @GetMapping("{corpId}/rules/new")
    public String addCorporateRule(@PathVariable Long corpId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corpId);
        Set<CorporateRoleDTO> roles = corporateService.getRoles(corpId);
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

        model.addAttribute("corporate", corporate);
        model.addAttribute("roleList", roles);
        model.addAttribute("currencies", currencies);
        model.addAttribute("corporateRule", new CorpTransferRuleDTO());
        return "/adm/corporate/addrule";
    }

    @PostMapping("/rules")
    public String createCorporateRule(@ModelAttribute("corporateRule") CorpTransferRuleDTO transferRuleDTO, BindingResult bindingResult, Principal principal, WebRequest webRequest, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        try {
            BigDecimal lowerAmount = new BigDecimal(transferRuleDTO.getLowerLimitAmount());
            if (!transferRuleDTO.isUnlimited()) {
                BigDecimal upperAmount = new BigDecimal(transferRuleDTO.getUpperLimitAmount());

            }
        } catch (NumberFormatException nfe) {
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Set<CorporateRoleDTO> corporateRoles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

            model.addAttribute("corporate", corporate);
            model.addAttribute("roleList", corporateRoles);
            model.addAttribute("currencies", currencies);
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("rule.amount.invalid", null, locale)));

            return "/adm/corporate/addrule";

        }

        String[] roleIds;
        roleIds = webRequest.getParameterValues("roles");
        Set<CorporateRoleDTO> roleDTOs = new HashSet<CorporateRoleDTO>();
        CorporateRoleDTO corporateRole;

        if (roleIds != null) {
            for (String roleId : roleIds) {
                corporateRole = new CorporateRoleDTO();
                corporateRole.setId(NumberUtils.toLong(roleId));
                roleDTOs.add(corporateRole);
            }
        } else if (roleIds == null && transferRuleDTO.isAnyCanAuthorize()) {
            roleDTOs = corporateService.getRoles(Long.parseLong(transferRuleDTO.getCorporateId()));
        }
        if (transferRuleDTO.isUnlimited()) {
            transferRuleDTO.setUpperLimitAmount(new BigDecimal(transferRuleDTO.getLowerLimitAmount()).multiply(new BigDecimal("5")).toString());
        }
        transferRuleDTO.setRoles(roleDTOs);

        try {
            String message = corporateService.addCorporateRule(transferRuleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates/" + transferRuleDTO.getCorporateId() + "/view";
        } catch (TransferRuleException tre) {
            logger.error("Failed to create transfer rule", tre);
            bindingResult.addError(new ObjectError("exception", tre.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Set<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
            model.addAttribute("corporate", corporate);
            model.addAttribute("roleList", roles);
            model.addAttribute("currencies", currencies);
            return "/adm/corporate/addrule";

        } catch (InternetBankingException ibe) {
            logger.error("Failed to create transfer rule", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Set<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
            model.addAttribute("corporate", corporate);
            model.addAttribute("roleList", roles);
            model.addAttribute("currencies", currencies);
            return "/adm/corporate/addrule";
        }
    }

    @GetMapping("/rules/{id}/edit")
    public String editCorporateRule(@PathVariable Long id, Model model) {
        CorpTransferRuleDTO transferRuleDTO = corporateService.getCorporateRule(id);
        Set<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

        for (CorporateRoleDTO role : roles) {
            for (CorporateRoleDTO roleDTO : transferRuleDTO.getRoles()) {
                if (roleDTO.getId().equals(role.getId())) {
                    roleDTO.setRuleMember(true);
                }
            }
        }
        model.addAttribute("roleList", roles);
        model.addAttribute("corporateRule", transferRuleDTO);
        model.addAttribute("currencies", currencies);

        return "/adm/corporate/editrule";
    }

    @PostMapping("/rules/update")
    public String updateCorporateRule(@ModelAttribute("corporateRule") CorpTransferRuleDTO transferRuleDTO, BindingResult bindingResult, Principal principal, Model model, WebRequest webRequest, RedirectAttributes redirectAttributes, Locale locale) {

        try {
            BigDecimal lowerAmount = new BigDecimal(transferRuleDTO.getLowerLimitAmount());
            if (!transferRuleDTO.isUnlimited()) {
                BigDecimal upperAmount = new BigDecimal(transferRuleDTO.getUpperLimitAmount());

            }
        } catch (NumberFormatException nfe) {
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Set<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

            model.addAttribute("corporate", corporate);
            model.addAttribute("roleList", roles);
            model.addAttribute("currencies", currencies);
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("rule.amount.invalid", null, locale)));

            return "/adm/corporate/editrule";

        }
        String[] roleIds;
        roleIds = webRequest.getParameterValues("roles");
        Set<CorporateRoleDTO> roleDTOs = new HashSet<CorporateRoleDTO>();
        CorporateRoleDTO corporateRole;

        if (roleIds != null) {
            for (String roleId : roleIds) {
                corporateRole = new CorporateRoleDTO();
                corporateRole.setId(NumberUtils.toLong(roleId));
                roleDTOs.add(corporateRole);
            }
        } else if (roleIds == null && transferRuleDTO.isAnyCanAuthorize()) {
            roleDTOs = corporateService.getRoles(Long.parseLong(transferRuleDTO.getCorporateId()));
        }
        if (transferRuleDTO.isUnlimited()) {
            transferRuleDTO.setUpperLimitAmount(new BigDecimal(transferRuleDTO.getLowerLimitAmount()).multiply(new BigDecimal("5")).toString());
        }
        transferRuleDTO.setRoles(roleDTOs);


        try {
            String message = corporateService.updateCorporateRule(transferRuleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates/" + transferRuleDTO.getCorporateId() + "/view";
        } catch (TransferRuleException tre) {
            logger.error("Failed to update transfer rule", tre);
            bindingResult.addError(new ObjectError("exception", tre.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Set<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
            model.addAttribute("corporate", corporate);
            model.addAttribute("roleList", roles);
            model.addAttribute("currencies", currencies);
            return "/adm/corporate/editrule";

        } catch (InternetBankingException ibe) {
            logger.error("Failed to update transfer rule", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Set<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
            model.addAttribute("corporate", corporate);
            model.addAttribute("roleList", roles);
            model.addAttribute("currencies", currencies);
            return "/adm/corporate/editrule";
        }
    }


    @GetMapping("/{id}/rules")
    public
    @ResponseBody
    DataTablesOutput<CorpTransferRuleDTO> getCorporateRules(@PathVariable Long id, DataTablesInput input) {

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
    public String deleteCorporateRule(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            String message = corporateService.deleteCorporateRule(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Failed to delete transfer rule", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/corporates/";

    }

}

