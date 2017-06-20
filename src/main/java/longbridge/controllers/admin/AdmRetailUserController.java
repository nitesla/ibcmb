package longbridge.controllers.admin;

import longbridge.dtos.RetailUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.forms.ChangePassword;
import longbridge.models.RetailUser;
import longbridge.security.FailedLoginService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/retail/users")
public class AdmRetailUserController {
    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private FailedLoginService failedLoginService;

    @Autowired
    MessageSource messageSource;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @PostMapping
    public String createUser(@ModelAttribute("retailUser") RetailUserDTO retailUser, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){
            return "adm/retail/add";
        }
       // retailUserService.addUser(retailUser);
        redirectAttributes.addFlashAttribute("message","Retail user created successfully");
        return "redirect:/admin/retail/users";
    }


    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        RetailUserDTO retailUser = retailUserService.getUser(userId);
        model.addAttribute("retailUser", retailUser);
        return "/adm/retail/edit";
    }



    @GetMapping
    public String getAllRetailUsers(Model model){
        return "adm/retail/view";
    }

    @GetMapping("/all")
    public @ResponseBody
    DataTablesOutput<RetailUserDTO> getRetailUsers(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<RetailUserDTO> retailUsers = retailUserService.getUsers(pageable);
        DataTablesOutput<RetailUserDTO> out = new DataTablesOutput<RetailUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(retailUsers.getContent());
        out.setRecordsFiltered(retailUsers.getTotalElements());
        out.setRecordsTotal(retailUsers.getTotalElements());
        return out;
    }

    @GetMapping(path = "/list")
    public @ResponseBody
    Iterable<RetailUserDTO> getRetailUsers(){

        Iterable<RetailUserDTO> retailUsers = retailUserService.getUsers();

        return retailUsers;
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable  Long userId, Model model){
        RetailUserDTO retailUser = retailUserService.getUser(userId);
        model.addAttribute("user",retailUser);
        return "retailUserDetails";
    }


    @PostMapping("/update")
    public String UpdateUser(@ModelAttribute("retailUser") RetailUserDTO retailUser, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
       if(result.hasErrors()){
           return "adm/retail/add";
       }
        retailUserService.updateUser(retailUser);
        redirectAttributes.addFlashAttribute("message", "Retail user updated successfully");
        return "redirect:/admin/retail/users";
    }


    @GetMapping("/{id}/activation")
    public String changeActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = retailUserService.changeActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing user activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/retail/users";
    }


    @GetMapping("/{userId}/unlock")
    public String unlockUser(@PathVariable Long userId, RedirectAttributes redirectAttributes,Locale locale){

        try{
            String message = retailUserService.unlockUser(userId);

            redirectAttributes.addFlashAttribute("message",message);

        }
        catch (InternetBankingException e){
            logger.error("Error unlocking user",e.getMessage());
            redirectAttributes.addFlashAttribute("failure",e.getMessage());

        }

        return "redirect:/admin/retail/users";
    }

    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = retailUserService.resetPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for retail user", pe);
        }
        return "redirect:/admin/retail/users";
    }

    @GetMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        retailUserService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("message", "Retail user deleted successfully");

        return "redirect:/admin/retail/users";
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
        RetailUserDTO user= retailUserService.getUser(userId);
        String oldPassword=changePassword.getOldPassword();
        String newPassword=changePassword.getNewPassword();
        String confirmPassword=changePassword.getConfirmPassword();

        //validate password according to the defined password policy
        //The validations can be done on the ChangePassword class


        if(!newPassword.equals(confirmPassword)){
            logger.info("PASSWORD MISMATCH");
        }

        user.setPassword(newPassword);
        retailUserService.updateUser(user);
        logger.info("PASSWORD CHANGED SUCCESSFULLY");
        return "changePassword";
    }

}
