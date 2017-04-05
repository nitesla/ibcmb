package longbridge.controllers.admin;

import longbridge.formValidations.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * Created by SYLVESTER on 31/03/2017.
 */
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    AdminUserService adminUserService;

    /**
     * Edit a new user
     * @return
     */
    @GetMapping("/edit")
    public String addUser(){
        return "addUser";
    }

    /**
     * Edit a existing user
     * @return
     */
    @GetMapping("/{user}/edit")
    public String editUser(){
        return "addUser";
    }

    /**
     * Creates a new user
     * @param adminUser
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping()
    public String createUser( AdminUser adminUser, Model model) throws Exception{
        adminUserService.addUser(adminUser);
        model.addAttribute("success","Admin user created successfully");
        return "addUser";
    }

    /**
     * Returnsa all users
     * @param model
     * @return
     */
    @GetMapping()
    public Iterable<AdminUser> getAllAdminUsers(Model model){
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
    @GetMapping("/{user}")
    public String getAdminUser(Long userId, Model model){
       AdminUser user =adminUserService.getUser(userId);
       model.addAttribute("adminUser",user);
       return "adminUser";
    }

    /**
     * Updates the user
     * @param adminUser
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/{user}")
    public String updateUser( AdminUser adminUser, Model model) throws Exception{
       boolean result = adminUserService.updateUser(adminUser);
       if(result) {
           model.addAttribute("success", "Admin user updated successfully");
       }
        return "addUser";
    }

    @PostMapping("/{user}/delete")
    public String deleteUser(AdminUser user){
        //TODO
        return "adminUsers";
    }

    @GetMapping("/changePassword")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@Valid ChangePassword changePassword,Long userId,BindingResult result, HttpRequest request, Model model){
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
        logger.info("PASSWORD CHANGED SUCCESSFULLY");
        return "changePassword";
    }

}
