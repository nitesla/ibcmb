package longbridge.controllers.admin;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
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

/**
 * Created by SYLVESTER on 31/03/2017.
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    MessageSource messageSource;


    /**
     * Page for adding a new user
     *
     * @return
     */
    @GetMapping("/new")
    public String addUser(Model model) {
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("adminUser", new AdminUserDTO());
        model.addAttribute("roles", roles);
        return "adm/admin/add";
    }


    /**
     * Creates a new user
     *
     * @param adminUser
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping
    public String createUser(@ModelAttribute("adminUser") @Valid AdminUserDTO adminUser, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
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
        }

        catch (EntrustException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating admin user on Entrust", se);
            return "adm/admin/add";
        }
        catch (InternetBankingSecurityException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating admin user", se);
            return "adm/admin/add";
        }
        catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating admin user", ibe);
            return "adm/admin/add";
        }

    }


    @GetMapping("/all")
    public
    @ResponseBody
    DataTablesOutput<AdminUserDTO> getUsers(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AdminUserDTO> adminUsers = adminUserService.getUsers(pageable);
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

    /**
     * Returns user
     *
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/{userId}/details")
    public String getAdminUser(@PathVariable String userId, Model model) {
        AdminUser adminUser = adminUserService.getUser(Long.parseLong(userId));
        model.addAttribute("user", adminUser);
        return "admin/details";
    }

    /**
     * Edit an existing user
     *
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        AdminUserDTO user = adminUserService.getAdminUser(userId);
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("adminUser", user);
        model.addAttribute("roles", roles);
        return "adm/admin/edit";
    }


    /**
     * Updates the user
     *
     * @param adminUser
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("adminUser") @Valid AdminUserDTO adminUser, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/admin/edit";
        }
        try {
            String message = adminUserService.updateUser(adminUser);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/users";
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
        }
        catch (InternetBankingSecurityException se) {
            redirectAttributes.addFlashAttribute("failure", se.getMessage());
            logger.error("Error deleting admin user", se);
        }
        catch (InternetBankingException ibe) {
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
        try {
            String message = adminUserService.resetPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for admin user", pe);
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
            return "redirect:/admin/dashboard";
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


}
