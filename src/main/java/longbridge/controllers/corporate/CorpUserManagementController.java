package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.CorpUserType;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.VerificationStatus;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 26/05/2017.
 */

@Controller
@RequestMapping("/corporate/users")
public class CorpUserManagementController {

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private CorpUserVerificationService corpUserVerificationService;

    @Autowired
    private MakerCheckerService makerCheckerService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private VerificationService verificationService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;

    @ModelAttribute
    public void init(Model model, Principal principal) {
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
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
        model.addAttribute("corporate", corporate);

        List<CorporateRoleDTO> corporateRoleDTO = corporateService.getRoles(corporateUser.getCorporate().getId());
        model.addAttribute("corporateRoles", corporateRoleDTO);

        List<CorpTransferRuleDTO> corpTransferRuleDTO = corporateService.getCorporateRules(corporate.getId());
        model.addAttribute("corpTransferRules", corpTransferRuleDTO);

    }

//    @ModelAttribute
//    public void setModelAttribute(Principal principal, Model model){
//        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
//        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
//        model.addAttribute("corporate", corporate);
//    }

    @GetMapping
    public String viewUsers(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        if(!corporateUser.getCorpUserType().equals(CorpUserType.INITIATOR)) {
            CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
            model.addAttribute("corporate", corporate);
            return "corp/user/view";
        }
        //Redirect the Initiator to dashboard, has no right to manage users
//        redirectAttributes.addFlashAttribute("error", "User must be an Initiator");
        return "redirect:/corporate/dashboard";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<CorporateUserDTO> getUsers(Principal principal, DataTablesInput input) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        if(!corporateUser.getCorpUserType().equals(CorpUserType.INITIATOR)) {
            CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
            Pageable pageable = DataTablesUtils.getPageable(input);
            Page<CorporateUserDTO> users = corporateUserService.getUsers(corporate.getId(), pageable);
            DataTablesOutput<CorporateUserDTO> out = new DataTablesOutput<>();
            out.setDraw(input.getDraw());
            out.setData(users.getContent());
            out.setRecordsFiltered(users.getTotalElements());
            out.setRecordsTotal(users.getTotalElements());
            return out;
        }
        return null;
    }

