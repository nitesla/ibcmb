package longbridge.controllers.operations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.exception.PasswordException;
import longbridge.exception.PasswordMismatchException;
import longbridge.exception.PasswordPolicyViolationException;
import longbridge.exception.WrongPasswordException;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

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
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

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
        String message = operationsUserService.updateUser(user);
            model.addAttribute("message", message);

        return "updateUser";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        operationsUserService.deleteUser(userId);
        return "redirect:/ops/users";
    }


    @GetMapping("/password")
    public String changePassword(Model model){
        model.addAttribute("changePassword",new ChangePassword());
        return "/ops/pword";
    }

    @PostMapping("/password")
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes,Locale locale){
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required",null,locale)));
            return "/ops/pword";
        }

        OperationsUser user = operationsUserService.getUserByName(principal.getName());
        try {
            String message = operationsUserService.changePassword(user, changePassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/dashboard";
        } catch (WrongPasswordException wpe) {
            result.reject("oldPassword", wpe.getMessage());
            logger.error("Wrong password from operation user {}", user.getUserName(), wpe.toString());
            return "/ops/pword";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword",  pve.getMessage());
            logger.error("Password policy violation from operation user {}", user.getUserName(), pve.toString());
            return "/ops/pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from operation user {}", user.getUserName(), pme.toString());
            return "/ops/pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for operation user {}", user.getUserName(), pe);
            return "/ops/pword";
        }

    }

    @GetMapping("/password/new")
    public String changeDefaultPassword(Model model) {
        ChangeDefaultPassword changePassword = new ChangeDefaultPassword();
        model.addAttribute("changePassword", changePassword);
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
        return "ops/new-pword";
    }


    @PostMapping("/password/new")
    public String changeDefaultPassword(@ModelAttribute("changePassword") @Valid ChangeDefaultPassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes,Locale locale,HttpServletRequest httpServletRequest) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required",null,locale)));
            return "/ops/new-pword";
        }

        OperationsUser user = operationsUserService.getUserByName(principal.getName());
        try {
            String message = operationsUserService.changeDefaultPassword(user, changePassword);
            redirectAttributes.addFlashAttribute("message", message);
            if (httpServletRequest.getSession().getAttribute("expired-password") != null) {
                httpServletRequest.getSession().removeAttribute("expired-password");
            }
            return "redirect:/ops/dashboard";
        } catch (PasswordPolicyViolationException pve) {
            result.reject("newPassword", pve.getMessage());
            logger.error("Password policy violation from operations user {}", user.getUserName(), pve);
            return "/ops/new-pword";
        } catch (PasswordMismatchException pme) {
            result.reject("confirmPassword", pme.getMessage());
            logger.error("New password mismatch from operations user {}", user.getUserName(), pme.toString());
            return "/ops/new-pword";
        } catch (PasswordException pe) {
            result.addError(new ObjectError("error", pe.getMessage()));
            logger.error("Error changing password for admin user {}", user.getUserName(), pe);

            return "/ops/new-pword";
        }
    }

}

