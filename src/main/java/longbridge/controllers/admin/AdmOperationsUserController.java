package longbridge.controllers.admin;

import longbridge.dtos.OperationsUserDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
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
    public String addUser(OperationsUser operationsUser){
        return "adm/operation/add";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("operationsUserForm") OperationsUser operationsUser, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "adm/operation/add";
        }
        operationsUserService.addUser(operationsUser);
        model.addAttribute("success","Retail user created successfully");
        return "redirect:/admin/operations/users";
    }

    @GetMapping
    public String getAllOperationsUsers(Model model){
        //Iterable<OperationsUser> operationsUserList= operationsUserService.getUsers();
        //model.addAttribute("operationsUserList",operationsUserList);
        return "adm/operation/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<OperationsUserDTO> getOpsUsers(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<OperationsUserDTO> opsUsers = operationsUserService.getUsers(pageable);
        DataTablesOutput<OperationsUserDTO> out = new DataTablesOutput<OperationsUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(opsUsers.getContent());
        out.setRecordsFiltered(opsUsers.getTotalElements());
        out.setRecordsTotal(opsUsers.getTotalElements());
        return out;
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