    @GetMapping("/add")
    public String addUser(Principal principal, Model model) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        if(!corporateUser.getCorpUserType().equals(CorpUserType.ADMIN)){
            return "redirect:/corporate/dashboard";
        }
        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporateUserDTO", corporateUserDTO);
        model.addAttribute("corporate", corporate);
        return "corp/user/add";
    }

    @PostMapping
    public String createUser(@ModelAttribute("corporateUserDTO") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }

            if (result.hasErrors()) {
            return "corp/user/add";
        }

        CorporateUser corpUser = corporateUserService.getUserByName(corporateUserDTO.getUserName());
        if (corpUser != null) {
            model.addAttribute("failure", messageSource.getMessage("user.exists", null, locale));
            return "corp/user/add";
        }
        Corporate corporate = corporateService.getCorp(Long.parseLong(corporateUserDTO.getCorporateId()));
        CorporateUser corporateUser = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, corporateUserDTO.getEmail());
        if (corporateUser != null) {
            model.addAttribute("failure", messageSource.getMessage("email.exists", null, locale));
            return "corp/user/add";
        }

        try {

            String message = null;

            if (CorpUserType.AUTHORIZER.equals(corporateUserDTO.getCorpUserType())) {
                CorporateRoleDTO corporateRole = corporateService.getCorporateRole(corporateUserDTO.getCorporateRoleId());
                corporateUserDTO.setCorporateRole(corporateRole.getName() + " " + corporateRole.getRank());

                if (makerCheckerService.isEnabled("ADD_AUTHORIZER_FROM_CORPORATE_ADMIN")) {
                    corpUserVerificationService.addAuthorizer(corporateUserDTO, "ADD_AUTHORIZER_FROM_CORPORATE_ADMIN", "Add an authorizer by corporate Admin");
                } else {
                    message = corporateUserService.addAuthorizer(corporateUserDTO);
                }

            } else {
                if (makerCheckerService.isEnabled("ADD_INITIATOR_FROM_CORPORATE_ADMIN")) {
                    corpUserVerificationService.addInitiator(corporateUserDTO, "ADD_INITIATOR_FROM_CORPORATE_ADMIN", "Add an initiator by corporate Admin");
                } else {
                    message = corporateUserService.addInitiator(corporateUserDTO);
                }
            }

            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corporate/users/";
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user {}", corporateUserDTO.getUserName(), doe);
            model.addAttribute("failure", doe.getMessage());
            return "corp/user/add";
        } catch (VerificationInterruptedException ib) {
            redirectAttributes.addFlashAttribute("message", ib.getMessage());
            return "redirect:/corporate/users/";
        } catch (VerificationException e) {
            result.addError(new ObjectError("error", e.getMessage()));
            logger.error("Error creating corporate user", e);
            model.addAttribute("failure", messageSource.getMessage("user.add.failure", null, locale));
            return "corp/user/add";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);
            model.addAttribute("failure", messageSource.getMessage("failure", null, locale));
            return "corp/user/add";
        }
    }


    @GetMapping("{id}/edit")
    public String editUser(@PathVariable Long id, Model model, Principal principal) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }

        CorporateUserDTO corporateUserDTO = corporateUserService.getUser(id);
        CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));

        model.addAttribute("corporateUserDTO", corporateUserDTO);
        model.addAttribute("corporate", corporate);
        return "corp/user/edit";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("corporateUserDTO") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }

        if (result.hasErrors()) {
            CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
            List<CorporateRoleDTO> corporateRoleDTO = corporateService.getRoles(Long.parseLong(corporateUserDTO.getCorporateId()));
            model.addAttribute("corporate", corporate);
            model.addAttribute("corporateRoles", corporateRoleDTO);
            return "corp/user/edit";
        }

        if (verificationService.isPendingVerification(corporateUserDTO.getId(), CorporateUser.class.getSimpleName())) {
            model.addAttribute("failure", "User has pending changes to be verified");
            return "corp/user/edit";
        }

        try {
            CorporateUserDTO corporateUser = corporateUserService.getUser(corporateUserDTO.getId());
            if (!corporateUserDTO.getEmail().equals(corporateUser.getEmail())) {
                Corporate corporate = corporateService.getCorp(Long.parseLong(corporateUserDTO.getCorporateId()));
                CorporateUser cp = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, corporateUserDTO.getEmail());
                if (cp != null) {
                    CorporateDTO corporateDTO = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                    List<CorporateRoleDTO> corporateRoleDTO = corporateService.getRoles(Long.parseLong(corporateUserDTO.getCorporateId()));
                    model.addAttribute("corporate", corporateDTO);
                    model.addAttribute("corporateRoles", corporateRoleDTO);
                    model.addAttribute("failure", messageSource.getMessage("email.exists", null, locale));
                    return "corp/user/edit";
                }
            }

            if (CorpUserType.AUTHORIZER.equals(corporateUserDTO.getCorpUserType())) {
                CorporateRoleDTO corporateRole = corporateService.getCorporateRole(corporateUserDTO.getCorporateRoleId());
                corporateUserDTO.setCorporateRole(corporateRole.getName() + " " + corporateRole.getRank());
            }

            if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())) {

                if (makerCheckerService.isEnabled("UPDATE_USER_FROM_CORPORATE_ADMIN")) {
                    corpUserVerificationService.saveAuthorizer(corporateUserDTO, "UPDATE_USER_FROM_CORPORATE_ADMIN", "Edit an authorizer by corporate Admin");
                } else {
                    String message = corporateUserService.updateUserFromCorpAdmin(corporateUserDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                }

            } else {

                if (makerCheckerService.isEnabled("UPDATE_USER_FROM_CORPORATE_ADMIN")) {
                    corpUserVerificationService.saveInitiator(corporateUserDTO, "UPDATE_USER_FROM_CORPORATE_ADMIN", "Edit an initiator by corporate Admin");
                } else {
                    String message = corporateUserService.updateUserFromCorpAdmin(corporateUserDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                }
            }

            return "redirect:/corporate/users/";

        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user {}", corporateUserDTO.getUserName(), doe);
            model.addAttribute("failure", doe.getMessage());
            return "corp/user/edit";
        } catch (VerificationInterruptedException ib) {
            redirectAttributes.addFlashAttribute("message", ib.getMessage());
            return "redirect:/corporate/users/";
        } catch (VerificationException e) {
            result.addError(new ObjectError("error", e.getMessage()));
            logger.error("Error editing corporate user", e);
            model.addAttribute("failure", messageSource.getMessage("user.add.failure", null, locale));
            return "corp/user/edit";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error updating corporate user", ibe);
            model.addAttribute("failure", ibe.getMessage());
            return "corp/user/edit";
        }
    }

    @GetMapping("/{id}/status")
    public String activationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }
        try {
            String message = "";
            if (makerCheckerService.isEnabled("UPDATE_CORP_USER_STATUS")) {
                message = corpUserVerificationService.changeStatusFromCorporateAdmin(id);
            } else {
                message = corporateUserService.changeActivationStatusFromCorpAdmin(id);
            }
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing corporate user activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/corporate/users";
    }


    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }

        if (verificationService.isPendingVerification(id, CorporateUser.class.getSimpleName())) {
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/corporate/users";
        }

        if (corpUserVerificationService.isPendingVerification(id, CorporateUser.class.getSimpleName())) {
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/corporate/users";
        }

        try {
            String message = corporateUserService.resetCorpPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for corporate user", pe);
        }
        return "redirect:/corporate/users";
    }

    @GetMapping("/{id}/unblock")
    public String unblock(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }
        try {
            String message = corporateUserService.unlockUser(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error unblocking corporate user", pe);
        }
        return "redirect:/corporate/users";
    }

    @GetMapping("/approvals")
    public String approvals(Principal principal, Model model) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(loggedInUser.getCorpUserType().equals(CorpUserType.INITIATOR)) {
            return "redirect:/corporate/dashboard";
        }
        return "/corp/user/approval";
    }

    @GetMapping(path = "/approvals/all")
    public
    @ResponseBody
    DataTablesOutput<CorpUserVerificationDTO> getAllVerification(Principal principal, DataTablesInput input) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        if(!corporateUser.getCorpUserType().equals(CorpUserType.INITIATOR)) {

            Pageable pageable = DataTablesUtils.getPageable(input);
            Page<CorpUserVerificationDTO> page = corpUserVerificationService.getRequestsByCorpId(corporateUser.getCorporate().getId(), pageable);
            DataTablesOutput<CorpUserVerificationDTO> out = new DataTablesOutput<>();
            out.setDraw(input.getDraw());
            out.setData(page.getContent());
            out.setRecordsFiltered(page.getTotalElements());
            out.setRecordsTotal(page.getTotalElements());
            return out;
        }
        return null;
    }

    @GetMapping("/{id}/approvals")
    public String getObjectsForVerification(@PathVariable Long id, Model model, Principal principal) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(loggedInUser.getCorpUserType().equals(CorpUserType.INITIATOR)) {
            return "redirect:/corporate/dashboard";
        }

        CorpUserVerificationDTO verification = corpUserVerificationService.getVerification(id);
        model.addAttribute("verification", new CorpUserVerificationDTO());
        model.addAttribute("verify", verification);

        if (VerificationStatus.PENDING.equals(verification.getStatus())) {
            boolean status = true;
            model.addAttribute("status", status);
        }

        return "corp/user/details";
    }

    @PostMapping("/verify")
    public String verify(@ModelAttribute("verification") @Valid CorpUserVerificationDTO corpUserVerification, BindingResult result, WebRequest request, Model model, RedirectAttributes redirectAttributes, Locale locale) {

        String approval = request.getParameter("approve");

        try {
            if ("true".equals(approval)) {
                corpUserVerificationService.verify(corpUserVerification);
                redirectAttributes.addFlashAttribute("message", "Operation approved successfully");

            } else if ("false".equals(approval)) {
                if (result.hasErrors()) {
                    CorpUserVerificationDTO corpUserVerification2 = corpUserVerificationService.getVerification(corpUserVerification.getId());
                    model.addAttribute("verify", corpUserVerification2);
                    if (VerificationStatus.PENDING.equals(corpUserVerification2.getStatus())) {
                        boolean status = true;
                        model.addAttribute("status", status);
                    }
                    return "corp/user/details";
                }
                corpUserVerificationService.decline(corpUserVerification);
                redirectAttributes.addFlashAttribute("message", "Operation declined successfully");

            }
        } catch (InternetBankingException ve) {
            logger.error("Error verifying the operation", ve);
            redirectAttributes.addFlashAttribute("failure", ve.getMessage());
        }
        return "redirect:/corporate/users/approvals";
    }


    @GetMapping("/{userId}/account/permission")
    public String getAccountPermissions(@PathVariable Long userId, Model model, Principal principal) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }

        List<AccountPermissionDTO> accountPermissions = corporateUserService.getAccountPermissionsForAdminManagement(userId);
        CorporateUserDTO user = corporateUserService.getUser(userId);
        model.addAttribute("corporateUser", user);
        model.addAttribute("accountPermissions", accountPermissions);

        return "corp/user/accountpermission";
    }


    @PostMapping("/account/permission")
    public String UpdateAccountPermissions(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, WebRequest request, RedirectAttributes redirectAttributes, Principal principal, Locale locale) {

        CorporateUser loggedInUser = corporateUserService.getUserByName(principal.getName());
        if(!loggedInUser.getCorpUserType().equals(CorpUserType.ADMIN)) {
            return "redirect:/corporate/dashboard";
        }


        boolean permissionChanged = false;

        List<AccountPermissionDTO> existingPermissions = corporateUserService.getAccountPermissionsForAdminManagement(corporateUserDTO.getId());

        for (AccountPermissionDTO accountPermission : existingPermissions) {

            AccountPermissionDTO.Permission currentPermission = AccountPermissionDTO.Permission.valueOf(request.getParameter(accountPermission.getAccountNumber()));

            if (currentPermission != accountPermission.getPermission()) {
                permissionChanged = true;
                accountPermission.setPermission(currentPermission);
            }
        }
        corporateUserDTO.setAccountPermissions(existingPermissions);
        if (permissionChanged) {

            if(corporateUserDTO.getId().equals(loggedInUser.getId())){
                redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("account.permission.notallowed",null,locale));
                return "redirect:/corporate/users/";
            }

            try {

                if (makerCheckerService.isEnabled("UPDATE_ACCOUNT_PERMISSION_FROM_CORPORATE_ADMIN")) {
                    String message = corpUserVerificationService.updateAccountPermissionsFromCorporateAdmin(corporateUserDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                } else {
                    String message = corporateUserService.updateAccountPermissions(corporateUserDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                }

            } catch (InternetBankingException ibe) {
                logger.error("Failed to update corporate user account permissions", ibe);
                redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
                return "redirect:/corporate/users/" + corporateUserDTO.getId() + "/account/permission";

            } catch (Exception e) {
                logger.error("Failed to update corporate user account permissions", e);
                redirectAttributes.addFlashAttribute("failure", "Error occurred updating account permissions");
                return "redirect:/corporate/users/" + corporateUserDTO.getId() + "/account/permission";

            }
        }

        return "redirect:/corporate/users";
    }
}
