package longbridge.controllers.admin;

import longbridge.dtos.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Wunmi on 31/03/2017.
 */

@Controller
@RequestMapping("/admin/operations/users")
public class AdmOperationsUserController {
    @Autowired
    OperationsUserService operationsUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/new")
    public String addUser(){
        return "addUser";
    }

    @PostMapping
    public String createUser(@ModelAttribute("operationsUserForm") OperationsUser operationsUser, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "addUser";
        }
        operationsUserService.addUser(operationsUser);
        model.addAttribute("success","Retail user created successfully");
        return "redirect:/operations/users";
    }

    @GetMapping
    public String getAllOperationsUsers(Model model){
        //Iterable<OperationsUser> operationsUserList= operationsUserService.getUsers();
        //model.addAttribute("operationsUserList",operationsUserList);
        return "adm/operation/view";
    }

    @GetMapping(path="/all", produces = "application/json")
    public @ResponseBody Iterable<OperationsUser> listAllUsers() {
        Iterable<OperationsUser> operationsUserList= operationsUserService.getUsers();

//        /**
//         * Construct the JSON to return
//         */
//        JsonNodeFactory factory = new JsonNodeFactory(false);
//        com.fasterxml.jackson.databind.node.ObjectNode result = factory.objectNode();
//
//        result.put("recordsTotal", 100);
//        result.put("recordsFiltered", 100);
//
//        ArrayNode an = result.putArray("data");
//
//        for (OperationsUser t : operationsUserList) {
//            ObjectNode row = factory.objectNode();
//            row.put("0", t.getId());
//            row.put("1", t.getFirstName());
//            row.put("2", t.getLastName());
//            row.put("3", (t.getRole()==null)? "No role": t.getRole().getName());
//            an.add(row);
//        }
//        return result.toString();
        return operationsUserList;
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable Long userId, Model model){
        OperationsUser user = operationsUserService.getUser(userId);
        model.addAttribute("operationsUser",user);
        return "operationsUserDetails";
    }

    @PostMapping("/{userId}")
    public String UpdateUser(@ModelAttribute("operationsUserForm") OperationsUser user, @PathVariable Long userId, BindingResult result,Model model) throws Exception{
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
        return "redirect:/operations/users";
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

