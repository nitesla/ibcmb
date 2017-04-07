package longbridge.controllers.admin;

import longbridge.dtos.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by SYLVESTER on 31/03/2017.
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
   private  AdminUserService adminUserService;

    /**
     * Page for adding a new user
     * @return
     */
    @GetMapping("/new")
    public String addUser(){
        return "admin/add";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {

        AdminUser user = adminUserService.getUser(userId);
        model.addAttribute("user", user);
        return "addUser";
    }

    /**
     * Creates a new user
     * @param adminUser
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping
    public String createUser(@ModelAttribute("adminUserForm") AdminUser adminUser, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "addUser";
        }
        adminUserService.addUser(adminUser);
        model.addAttribute("success","Admin user created successfully");
        return "redirect:/admin/users";
    }

    /**
     * Returns all users
     * @param model
     * @return
     */
    @GetMapping
    public Iterable<AdminUser> getUsers(Model model){
        Iterable<AdminUser> adminUserList=adminUserService.getUsers();
        model.addAttribute("adminUserList",adminUserList);
        return adminUserList;
    }

    /**
     * Returns user
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/{userId}")
    public String getAdminUser(@PathVariable  Long userId, Model model){
       AdminUser user =adminUserService.getUser(userId);
       model.addAttribute("adminUser",user);
       return "adminUserDetails";
    }

    /**
     * Updates the user
     * @param adminUser
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/{userId}")
    public String updateUser(@ModelAttribute("adminUserForm") @Validated AdminUser adminUser, @PathVariable Long userId, BindingResult result, Model model) throws Exception{
      if(result.hasErrors()) {
          return "addUser";
      }
         adminUser.setId(userId);
          boolean updated = adminUserService.updateUser(adminUser);
          if (updated) {
              model.addAttribute("success", "Admin user updated successfully");
          }
        return "redirect:/admin/users";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId){
        adminUserService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/password")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/password")
    public String changePassword(@Valid ChangePassword changePassword,Long userId, BindingResult result, HttpRequest request, Model model){
/*        if(result.hasError()){

        }*/
        AdminUser user=adminUserService.getUser(userId);
        String oldPassword=changePassword.getOldPassword();
        String newPassword=changePassword.getNewPassword();
        String confirmPassword=changePassword.getConfirmPassword();

        //TODO validate password according to the defined password policy
        //The validations can be done on the ChangePassword class

        if(!newPassword.equals(confirmPassword)){
            logger.info("PASSWORD MISMATCH");
        }

        user.setPassword(newPassword);
        adminUserService.addUser(user);
        logger.trace("Password for user {} changed successfully",user.getUserName());
        return "changePassword";
    }

}
