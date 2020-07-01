package longbridge.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.Confidence;
import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.models.User;
import longbridge.services.*;

import longbridge.validator.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import longbridge.utils.DataTablesUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

/**
 * Created by SYLVESTER on 31/03/2017.
 */
@Controller
@RequestMapping("/admin/users")
public class  AdminUserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private EmailValidator emailValidator;


    @GetMapping("/new")
    public String addUser(Model model) {
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("adminUser", new AdminUserDTO());
        model.addAttribute("roles", roles);
        return "/adm/admin/add";
    }



    @PostMapping
    public String createUser(@ModelAttribute("adminUser") @Valid AdminUserDTO adminUser, BindingResult result, RedirectAttributes redirectAttributes, Locale locale, Principal principal) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/admin/add";
        }


        if(!emailValidator.validate(adminUser.getEmail())){
            result.addError(new ObjectError("invalid", messageSource.getMessage("email.invalid",null,locale)));
            logger.error("Invalid User email {}", adminUser.getEmail());
            return "adm/admin/add";
        }

        try {
            String message = adminUserService.addUser(adminUser);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/users";
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating admin user {}", adminUser.getUserName(), doe);
            return "adm/admin/add";
        } catch (EntrustException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating admin user on Entrust", se);
            return "adm/admin/add";
        } catch (InternetBankingSecurityException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating admin user", se);
            return "adm/admin/add";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating admin user", ibe);
            return "adm/admin/add";
        }

    }




    @GetMapping("/all")
    public
    @ResponseBody
    DataTablesOutput<AdminUserDTO> getUsers(DataTablesInput input, @RequestParam("csearch") String search) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AdminUserDTO> adminUsers = null;
        if (StringUtils.isNoneBlank(search)) {
            adminUsers = adminUserService.findUsers(search, pageable);
        } else {
            adminUsers = adminUserService.getUsers(pageable);
        }
        DataTablesOutput<AdminUserDTO> out = new DataTablesOutput<AdminUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(adminUsers.getContent());
        out.setRecordsFiltered(adminUsers.getTotalElements());
        out.setRecordsTotal(adminUsers.getTotalElements());
        return out;
    }

    /**
     * Returns all users
     *
     * @param model
     * @return
     */
    @GetMapping
    public String getUsers(Model model) {
//        Iterable<AdminUser> adminUserList=adminUserService.getUsers();
//        model.addAttribute("adminUserList",adminUserList);
        return "adm/admin/view";
    }


    @GetMapping("/{userId}/details")
    public String getAdminUser(@PathVariable String userId, Model model) {
        AdminUser adminUser = adminUserService.getUser(Long.parseLong(userId));
        model.addAttribute("user", adminUser);
        return "admin/details";
    }

    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        AdminUserDTO user = adminUserService.getAdminUser(userId);
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("adminUser", user);
        model.addAttribute("roles", roles);
        return "adm/admin/edit";
    }


    @PostMapping("/update")
    public String updateUser(@ModelAttribute("adminUser") @Valid AdminUserDTO adminUser, BindingResult result, RedirectAttributes redirectAttributes, Locale locale, Principal principal) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/admin/edit";
        }


        if(!emailValidator.validate(adminUser.getEmail())){
            result.addError(new ObjectError("invalid", messageSource.getMessage("email.invalid",null,locale)));
            logger.error("Invalid User email {}", adminUser.getEmail());
            return "adm/admin/edit";
        }


        try {
            String message = adminUserService.updateUser(adminUser);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/users";
        } catch (DuplicateObjectException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Existing user found", ibe);
            return "adm/admin/edit";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error updating admin user", ibe);
            return "adm/admin/edit";
        }
    }

    @GetMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            String message = adminUserService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingSecurityException se) {
            redirectAttributes.addFlashAttribute("failure", se.getMessage());
            logger.error("Error deleting admin user", se);
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            logger.error("Error deleting admin user", ibe);
        }
        return "redirect:/admin/users";
    }

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("roles", roles);

    }

    @GetMapping("/{id}/activation")
    public String changeActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = adminUserService.changeActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing user activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/users";
    }


    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        if (verificationService.isPendingVerification(id, AdminUser.class.getSimpleName())) {
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/admin/users";

        }
        try {
            String message = adminUserService.resetPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for admin user", pe);
        } catch (InternetBankingException e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/password")
    public String changePassword(Model model) {
        ChangePassword changePassword = new ChangePassword();
        model.addAttribute("changePassword", changePassword);
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
        return "adm/admin/pword";
    }

    @PostMapping("/password")
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/admin/pword";
        }

        AdminUser user = adminUserService.getUserByName(principal.getName());
        try {
            String message = adminUserService.changePassword(user, changePassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/login/admin";
        } catch (WrongPasswordException wpe) {
            result.reject("oldPassword", wpe.getMessage());
            logger.error("Wrong password from admin user {}", user.getUserName(), wpe.toString());
            return "/adm/admin/pword";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword", pve.getMessage());
            logger.error("Password policy violation from admin user {} error {}", user.getUserName(), pve.toString());
            return "/adm/admin/pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from admin user {}", user.getUserName(), pme.toString());
            return "/adm/admin/pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for admin user {}", user.getUserName(), pe);
            return "/adm/admin/pword";
        }
    }


    @GetMapping("/password/new")
    public String changeDefaultPassword(Model model) {
        ChangeDefaultPassword changePassword = new ChangeDefaultPassword();
        model.addAttribute("changePassword", changePassword);
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
        return "adm/admin/new-pword";
    }


    @PostMapping("/password/new")
    public String changeDefaultPassword(@ModelAttribute("changePassword") @Valid ChangeDefaultPassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest httpServletRequest) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/admin/new-pword";
        }

        AdminUser user = adminUserService.getUserByName(principal.getName());
        try {
            String message = adminUserService.changeDefaultPassword(user, changePassword);
            redirectAttributes.addFlashAttribute("message", message);
            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }

            SettingDTO setting = configService.getSettingByName("ENABLE_ADMIN_2FA");
            boolean tokenAuth = false;
            if (setting != null && setting.isEnabled()) {
                tokenAuth = (setting.getValue().equalsIgnoreCase("yes") ? true : false);
            }

            if (tokenAuth) {
                return "redirect:/admin/token";
            }

            return "redirect:/admin/dashboard";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword", pve.getMessage());
            logger.error("Password policy violation from admin user {}", user.getUserName(), pve);
            return "/adm/admin/new-pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from admin user {}", user.getUserName(), pme.toString());
            return "/adm/admin/new-pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for admin user {}", user.getUserName(), pe);
            return "/adm/admin/new-pword";
        }
    }


    @GetMapping("/transfersettings")
    public String adminSetTransferSettings(){
        return "transfersettings";
    }

    @ResponseBody
    @PostMapping("/submitTransferLimitForCoronationAccounts")
    public String submitTransferLimitDetailsForCoronationAccounts(@RequestParam("accountNumber") String accountNumber,
                                                                  @RequestParam("accountClass") String accountClass,
                                                                  @RequestParam("limit") String limit,
                                                                  @RequestParam("frequency") String frequency,
                                                                  @RequestParam("bankUserType") String bankUserType,
                                                                  @RequestParam("bankChoice") String bankChoice){
        logger.info("accountNumber = " + accountNumber);
        logger.info("accountClass = " + accountClass);
        logger.info("limit = " + limit);
        logger.info("frequency = " + frequency);
        logger.info("bank user-type = " + bankUserType);
        logger.info("bankchoice = " + bankChoice);
        return "success";
    }


    @ResponseBody
    @PostMapping("/submitTransferLimitForOtherBankAccounts")
    public String submitTransferLimitForOtherBanks(@RequestParam("accountNumber") String accountNumber,
                                                   @RequestParam("accountClass") String accountClass,
                                                   @RequestParam("limit") String limit,
                                                   @RequestParam("frequency") String frequency,
                                                   @RequestParam("bankUserType") String bankUserType,
                                                   @RequestParam("bankChoice") String bankChoice,
                                                   @RequestParam("transferPlatform") String transferPlatform){
        logger.info("accountNumber = " + accountNumber);
        logger.info("accountClass = " + accountClass);
        logger.info("limit = " + limit);
        logger.info("frequency = " + frequency);
        logger.info("bank user-type = " + bankUserType);
        logger.info("bankchoice = " + bankChoice);
        logger.info("transferPlatform = " + transferPlatform);

        return "success";
    }

    @ResponseBody
    @PostMapping("/submitTransferFeePercentage")
    public String submitTransferFeePercentage(@RequestParam("percentage") String percentage,
                                              @RequestParam("transferMethod") String transferMethod,
                                              @RequestParam("feeType") String feeType){
        logger.info("percentage = " + percentage);
        logger.info("transfer method = " + transferMethod);
        logger.info("fee type = " + feeType);
    return "success";
    }

    @ResponseBody
    @PostMapping("/submitTransferFeeFixedAmount")
    public String submitTransferFeeFixedAmount(@RequestParam("fixedRate") String fixedRate,
                                               @RequestParam("transferMethod") String transferMethod,
                                               @RequestParam("feeType") String feeType){
        logger.info("fixedRate = " + fixedRate);
        logger.info("transfer method = " + transferMethod);
        logger.info("fee type = " + feeType);
        return "success";
    }

    @ResponseBody
    @PostMapping("/submitTransferAdjustments")
    public String submitTransferAdjustments(@RequestParam("fixedRate") String fixedRate,
                                            @RequestParam("transferMethod") String transferMethod,
                                            @RequestParam("feeType") String feeType,
                                            @RequestParam("percentage") String percentage){
        logger.info("fixedRate = " + fixedRate);
        logger.info("transfer method = " + transferMethod);
        logger.info("fee type = " + feeType);
        logger.info("percentage " + percentage);
        return "success";

    }

}
