package longbridge.controllers.operations;

import longbridge.api.AccountInfo;
import longbridge.api.CustomerDetails;
import longbridge.dtos.*;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/ops/corporates/users/")
public class OpsCorporateUserController {
    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private RoleService roleService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private VerificationService verificationService;
    @Autowired
    private MakerCheckerService makerCheckerService;


    @ModelAttribute
    public void init(Model model) {
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


    @GetMapping("{corpId}/new")
    public String addUser(@PathVariable Long corpId, Model model, RedirectAttributes redirectAttributes) {
        CorporateDTO corporateDTO = corporateService.getCorporate(corpId);
//        if (corporateDTO.getCorporateType().equals("SOLE")) {
//            redirectAttributes.addFlashAttribute("failure", "Corporate entity has sole user");
//            return "redirect:/ops/corporates";
//        }

        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "/ops/corporate/addUser";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, HttpSession session, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        CorporateDTO corporateDTO = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
        model.addAttribute("corporate", corporateDTO);

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/ops/corporate/addUser";
        }
        try {
            String message = corporateUserService.addUser(corporateUserDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates/" + corporateUserDTO.getCorporateId() + "/view";


        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user ", doe);
            return "/ops/corporate/addUser";

        } catch (InternetBankingSecurityException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating corporate user on Entrust ", se);
            return "/ops/corporate/addUser";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);
            return "/ops/corporate/addUser";
        }
    }


    @GetMapping("/{userId}/unlock")
    public String unlockUser(@PathVariable Long userId, RedirectAttributes redirectAttributes, Locale locale) {

        String corpId = corporateUserService.getUser(userId).getCorporateId();

        if (verificationService.isPendingVerification(userId, CorporateUser.class.getSimpleName())) {
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/ops/corporates/" + corpId + "/view";
        }

        try {
            String message = corporateUserService.unlockUser(userId);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException e) {
            logger.error("Error unlocking user", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/ops/corporates/" + corpId + "/view";
    }

    @GetMapping("/{userId}/edit")
    public String getUser(@PathVariable Long userId, Model model) {
        CorporateUserDTO user = corporateUserService.getUser(userId);
        model.addAttribute("corporateUser", user);
        return "/ops/corporate/editUser";
    }

    @PostMapping("edit")
    public String UpdateUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale, Model model) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            CorporateUserDTO user = corporateUserService.getUser(corporateUserDTO.getId());
            return "/ops/corporate/editUser";
        }
        try {
            String message = corporateUserService.updateUser(corporateUserDTO);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Failed to update corporate user", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            return "redirect:/ops/corporates/users/" + corporateUserDTO.getId() + "/edit";

        }

