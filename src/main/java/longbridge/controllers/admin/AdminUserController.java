package longbridge.controllers.admin;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import longbridge.services.PasswordService;
import longbridge.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by SYLVESTER on 31/03/2017.
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private  AdminUserService adminUserService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    PasswordService passwordService;


    /**
     * Page for adding a new user
     * @return
     */
    @GetMapping("/new")
    public String addUser(Model model){
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("adminUser", new AdminUserDTO());
        model.addAttribute("roles",roles);
        return "adm/admin/add";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        AdminUserDTO user = adminUserService.getAdminUser(userId);
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("adminUser", user);
        model.addAttribute("roles",roles);
        return "adm/admin/edit";
    }

    /**
     * Creates a new user
     * @param adminUser
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping
    public String createUser(@ModelAttribute("adminUser") @Valid AdminUserDTO adminUser, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){

            result.addError(new ObjectError("invalid","Please fill in the required fields"));
            return "adm/admin/add";
        }
        if(!adminUserService.isValidUsername(adminUser.getUserName())){
            result.addError(new ObjectError("invalid","Username already exists"));
            return "adm/admin/add";
        }

        adminUserService.addUser(adminUser);

        redirectAttributes.addFlashAttribute("message","Admin user created successfully");
        return "redirect:/admin/users";
    }


    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<AdminUserDTO> getUsers(DataTablesInput input){
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
     * @param model
     * @return
     */
    @GetMapping
    public String getUsers(Model model){
//        Iterable<AdminUser> adminUserList=adminUserService.getUsers();
//        model.addAttribute("adminUserList",adminUserList);
        return "adm/admin/view";
    }
    /**
     * Returns user
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/{userId}/details")
    public String getAdminUser(@PathVariable  String userId, Model model){
       AdminUser adminUser =adminUserService.getUser(Long.parseLong(userId));
       model.addAttribute("user",adminUser);
       return "admin/details";
    }

    /**
     * Updates the user
     * @param adminUser
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") AdminUserDTO adminUser, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
      if(result.hasErrors()) {
          return "adm/admin/add";
      }
          boolean updated = adminUserService.updateUser(adminUser);
          if (updated) {
              redirectAttributes.addFlashAttribute("message", "Admin user updated successfully");
          }
        return "redirect:/admin/users";
    }

    @GetMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId){
        adminUserService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @ModelAttribute
    public void init(Model model){
        model.addAttribute("passwordRules",passwordService.getPasswordRules());
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("roles",roles);

    }

    @GetMapping("/{id}/activation")
    public String changeActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes){
        adminUserService.changeActivationStatus(id);
        redirectAttributes.addFlashAttribute("message", "User activation status changed successfully");
        return "redirect:/admin/users";
    }


    @GetMapping("/{id}/password/reset")
        public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes){
        if(adminUserService.resetPassword(id)) {
            redirectAttributes.addFlashAttribute("message", "Password reset successfully");
        }
        return "redirect:/admin/users";
        }

    @GetMapping("/password")
    public String changePassword(Model model){

        ChangePassword changePassword = new ChangePassword();
        model.addAttribute("changePassword", changePassword);
        model.addAttribute("passwordRules",passwordService.getPasswordRules());
        return "adm/admin/pword";
    }

    @PostMapping("/password")
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePassword changePassword,BindingResult result, Principal principal,   RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            result.addError(new ObjectError("invalid", "Please provide valid password"));
            return "/adm/admin/pword";
        }

        AdminUserDTO user = adminUserService.getUserByName(principal.getName());

        if(!this.passwordEncoder.matches(changePassword.getOldPassword(),user.getPassword())){
            logger.trace("Invalid old password provided for change");
            result.addError(new ObjectError("invalid", "Incorrect Old Password"));
            return "/adm/admin/pword";
        }

        String errorMsg = passwordService.validate(changePassword.getNewPassword());
        if(!errorMsg.equals("")){
            result.addError(new ObjectError("invalid", errorMsg));
            return "/adm/admin/pword";
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())){
            logger.trace("PASSWORD MISMATCH");
            result.addError(new ObjectError("invalid", "Passwords do not match"));
            return "/adm/admin/pword";
        }


        if(!this.passwordEncoder.matches(changePassword.getOldPassword(),user.getPassword())){
            logger.trace("Invalid old password provided for change");
            result.addError(new ObjectError("invalid", "Incorrect Old Password"));
            return "/adm/admin/pword";
        }


        adminUserService.changePassword(user, changePassword.getOldPassword(), changePassword.getNewPassword());
        redirectAttributes.addFlashAttribute("message","Password changed successfully");
        return "redirect:/admin/logout";
    }

}
