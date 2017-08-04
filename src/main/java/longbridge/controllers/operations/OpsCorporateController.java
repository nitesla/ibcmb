package longbridge.controllers.operations;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.api.AccountInfo;
import longbridge.api.CustomerDetails;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.TransferRuleException;
import longbridge.models.*;
import longbridge.services.*;

import org.apache.commons.lang3.StringUtils;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/ops/corporates")
public class OpsCorporateController {

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CodeService codeService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private MakerCheckerService makerCheckerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private VerificationService verificationService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @ModelAttribute
    public void init(Model model) {
        List<CodeDTO> corporateTypes = codeService.getCodesByType("CORPORATE_TYPE");
        model.addAttribute("corporateTypes", corporateTypes);

        Iterable<RoleDTO> roles = roleService.getRolesByUserType(UserType.CORPORATE);
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()) {
            RoleDTO roleDTO = roleDTOIterator.next();
            if (roleDTO.getName().equals("SOLE")) {
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles", roles);

    }

    @GetMapping("/new")
    public String addCorporate(Model model) {
        model.addAttribute("corporate", new CorporateDTO());
        return "/ops/corporate/setup/new";
    }


    @PostMapping
    public String createCorporate(@ModelAttribute("corporate") @Valid CorporateDTO corporate, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/corporate/add";
        }

        boolean exists = corporateService.corporateExists(corporate.getCustomerId());
        if (exists) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("corporate.exist", null, locale)));
            return "/ops/corporate/add";
        }

        List<AccountInfo> accountInfos = integrationService.fetchAccounts(corporate.getCustomerId());

        if (accountInfos == null || accountInfos.isEmpty()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("corp.cifid.invalid", null, locale)));
            return "/ops/corporate/add";
        } else {
            CustomerDetails customerDetails = integrationService.viewCustomerDetailsByCif(corporate.getCustomerId());
            if (!customerDetails.isCorp()) {
                result.addError(new ObjectError("invalid", messageSource.getMessage("corp.cifid.invalid", null, locale)));
                return "/ops/corporate/add";
            }
            corporate.setName(customerDetails.getCustomerName());
            if (!makerCheckerService.isEnabled("ADD_CORPORATE")) {

                session.setAttribute("corporate", corporate);
                return "redirect:/ops/corporates/user/first";
            } else {
                try {
                    String message = corporateService.addCorporate(corporate);
                    redirectAttributes.addFlashAttribute("message", message);
                } catch (InternetBankingException e) {
                    logger.error("Error creating corporate entity", e);
                    redirectAttributes.addFlashAttribute("message", e.getMessage());

                }
                return "redirect:/ops/corporates";
            }
        }

    }


    @GetMapping("/user/first")
    public String addFirstCorporateUser(Model model, HttpSession session) {
        CorporateDTO corporateDTO = (CorporateDTO) session.getAttribute("corporate");
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "/ops/corporate/addUser";
    }


    @GetMapping("/{id}/edit")
    public String editCorporate(@PathVariable Long id, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(id);
        model.addAttribute("corporate", corporate);
        return "/ops/corporate/edit";
    }

    @GetMapping("/{corporateId}")
    public String getCorporate(@PathVariable Long corporateId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        model.addAttribute("corporate", corporate);
        return "/ops/corporates/details";
    }

    @GetMapping
    public String getAllCorporates(HttpSession session) {
        session.removeAttribute("corporate");
        return "/ops/corporate/view";
    }


    @GetMapping("/{reqId}/view")
    public String viewCorporate(@PathVariable Long reqId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(reqId);
        model.addAttribute("corporate", corporate);
        return "/ops/corporate/viewdetails";
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
    DataTablesOutput<CorporateDTO> getCorporates(DataTablesInput input, @RequestParam("csearch") String search) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateDTO> corps = corporateService.getCorporates(pageable);
        if (StringUtils.isNoneBlank(search)) {
            corps = corporateService.findCorporates(search, pageable);
        } else {
            corps = corporateService.getCorporates(pageable);
        }
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
            return "/ops/corporate/edit";
        }
        try {
            String message = corporateService.updateCorporate(corporate);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates";
        } catch (InternetBankingException ibe) {
            logger.error("Failed to update corporate entity", ibe);
            result.addError(new ObjectError("invalid", ibe.getMessage()));
            return "/ops/corporate/edit";

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
        return "redirect:/ops/corporates";
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
        return "redirect:/ops/corporates";
    }


    @GetMapping("/{corporateId}/account/new")
    public String linkAccount(@PathVariable Long corporateId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        AccountDTO account = new AccountDTO();
        account.setCustomerId(corporate.getCustomerId());
        model.addAttribute("account", account);
        model.addAttribute("corporate", corporate);
        return "/ops/corporate/addAccount";
    }

    @PostMapping("/account/new")
    public String linkAccountPost(@ModelAttribute("account") @Valid AccountDTO accountDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
            model.addAttribute("corporate", corporate);
            return "/ops/corporate/addAccount";
        }

        try {
            Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
            String message = corporateService.addAccount(corporate, accountDTO);
            Long corporateId = corporate.getId();
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates/" + corporateId + "/view";
        } catch (InternetBankingException ibe) {
            Corporate corporate = corporateService.getCorporateByCustomerId(accountDTO.getCustomerId());
            model.addAttribute("account", accountDTO);
            model.addAttribute("corporate", corporate);
            result.addError(new ObjectError("exception", messageSource.getMessage("corporate.account.add.failure", null, locale)));
            return "/ops/corporate/addAccount";
        }

    }

    @GetMapping("{corpId}/rules/new")
    public String addCorporateRule(@PathVariable Long corpId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corpId);
        List<CorporateRoleDTO> roles = corporateService.getRoles(corpId);
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        model.addAttribute("corporate", corporate);
        model.addAttribute("roleList", roles);
        model.addAttribute("currencies", currencies);
        model.addAttribute("corporateRule", new CorpTransferRuleDTO());
        return "/ops/corporate/addrule";
    }

    private void reInitializeModel(Model model, String corpId) {
        CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(corpId));
        List<CorporateRoleDTO> corporateRoles = corporateService.getRoles(NumberUtils.toLong(corpId));
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

        model.addAttribute("corporate", corporate);
        model.addAttribute("roleList", corporateRoles);
        model.addAttribute("currencies", currencies);
    }


    @PostMapping("/rules")
    public String createCorporateRule(@ModelAttribute("corporateRule") CorpTransferRuleDTO transferRuleDTO, BindingResult bindingResult, WebRequest request, Principal principal, WebRequest webRequest, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        String corporateId = transferRuleDTO.getCorporateId();
        try {
            new BigDecimal(transferRuleDTO.getLowerLimitAmount());
            if (!transferRuleDTO.isUnlimited()) {
                new BigDecimal(transferRuleDTO.getUpperLimitAmount());
            }
        } catch (NumberFormatException nfe) {
            reInitializeModel(model, corporateId);
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("rule.amount.invalid", null, locale)));

            return "/ops/corporate/addrule";

        }

        String[] roleIds;
        roleIds = webRequest.getParameterValues("roles");
        List<CorporateRoleDTO> roleDTOs = new ArrayList<CorporateRoleDTO>();
        CorporateRoleDTO corporateRole;
        int num = 2;
        SettingDTO setting = configService.getSettingByName("MIN_CORPORATE_APPROVERS");
        if (setting != null && setting.isEnabled()) {
            num = NumberUtils.toInt(setting.getValue());
        }

        if (roleIds != null) {
            if (roleIds.length >= num) {
                for (String roleId : roleIds) {
                    corporateRole = new CorporateRoleDTO();
                    corporateRole.setId(NumberUtils.toLong(roleId));
                    roleDTOs.add(corporateRole);
                }
            } else {
                reInitializeModel(model, corporateId);
                bindingResult.addError(new ObjectError("exception", String.format(messageSource.getMessage("role.required", null, locale), num)));
                return "/ops/corporate/addrule";
            }
        } else {
            reInitializeModel(model, corporateId);
            bindingResult.addError(new ObjectError("exception", String.format(messageSource.getMessage("role.required", null, locale), num)));
            return "/ops/corporate/addrule";
        }
        if (transferRuleDTO.isUnlimited()) {
            transferRuleDTO.setUpperLimitAmount(new BigDecimal(Integer.MAX_VALUE).toString());
        }

        transferRuleDTO.setRoles(roleDTOs);
        transferRuleDTO.setAnyCanAuthorize("Y".equals(request.getParameter("anyCanAuthorize")));
        try {
            String message = corporateService.addCorporateRule(transferRuleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates/" + transferRuleDTO.getCorporateId() + "/view";
        } catch (TransferRuleException tre) {
            logger.error("Failed to create transfer rule", tre);
            bindingResult.addError(new ObjectError("exception", tre.getMessage()));
            reInitializeModel(model, corporateId);
            return "/ops/corporate/addrule";

        } catch (InternetBankingException ibe) {
            logger.error("Failed to create transfer rule", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            reInitializeModel(model, corporateId);
            return "/ops/corporate/addrule";
        }
    }

    @GetMapping("/rules/{id}/edit")
    public String editCorporateRule(@PathVariable Long id, Model model) {
        CorpTransferRuleDTO corpTransferRuleDTO = corporateService.getCorporateRule(id);
        model.addAttribute("corporateRule", corpTransferRuleDTO);
        reloadModel(model, id);

        return "/ops/corporate/editrule";
    }

    private void reloadModel(Model model, Long id) {
        CorpTransferRuleDTO transferRuleDTO = corporateService.getCorporateRule(id);
        CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
        List<CorporateRoleDTO> roles = corporateService.getRoles(NumberUtils.toLong(transferRuleDTO.getCorporateId()));
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

        for (CorporateRoleDTO role : roles) {
            for (CorporateRoleDTO roleDTO : transferRuleDTO.getRoles()) {
                if (roleDTO.getId().equals(role.getId())) {
                    role.setRuleMember(true);
                }

            }
        }


        model.addAttribute("corporate", corporate);
        model.addAttribute("roleList", roles);
        model.addAttribute("currencies", currencies);

    }

    @PostMapping("/rules/update")
    public String updateCorporateRule(@ModelAttribute("corporateRule") CorpTransferRuleDTO transferRuleDTO, BindingResult bindingResult, WebRequest request, Principal principal, Model model, WebRequest webRequest, RedirectAttributes redirectAttributes, Locale locale) {

        try {
            new BigDecimal(transferRuleDTO.getLowerLimitAmount());
            if (!transferRuleDTO.isUnlimited()) {
                new BigDecimal(transferRuleDTO.getUpperLimitAmount());

            }
        } catch (NumberFormatException nfe) {
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("rule.amount.invalid", null, locale)));
            reloadModel(model, transferRuleDTO.getId());

            return "/ops/corporate/editrule";

        }
        String[] roleIds;
        roleIds = webRequest.getParameterValues("roles");
        List<CorporateRoleDTO> roleDTOs = new ArrayList<>();
        CorporateRoleDTO corporateRole;

        int num = 2;
        SettingDTO setting = configService.getSettingByName("MIN_CORPORATE_APPROVERS");
        if (setting != null && setting.isEnabled()) {

            num = Integer.parseInt(setting.getValue());
        }

        if (roleIds != null) {
            if (roleIds.length >= num) {
                for (String roleId : roleIds) {
                    corporateRole = new CorporateRoleDTO();
                    corporateRole.setId(NumberUtils.toLong(roleId));
                    roleDTOs.add(corporateRole);
                }
            } else {
                bindingResult.addError(new ObjectError("exception", String.format(messageSource.getMessage("role.required", null, locale), num)));
                reloadModel(model, transferRuleDTO.getId());

                return "/ops/corporate/editrule";
            }
        } else {
            bindingResult.addError(new ObjectError("exception", String.format(messageSource.getMessage("role.required", null, locale), num)));
            reloadModel(model, transferRuleDTO.getId());
            return "/ops/corporate/editrule";
        }
        if (transferRuleDTO.isUnlimited()) {
            transferRuleDTO.setUpperLimitAmount(new BigDecimal(Integer.MAX_VALUE).toString());
        }
        transferRuleDTO.setRoles(roleDTOs);
        transferRuleDTO.setAnyCanAuthorize("Y".equals(request.getParameter("anyCanAuthorize")));


        try {
            String message = corporateService.updateCorporateRule(transferRuleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates/" + transferRuleDTO.getCorporateId() + "/view";
        } catch (TransferRuleException tre) {
            logger.error("Failed to update transfer rule", tre);
            bindingResult.addError(new ObjectError("exception", tre.getMessage()));
            reloadModel(model, transferRuleDTO.getId());
            return "/ops/corporate/editrule";

        } catch (InternetBankingException ibe) {
            logger.error("Failed to update transfer rule", ibe);
            bindingResult.addError(new ObjectError("exception", ibe.getMessage()));
            reloadModel(model, transferRuleDTO.getId());

            return "/ops/corporate/editrule";
        }
    }


    @GetMapping("/{id}/rules")
    public
    @ResponseBody
    DataTablesOutput<CorpTransferRuleDTO> getCorporateRules(@PathVariable Long id, DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpTransferRuleDTO> transferRules = corporateService.getCorporateRules(id, pageable);
        DataTablesOutput<CorpTransferRuleDTO> out = new DataTablesOutput<CorpTransferRuleDTO>();
        out.setDraw(input.getDraw());
        out.setData(transferRules.getContent());
        out.setRecordsFiltered(transferRules.getTotalElements());
        out.setRecordsTotal(transferRules.getTotalElements());
        return out;
    }

    @GetMapping("/rules/{id}/delete")
    public String deleteCorporateRule(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        CorpTransferRuleDTO transferRuleDTO = corporateService.getCorporateRule(id);
        try {

            String message = corporateService.deleteCorporateRule(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Failed to delete transfer rule", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/ops/corporates/" + transferRuleDTO.getCorporateId() + "/view";

    }


    @GetMapping("/{cifid}/name")
    @ResponseBody
    public String getCustomerName(@PathVariable String cifid) {
        CustomerDetails customerDetails = integrationService.viewCustomerDetailsByCif(cifid);

        if (customerDetails.getCustomerName() == null) {
            return "false";
        }
        if (!customerDetails.isCorp()) {
            return "false";
        }
        return customerDetails.getCustomerName();
    }


    @PostMapping("/new")
    public String addCorporateEntity(@ModelAttribute("corporate") @Valid CorporateDTO corporate, BindingResult result, Model model, HttpSession session, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/corporate/setup/new";
        }
//        logger.info("the cif id {}",corporate.getCustomerId());
        CustomerDetails customerDetails = integrationService.viewCustomerDetailsByCif(corporate.getCustomerId());

        if (customerDetails.getCustomerName() == null || !customerDetails.isCorp()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("corp.cifid.invalid", null, locale)));
            return "/ops/corporate/setup/new";

        }
        CorporateRequestDTO corporateExistingData = (CorporateRequestDTO) session.getAttribute("corporateRequest");
        String accounts[] = (String[]) session.getAttribute("selectedAccounts");
        CorporateRequestDTO corporateRequestDTO = new CorporateRequestDTO();
        corporateRequestDTO.setCustomerId(corporate.getCustomerId());
        corporateRequestDTO.setCorporateType(corporate.getCorporateType());
        corporateRequestDTO.setCustomerName(customerDetails.getCustomerName());
        corporate.setCustomerName(customerDetails.getCustomerName());
        session.setAttribute("corporateRequest", corporateRequestDTO);

        logger.debug("Corporate Request DTO " +
                "{}", corporateRequestDTO.toString());


        List<AccountInfo> accountInfos = integrationService.fetchAccounts(corporate.getCustomerId().toUpperCase());

        SettingDTO setting = configService.getSettingByName("SHARE_CORPORATE_ACCOUNT");

        if (setting != null) {
            if (setting.isEnabled()) {
                if ("NO".equalsIgnoreCase(setting.getValue())) {
                    accountInfos = filterAccounts(accountInfos, accountService.getAccounts(corporate.getCustomerId()));
                }
            } else {
                accountInfos = filterAccounts(accountInfos, accountService.getAccounts(corporate.getCustomerId()));
            }
        }

        logger.info("the schemeTYpe is {}",accountInfos.get(0).getSchemeType());
        model.addAttribute("accounts", accountInfos);
        if (((corporateExistingData != null) && (accounts != null)) && (corporate.getCustomerId().equalsIgnoreCase(corporateExistingData.getCustomerId()))) {

            model.addAttribute("selectedAccounts", Arrays.asList(accounts));
            model.addAttribute("corporate", corporateExistingData);
        } else {
            model.addAttribute("corporate", corporate);
            model.addAttribute("selectedAccounts", "null");
        }
        return "/ops/corporate/setup/account";

    }

    private List<AccountInfo> filterAccounts(List<AccountInfo> newAccs, List<AccountDTO> existingAccs) {

        List<AccountInfo> accountInfos = new ArrayList<>();
        logger.debug("Existing accounts: {}", existingAccs);
        logger.debug("New Accounts: {}", newAccs);

        for (AccountInfo accountInfo : newAccs) {
            boolean existingAcc = false;
            for (AccountDTO accountDTO : existingAccs) {
                if (accountInfo.getAccountNumber().equals(accountDTO.getAccountNumber())) {
                    existingAcc = true;
                    break;
                }
            }
            if (!existingAcc) {
                accountInfos.add(accountInfo);
            }
        }
        logger.debug("Filtered accounts: {}", accountInfos.toString());
        return accountInfos;
    }


    @PostMapping("/accounts")
    public String addCorporateAccounts(@ModelAttribute("corporate") @Valid CorporateDTO corporate, BindingResult
            result, RedirectAttributes redirectAttributes, WebRequest request, HttpSession session, Locale locale, Model
                                               model) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            List<AccountInfo> accountInfos = integrationService.fetchAccounts(corporate.getCustomerId().toUpperCase());
            model.addAttribute("accounts", accountInfos);
            session.removeAttribute("corporateRequest");
            return "/ops/corporate/setup/account";
        }

        if (corporateService.corporateIdExists(corporate.getCorporateId())) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("corp.id.exists", null, locale)));
            List<AccountInfo> accountInfos = integrationService.fetchAccounts(corporate.getCustomerId().toUpperCase());
            model.addAttribute("accounts", accountInfos);
            return "/ops/corporate/setup/account";

        }

        String[] accounts = request.getParameterValues("accounts");

        logger.info("Customer accounts {}", Arrays.asList(accounts));

        if (session.getAttribute("corporateRequest") != null) {
            List<AccountInfo> accountInfos = integrationService.fetchAccounts(corporate.getCustomerId().toUpperCase());
            session.removeAttribute("accountInfos");
            session.removeAttribute("selectedAccounts");

            session.setAttribute("accountInfos", accountInfos);
            session.setAttribute("selectedAccounts", accounts);
            CorporateRequestDTO corporateRequestDTO = (CorporateRequestDTO) session.getAttribute("corporateRequest");
            session.removeAttribute("corporateRequest");
            corporateRequestDTO.setCorporateName(corporate.getCorporateName());
            corporateRequestDTO.setCorporateId(corporate.getCorporateId());
            if (accounts.length > 0) {
                List<AccountDTO> accountDTOs = new ArrayList<>();
                for (String account : accounts) {
                    AccountDTO accountDTO = new AccountDTO();
                    accountDTO.setAccountNumber(account);
                    accountDTOs.add(accountDTO);
                }
                corporateRequestDTO.setAccounts(accountDTOs);
            }
            if((session.getAttribute("authorizerLevels") !=null)&&(!session.getAttribute("authorizerLevels").toString().equalsIgnoreCase("null"))) {
                List<AuthorizerLevelDTO> authorizerList = (List<AuthorizerLevelDTO>) session.getAttribute("authorizerLevels");
                model.addAttribute("authorizerList",authorizerList);
            }else{
                model.addAttribute("authorizerList","null");
            }
            session.setAttribute("corporateRequest",corporateRequestDTO);

            model.addAttribute("selectedAccounts",accounts);
            model.addAttribute("corporate",corporateRequestDTO);
            logger.info("Corporate Request DTO {}", corporateRequestDTO.toString());
            return "/ops/corporate/setup/addauthorizer";

        }
        return "/ops/corporate/setup/account";

    }

    @GetMapping("/validate/{id}")
    @ResponseBody
    public String valiidateCorporateId(@PathVariable String id) {
        try {
            boolean isExisting = corporateService.corporateIdExists(id);
            if (!isExisting) {
                return "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @GetMapping("/authorizer")
    public String getAuthorizerPage(Model model) {
        model.addAttribute("authorizerList", "null");
        return "/ops/corporate/setup/authorizer";
    }


    @PostMapping("/authorizer")
    public String createAuthorizerLevels(WebRequest request, RedirectAttributes redirectAttributes, HttpSession
            session, Model model, Locale locale) {

        try {
            String authorizers = request.getParameter("authorizers");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            List<AuthorizerLevelDTO> authorizerList = mapper.readValue(authorizers, new TypeReference<List<AuthorizerLevelDTO>>() {
            });

            logger.info("Authorizers: {}", authorizerList.toString());
            session.removeAttribute("authorizerLevels");
            session.setAttribute("authorizerLevels", authorizerList);

            Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");

            if (session.getAttribute("corporateRequest") != null) {
                CorporateRequestDTO corporateRequestDTO = (CorporateRequestDTO) session.getAttribute("corporateRequest");
                session.removeAttribute("corporateRequest");
                session.setAttribute("corporateRequest", corporateRequestDTO);
                model.addAttribute("corporate", corporateRequestDTO);
                corporateRequestDTO.setAuthorizers(authorizerList);

                logger.info("Corporate Request DTO " +
                        "{}", corporateRequestDTO.toString());

            }
            model.addAttribute("currencies", currencies);
            model.addAttribute("authorizerList", authorizerList);


            int num = 2;
            SettingDTO setting = configService.getSettingByName("MIN_CORPORATE_APPROVERS");
            if (setting != null && setting.isEnabled()) {

                num = Integer.parseInt(setting.getValue());
            }

            model.addAttribute("numAuthorizers",num);
            if(session.getAttribute("rules")!= null) {
                String rules = (String) session.getAttribute("rules");
                model.addAttribute("rules",rules);
            }else{
                model.addAttribute("rules","");
            }

            return "/ops/corporate/setup/addrule";

        } catch (Exception ibe) {
            logger.error("Error creating group", ibe);
            redirectAttributes.addFlashAttribute("failure", "Error has occurred");
            session.removeAttribute("corporateRequest");
            session.removeAttribute("authorizerLevels");
            return "redirect:/ops/corporates/new";
        }


    }

    @GetMapping("/back/new")
    public String addCorporateUsingBack(Model model, HttpSession session) {
        CorporateRequestDTO corporate = (CorporateRequestDTO) session.getAttribute("corporateRequest");
        model.addAttribute("corporate", corporate);
        return "/ops/corporate/setup/new";
    }

    @GetMapping("/back/account")
    public String addAccountUsingBack(Model model, HttpSession session) {
        String accounts[] = (String[]) session.getAttribute("selectedAccounts");
        CorporateRequestDTO corporate = (CorporateRequestDTO) session.getAttribute("corporateRequest");
//        logger.info("the session corp request {}",corporate);
//        logger.info("the session accounts {}",Arrays.asList(accounts));
        List<AccountInfo> accountInfos = (List<AccountInfo>) session.getAttribute("accountInfos");
        model.addAttribute("accounts", accountInfos);
        model.addAttribute("corporate", corporate);
        model.addAttribute("selectedAccounts",Arrays.asList(accounts));
        return "/ops/corporate/setup/account";
    }

    @GetMapping("/back/authorizer")
    public String getAuthorizerBackPage(Model model, HttpSession session) {
        List<AuthorizerLevelDTO> authorizerList = (List<AuthorizerLevelDTO>) session.getAttribute("authorizerLevels");
        CorporateRequestDTO corporate = (CorporateRequestDTO) session.getAttribute("corporateRequest");
        model.addAttribute("authorizerList",authorizerList);
        model.addAttribute("corporate",corporate);

        return "/ops/corporate/setup/addauthorizer";
    }
    @GetMapping("/back/rule")
    public String getRuleBackPage(WebRequest request, Model model, HttpSession session){
        String users= request.getParameter("users");

        logger.info("Corporate Users are: {}",users);
        session.removeAttribute("users");
        session.setAttribute("users",users);
        List<AuthorizerLevelDTO> authorizerList = (List<AuthorizerLevelDTO>) session.getAttribute("authorizerLevels");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        CorporateRequestDTO corporateRequestDTO = (CorporateRequestDTO) session.getAttribute("corporateRequest");
        String rules = (String) session.getAttribute("rules");
        model.addAttribute("corporate", corporateRequestDTO);
        corporateRequestDTO.setAuthorizers(authorizerList);
//        logger.info("Corporate Request DTO {}", corporateRequestDTO.toString());
        model.addAttribute("currencies", currencies);
        model.addAttribute("authorizerList", authorizerList);
//        logger.info("the authorizer is {}",corporateRequestDTO);
        int num = 2;
        SettingDTO setting = configService.getSettingByName("MIN_CORPORATE_APPROVERS");
        if (setting != null && setting.isEnabled()) {
            num = Integer.parseInt(setting.getValue());
        }

        model.addAttribute("numAuthorizers",num);
        model.addAttribute("rules",rules);

        return "/ops/corporate/setup/addrule";
    }


    @PostMapping("/rules/new")
    public String createTransactionRule(WebRequest request, RedirectAttributes redirectAttributes, HttpSession
            session, Model model, Locale locale) {

        String rules = request.getParameter("rules");

        logger.info("Transaction rules are: {}", rules);


        List<CorpTransferRuleDTO> transferRules = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            transferRules = mapper.readValue(rules, new TypeReference<List<CorpTransferRuleDTO>>() {
            });
            session.removeAttribute("rules");
            session.setAttribute("rules",rules);
            logger.debug("Corp Transfer Rules: {}", transferRules.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(session.getAttribute("corporateRequest")!=null) {
            CorporateRequestDTO corporateRequestDTO = (CorporateRequestDTO) session.getAttribute("corporateRequest");
            corporateRequestDTO.setCorpTransferRules(transferRules);
            model.addAttribute("corporate",corporateRequestDTO);
            session.removeAttribute("corporateRequest");
            session.setAttribute("corporateRequest",corporateRequestDTO);
            logger.debug("Corporate Reequest: {}",corporateRequestDTO);
        }

        if((session.getAttribute("inputedUsers") != null)){
            String users = session.getAttribute("inputedUsers").toString();
//            logger.info("The inputed users are {}",users);
            model.addAttribute("inputedUsers",users);
        }else{
            model.addAttribute("inputedUsers","");
        }
        if(session.getAttribute("authorizerLevels")!=null) {
            List<AuthorizerLevelDTO>  authorizerLevels= (ArrayList) session.getAttribute("authorizerLevels");
            model.addAttribute("authorizerLevels",authorizerLevels);
        }

        return "/ops/corporate/setup/adduser";


    }

    @PostMapping("/users/create")
    public String createCorporateUsers(WebRequest request, RedirectAttributes redirectAttributes, HttpSession session, Model model, Locale locale) {

        String users= request.getParameter("users");

        logger.info("Corporate Users are: {}",users);
        session.removeAttribute("users");
        session.setAttribute("users",users);

        List<CorporateUserDTO> corporateUsers = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            corporateUsers = mapper.readValue(users, new TypeReference<List<CorporateUserDTO>>() {
            });

            logger.debug("Corporate users: {}", corporateUsers.toString());


            if (session.getAttribute("corporateRequest") != null) {
                CorporateRequestDTO corporateRequestDTO = (CorporateRequestDTO) session.getAttribute("corporateRequest");
                corporateRequestDTO.setCorporateUsers(corporateUsers);
                model.addAttribute("corporate", corporateRequestDTO);

                logger.debug("Corporate Request: {}", corporateRequestDTO);
                if (makerCheckerService.isEnabled("ADD_CORPORATE")) {
                    String message = verificationService.add(corporateRequestDTO, "ADD_CORPORATE", "Adding Corporate Entity");
                    redirectAttributes.addFlashAttribute("message", message);
                } else {
                    String message = corporateService.addCorporate(corporateRequestDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                }
                session.removeAttribute("corporateRequest");
            }
        } catch (Exception e) {
            logger.error("Error creating corporate entity", e);
            redirectAttributes.addFlashAttribute("failure", "Failed to create corporate entity");

        }
        return "redirect:/ops/corporates/new";


    }

    @GetMapping("/{username}/exists")
    @ResponseBody
    public String checkUsername(@PathVariable String username) {
        if (corporateUserService.userExists(username)) {
            return "true";
        } else {
            return "false";
        }

    }
    @GetMapping("/back/users/keep")
    @ResponseBody
    public String keepUsers(WebRequest request,HttpSession session) {
        String users = request.getParameter("users");

//        logger.info("Corporate Users back are: {}", users);
        session.removeAttribute("inputedUsers");
        session.setAttribute("inputedUsers", users);
        return "success";
    }
}