        return "redirect:/ops/corporates/" + corporateUserDTO.getCorporateId() + "/view";
    }


    @GetMapping("/{id}/activation")
    public String changeUserActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String corpId = corporateUserService.getUser(id).getCorporateId();

        try {
            String message = corporateUserService.changeActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing corporate activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/ops/corporates/" + corpId + "/view";
    }

    @GetMapping("{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        String corpId = corporateUserService.getUser(userId).getCorporateId();

        try {
            String message = corporateUserService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error deleting user", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/ops/corporates/" + corpId + "/view";

    }

    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String corpId = corporateUserService.getUser(id).getCorporateId();

        if (verificationService.isPendingVerification(id, CorporateUser.class.getSimpleName())) {
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/ops/corporates/" + corpId + "/view";
        }

        try {
            String message = corporateUserService.resetPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for corporate user", pe);
        }
        return "redirect:/ops/corporates/" + corpId + "/view";
    }


    @GetMapping("/{id}/securityquestion/reset")
    public String resetSecurityQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {


        if (verificationService.isPendingVerification(id, CorporateUser.class.getSimpleName())) {
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/ops/retail/users";

        }

        try {
            String message = corporateUserService.resetSecurityQuestion(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/ops/retail/users";
    }


    @GetMapping("new/entity")
    public String newCorporate() {
        return "/ops/corporate/newCorporate";
    }

    @GetMapping("corp/new")
    public Map<String, List<String>> addCorporateEntity(WebRequest webRequest, HttpSession session, Locale locale) {
        logger.info("ggg");
        String custId = webRequest.getParameter("customerId");
        Map<String, List<String>> accountDetails = new HashMap<>();
        List<String> accountNum = new ArrayList<>();
        List<String> accountName = new ArrayList<>();
        if (custId == null) {
//            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return null;
        }
        CustomerDetails customerDetails = integrationService.viewCustomerDetailsByCif(custId);

        if (customerDetails.getCustomerName() == null || !customerDetails.isCorp()) {
//            result.addError(new ObjectError("invalid", messageSource.getMessage("corp.cifid.invalid", null, locale)));
            return null;

        }


        CorporateRequestDTO corporateRequestDTO = new CorporateRequestDTO();
        corporateRequestDTO.setCustomerId(custId);
//        corporateRequestDTO.setCorporateType(customerDetails.get);
//        corporateRequestDTO.setCustomerName(customerDetails.getCustomerName());
//        corporate.setCustomerName(customerDetails.getCustomerName());
        session.setAttribute("corporateRequest", corporateRequestDTO);

        List<AccountInfo> accountInfos = integrationService.fetchAccounts(custId.toUpperCase());
        if (accountInfos.size() > 0) {
            for (AccountInfo acctInfo : accountInfos) {
                logger.info("the acount number {}", acctInfo.getAccountNumber());
                accountNum.add(acctInfo.getAccountNumber());
                accountName.add(acctInfo.getAccountName());
            }
            accountDetails.put("accountNum", accountNum);
            accountDetails.put("accountName", accountName);
        }
        return accountDetails;

    }

    @GetMapping("/{userId}/accountpermission")
    public String getAccountPermissions(@PathVariable Long userId, Model model) {

        List<AccountPermissionDTO> accountPermissions = corporateUserService.getAccountPermissions(userId);
        CorporateUserDTO user = corporateUserService.getUser(userId);
        model.addAttribute("corporateUser", user);
        model.addAttribute("accountPermissions", accountPermissions);


        return "ops/corporate/accountpermission";
    }

    @PostMapping("/accountpermission")
    public String UpdateAccountPermissions(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, WebRequest request, RedirectAttributes redirectAttributes) {

        boolean permissionChanged = false;

        List<AccountPermissionDTO> existingPermissions = corporateUserService.getAccountPermissions(corporateUserDTO.getId());
        logger.info("existing PermissionsOld {}",existingPermissions);
        List<AccountPermissionDTO> removePermissions=new ArrayList<>();
        for (AccountPermissionDTO accountPermission : existingPermissions) {
            try {
                AccountPermissionDTO.Permission currentPermission = AccountPermissionDTO.Permission.valueOf(request.getParameter(accountPermission.getAccountNumber()));

                if (currentPermission != accountPermission.getPermission()) {
                    permissionChanged = true;
                    accountPermission.setPermission(currentPermission);
                }
            }catch(NullPointerException e){
                removePermissions.add(accountPermission);
                logger.info("remove permission {}",removePermissions);

            }
        }
        if(removePermissions.size()>-1) existingPermissions.removeAll(removePermissions);
        logger.info("existing PermissionsNew {}",existingPermissions);

        corporateUserDTO.setAccountPermissions(existingPermissions);
        if (permissionChanged) {
            try {

                if (makerCheckerService.isEnabled("UPDATE_USER_ACCOUNT_PERMISSION")) {
                    String message = verificationService.add(corporateUserDTO, "UPDATE_USER_ACCOUNT_PERMISSION", "Update corporate user account permission");
                    redirectAttributes.addFlashAttribute("message", message);
                } else {
                    String message = corporateUserService.updateAccountPermissions(corporateUserDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                }

            } catch (InternetBankingException ibe) {
                logger.error("Failed to update corporate user account permissions", ibe);
                redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
                return "redirect:/ops/corporates/users/" + corporateUserDTO.getId() + "/accountpermission";

            } catch (Exception e) {
                logger.error("Failed to update corporate user account permissions", e);
                redirectAttributes.addFlashAttribute("failure", "Error occurred updating account permissions");
                return "redirect:/ops/corporates/users/" + corporateUserDTO.getId() + "/accountpermission";

            }
        }

        return "redirect:/ops/corporates/" + corporateUserDTO.getCorporateId() + "/view";
    }


}
