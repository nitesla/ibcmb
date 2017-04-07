package longbridge.controllers.admin;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import static javax.swing.text.html.HTML.Tag.HEAD;

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
    private ModelMapper modelMapper;

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
     * @param adminUserDTO
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping
    public String createUser(@ModelAttribute("user") AdminUserDTO adminUserDTO, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "admin/add";
        }

        AdminUser adminUser =modelMapper.map(adminUserDTO,AdminUser.class);
        adminUserService.addUser(adminUser);
        model.addAttribute("success","Admin user created successfully");
        return "redirect:/admin/list";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {

        AdminUser user = adminUserService.getUser(userId);
        AdminUserDTO userDTO = modelMapper.map(user,AdminUserDTO.class);
        model.addAttribute("user", userDTO);
        return "admin/edit";
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
    @GetMapping("/{userId}/details")
    public String getAdminUser(@PathVariable  String userId, Model model){
       AdminUser user =adminUserService.getUser(Long.parseLong(userId));
       AdminUserDTO adminUserDTO = modelMapper.map(user,AdminUserDTO.class);
       model.addAttribute("user",adminUserDTO);
       return "admin/details";
    }

    /**
     * Updates the user
     * @param adminUserDTO
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/{userId}")
    public String updateUser(@ModelAttribute("user") @Valid AdminUserDTO adminUserDTO, @PathVariable Long userId, BindingResult result, Model model) throws Exception{
      if(result.hasErrors()) {
          return "addUser";
      }
         adminUserDTO.setId(userId);
         AdminUser adminUser = modelMapper.map(adminUserDTO,AdminUser.class);
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
       if(result.hasErrors()){
            return "password";
        }
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
