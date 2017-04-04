package longbridge.controllers.admin;

import longbridge.formValidations.ChangePassword;
import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;
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
@RequestMapping("/retail/user")
public class AdmRetailUserController {
    @Autowired
    RetailUserService retailUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/add")
    public String addUser(){
        return "addUser";
    }

    @PostMapping("/add")
    public String createUser(RetailUser retailUser, Model model) throws Exception{
        retailUserService.addUser(retailUser);
        model.addAttribute("success","Retail user created successfully");
        return "addUser";
    }

    @GetMapping("/all")
    public Iterable<RetailUser> getAllRetailUsers(Model model){
        Iterable<RetailUser> retailUserList= retailUserService.getUsers();
        model.addAttribute("retailUserList",retailUserList);
        return retailUserList;
    }

    @GetMapping("/user")
    public String getUser(Long userId, Model model){
        RetailUser user = retailUserService.getUser(userId);
        model.addAttribute("retailUser",user);
        return "retailUser";
    }

    @PostMapping("/update")
    public String UpdateUser(RetailUser retailUser, Model model) throws Exception{
       boolean result = retailUserService.updateUser(retailUser);
       if(result) {
           model.addAttribute("success", "Retail user updated successfully");
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
