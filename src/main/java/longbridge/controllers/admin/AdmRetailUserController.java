package longbridge.controllers.admin;

import longbridge.dtos.ChangePassword;
import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

/**
 * Created by Fortune on 4/3/2017.
 */

@RestController
@RequestMapping("/retail/users")
public class AdmRetailUserController {
    @Autowired
    RetailUserService retailUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());



    @GetMapping("/new")
    public String addUser(){
        return "addUser";
    }



    @PostMapping
    public String createUser(@ModelAttribute("retailUserForm") RetailUser retailUser, BindingResult result,Model model) throws Exception{
        if(result.hasErrors()){
            return "addUser";
        }
        retailUserService.addUser(retailUser);
        model.addAttribute("success","Retail user created successfully");
        return "redirect:/retail/users";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        RetailUser user = retailUserService.getUser(userId);
        model.addAttribute("user", user);
        return "addUser";
    }

    @GetMapping
    public Iterable<RetailUser> getAllRetailUsers(Model model){
        Iterable<RetailUser> retailUserList= retailUserService.getUsers();
        model.addAttribute("retailUserList",retailUserList);
        return retailUserList;
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable  Long userId, Model model){
        RetailUser user = retailUserService.getUser(userId);
        model.addAttribute("retailUser",user);
        return "retailUserDetails";
    }

    @PostMapping("/{userId}")
    public String UpdateUser(@ModelAttribute("retailUserForm") RetailUser retailUser, @PathVariable Long userId, BindingResult result, Model model) throws Exception{
       if(result.hasErrors()){
           return "addUser";
       }
       retailUser.setId(userId);
        boolean updated = retailUserService.updateUser(retailUser);
       if(updated) {
           model.addAttribute("success", "Retail user updated successfully");
       }
        return "redirect:/retail/users";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        retailUserService.deleteUser(userId);
        return "redirect:/retail/users";
    }

    @GetMapping("/password")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/password")
    public String changePassword(@Validated ChangePassword changePassword, Long userId, BindingResult result, HttpRequest request, Model model){
         if(result.hasErrors()){
             return "changePassword";
        }
        RetailUser user= retailUserService.getUser(userId);
        String oldPassword=changePassword.getOldPassword();
        String newPassword=changePassword.getNewPassword();
        String confirmPassword=changePassword.getConfirmPassword();

        //validate password according to the defined password policy
        //The validations can be done on the ChangePassword class


        if(!newPassword.equals(confirmPassword)){
            logger.info("PASSWORD MISMATCH");
        }

        user.setPassword(newPassword);
        retailUserService.addUser(user);
        logger.info("PASSWORD CHANGED SUCCESSFULLY");
        return "changePassword";
    }

}
