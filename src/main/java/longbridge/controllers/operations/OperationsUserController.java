package longbridge.controllers.operations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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
 * Created by Wunmi on 31/03/2017.
 */

@Controller
@RequestMapping("/ops/users")
public class OperationsUserController {
    @Autowired
    OperationsUserService operationsUserService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    PasswordService passwordService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/new")
    public String addUser(){
        return "/adm/operation/add";
    }

    @PostMapping
    public String createUser(@ModelAttribute("operationsUserForm") OperationsUserDTO operationsUser, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "adm/operation/add";
        }
        operationsUserService.addUser(operationsUser);
        model.addAttribute("success","Operations user created successfully");
        return "redirect:/ops/users";
    }

    @GetMapping
    public Iterable<OperationsUserDTO> getAllOperationsUsers(Model model){
        Iterable<OperationsUserDTO> operationsUserList= operationsUserService.getUsers();
        model.addAttribute("operationsUserList",operationsUserList);
        return operationsUserList;
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable Long userId, Model model){
        OperationsUserDTO user = operationsUserService.getUser(userId);
        model.addAttribute("operationsUser",user);
        return "operationsUserDetails";
    }

    @PostMapping("/{userId}")
    public String UpdateUser(@ModelAttribute("operationsUserForm") OperationsUserDTO user, @PathVariable Long userId, BindingResult result,Model model) throws Exception{
        if(result.hasErrors()){
            return "addUser";
        }
        user.setId(userId);
        boolean updated = operationsUserService.updateUser(user);
        if(updated) {
            model.addAttribute("success", "Operations user updated successfully");
        }
        return "updateUser";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        operationsUserService.deleteUser(userId);
        return "redirect:/ops/users";
    }


    @GetMapping("/password")
    public String changePassword(Model model){

        ChangePassword changePassword = new ChangePassword();
        model.addAttribute("changePassword", changePassword);
        model.addAttribute("passwordRules",passwordService.getPasswordRules());
        return "/ops/pword";
    }

    @PostMapping("/password")
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePassword changePassword, Principal principal, BindingResult result,  RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            result.addError(new ObjectError("invalid", "Please provide valid password"));
            return "/ops/pword";
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())){
            logger.trace("PASSWORD MISMATCH");
            result.addError(new ObjectError("invalid", "Passwords do not match"));
            return "/ops/pword";
        }

        OperationsUserDTO user = operationsUserService.getUserByName(principal.getName());

        if(!this.passwordEncoder.matches(changePassword.getOldPassword(),user.getPassword())){
            logger.trace("Invalid old password provided for change");
            result.addError(new ObjectError("invalid", "Incorrect Old Password"));
            return "/ops/pword";
        }


        operationsUserService.changePassword(user, changePassword.getOldPassword(), changePassword.getNewPassword());

        redirectAttributes.addFlashAttribute("message","Password changed successfully");
        return "redirect:/ops/logout";
    }
}

