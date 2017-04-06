package longbridge.controllers.operations;

import longbridge.formValidations.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
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
 * Created by Wunmi on 31/03/2017.
 */

@RestController
@RequestMapping("/operations/user")
public class OperationsUserController {
    @Autowired
    OperationsUserService operationsUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/add")
    public String addUser(){
        return "addUser";
    }

    @PostMapping("/add")
    public String createUser(OperationsUser operationsUser, Model model) throws Exception{
        operationsUserService.addUser(operationsUser);
        model.addAttribute("success","Retail user created successfully");
        return "addUser";
    }

    @GetMapping("/all")
    public Iterable<OperationsUser> getAllOperationsUsers(Model model){
        Iterable<OperationsUser> operationsUserList= operationsUserService.getUsers();
        model.addAttribute("operationsUserList",operationsUserList);
        return operationsUserList;
    }

    @GetMapping("/user")
    public String getUser(Long userId, Model model){
        OperationsUser user = operationsUserService.getUser(userId);
        model.addAttribute("operationsUser",user);
        return "operationsUser";
    }

    @PostMapping("/update")
    public String UpdateUser(OperationsUser operationsUser, Model model) throws Exception{
        boolean result = operationsUserService.updateUser(operationsUser);
        if(result) {
            model.addAttribute("success", "Operations user updated successfully");
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
        OperationsUser user= operationsUserService.getUser(userId);
        String oldPassword=changePassword.getOldPassword();
        String newPassword=changePassword.getNewPassword();
        String confirmPassword=changePassword.getConfirmPassword();

        //TODO validate password according to the defined password policy
        //The validations can be done on the ChangePassword class


        if(!newPassword.equals(confirmPassword)){
            logger.info("PASSWORD MISMATCH");
        }

        user.setPassword(newPassword);
        operationsUserService.addUser(user);
        logger.info("PASSWORD CHANGED SUCCESSFULLY");
        return "changePassword";
    }

}

