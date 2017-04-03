package longbridge.controllers.customer;

import longbridge.formValidations.ChangePassword;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.CorporateUserService;
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
 * Created by Fortune on 4/3/2017.
 */

@RestController
@RequestMapping("/corporate/user")
public class CorporateUserController {
    @Autowired
    CorporateUserService corporateUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/add")
    public String addUser(){
        return "addUser";
    }

    @PostMapping("/add")
    public String createUser(CorporateUser corporateUser, Model model) throws Exception{
        corporateUserService.addUser(corporateUser);
        model.addAttribute("success","Corporate user created successfully");
        return "addUser";
    }

    @GetMapping("/all")
    public Iterable<CorporateUser> getAllRetailUsers(Model model){
        Iterable<CorporateUser> corporateUserList= corporateUserService.getUsers();
        model.addAttribute("corporateUserList",corporateUserList);
        return corporateUserList;
    }

    @GetMapping("/user")
    public String getUser(Long userId, Model model){
        CorporateUser user = corporateUserService.getUser(userId);
        model.addAttribute("corporateUser",user);
        return "corporateUser";
    }

    @PostMapping("/update")
    public String UpdateUser(CorporateUser user, Model model) throws Exception{
        boolean result = corporateUserService.updateUser(user);
        if(result) {
            model.addAttribute("success", "Corporate user updated successfully");
        }
        return "updateUser";
    }

    @GetMapping("/changePassword")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@Valid ChangePassword changePassword, Long userId, BindingResult result, HttpRequest request, Model model){
        /* if(result.hasError()){
        }*/
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
