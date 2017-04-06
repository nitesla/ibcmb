package longbridge.controllers.admin;

import longbridge.dtos.ChangePassword;
import longbridge.dtos.CorporateUserDTO;
import longbridge.models.CorporateUser;
import longbridge.services.CorporateUserService;
import org.modelmapper.ModelMapper;
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
@RequestMapping("/admin/corporate/users")
public class AdmCorporateUserController {
    @Autowired
    CorporateUserService corporateUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/new")
    public String addUser(){
        return "addUser";
    }

    @PostMapping
    public String createUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "addUser";
        }
        CorporateUser corporateUser = modelMapper.map(corporateUserDTO,CorporateUser.class);
        corporateUserService.addUser(corporateUser);
        model.addAttribute("success","Corporate user created successfully");
        return "redirect:/corporate/users";
    }

    @GetMapping
    public Iterable<CorporateUser> getRetailUsers(Model model){
        Iterable<CorporateUser> corporateUserList= corporateUserService.getUsers();
        model.addAttribute("corporateUserList",corporateUserList);
        return corporateUserList;
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable Long userId, Model model){
        CorporateUser user = corporateUserService.getUser(userId);
        CorporateUserDTO corporateUserDTO = modelMapper.map(user,CorporateUserDTO.class);
        model.addAttribute("corporateUser",corporateUserDTO);
        return "corporateUser";
    }

    @PostMapping("/{userId}")
    public String UpdateUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, @PathVariable Long userId, BindingResult result,Model model) throws Exception{
        if(result.hasErrors()){
            return "addUser";
        }
        corporateUserDTO.setId(userId);
        CorporateUser corporateUser = modelMapper.map(corporateUserDTO,CorporateUser.class);
        boolean updated = corporateUserService.updateUser(corporateUser);
        if(updated) {
            model.addAttribute("success", "Corporate user updated successfully");
        }
        return "redirect:/corporate/users";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        corporateUserService.deleteUser(userId);
        return "redirect:/corporate/users";
    }

    @GetMapping("/changePassword")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@Valid ChangePassword changePassword, Long userId, BindingResult result, HttpRequest request, Model model){
         if(result.hasErrors()){
             return "changePassword";
        }
        CorporateUser user= corporateUserService.getUser(userId);
        String oldPassword=changePassword.getOldPassword();
        String newPassword=changePassword.getNewPassword();
        String confirmPassword=changePassword.getConfirmPassword();

        //TODO validate password according to the defined password policy
        //The validations can be done on the ChangePassword class


        if(!newPassword.equals(confirmPassword)){
            logger.info("PASSWORD MISMATCH");
        }

        user.setPassword(newPassword);
        corporateUserService.addUser(user);
        logger.info("PASSWORD CHANGED SUCCESSFULLY");
        return "changePassword";
    }

}
