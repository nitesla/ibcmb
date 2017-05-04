package longbridge.controllers.admin;

import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.OperationsUser;
import longbridge.models.Verification;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordService;
import longbridge.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
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
 * Created by SYLVESTER on 31/03/2017.
 */
@Controller
@RequestMapping("admin/operations/users")
public class AdmOperationsUserController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private  OperationsUserService operationsUserService;
    @Autowired
    private OperationsUserRepo operationsUserRepo;
    @Autowired
    private VerificationRepo verificationRepo;
    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    PasswordService passwordService;




    /**
     * Page for adding a new user
     * @return
     */
    @GetMapping("/new")
    public String addUser(Model model){
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("operationsUser", new OperationsUserDTO());
        model.addAttribute("roles",roles);
        return "adm/operation/add";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        OperationsUserDTO user = operationsUserService.getUser(userId);
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("operationsUser", user);
        model.addAttribute("roles",roles);
        return "adm/operation/edit";
    }

    /**
     * Creates a new user
     * @param operationsUser
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping
    public String createUser(@ModelAttribute("operationsUser") @Valid OperationsUserDTO operationsUser, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()){

            result.addError(new ObjectError("invalid","Please fill in the required fields"));
            return "adm/operation/add";
        }
        if(!operationsUserService.isValidUsername(operationsUser.getUserName())){
            result.addError(new ObjectError("invalid","Username already exists"));
            return "adm/operation/add";
        }

        operationsUserService.addUser(operationsUser);

        redirectAttributes.addFlashAttribute("message","Operation user created successfully");
        return "redirect:/admin/operations/users";
    }


    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<OperationsUserDTO> getUsers(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<OperationsUserDTO> operationsUsers = operationsUserService.getUsers(pageable);
        DataTablesOutput<OperationsUserDTO> out = new DataTablesOutput<OperationsUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(operationsUsers.getContent());
        out.setRecordsFiltered(operationsUsers.getTotalElements());
        out.setRecordsTotal(operationsUsers.getTotalElements());

        return out;
    }
    /**
     * Returns all users
     * @param model
     * @return
     */
    @GetMapping
    public String getUsers(Model model){
//        Iterable<OperationsUser> operationsUserList=operationsUserService.getUsers();
//        model.addAttribute("operationsUserList",operationsUserList);
        return "adm/operation/view";
    }
    /**
     * Returns user
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/{userId}/details")
    public String getOperationsUser(@PathVariable  Long userId, Model model){
        OperationsUserDTO operationsUser =operationsUserService.getUser(userId);
        model.addAttribute("user",operationsUser);
        return "operation/details";
    }

    /**
     * Updates the user
     * @param operationsUser
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") OperationsUserDTO operationsUser, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()) {
            return "adm/operation/add";
        }
        boolean updated = operationsUserService.updateUser(operationsUser);
        if (updated) {
            redirectAttributes.addFlashAttribute("message", "Operations user updated successfully");
        }
        return "redirect:/admin/operations/users";
    }

    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes){
        if(operationsUserService.resetPassword(id)) {
            redirectAttributes.addFlashAttribute("message", "Password reset successfully");
        }
        return "redirect:/admin/operations/users";
    }

    @GetMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId){
        operationsUserService.deleteUser(userId);
        return "redirect:/admin/operations/users";
    }

    @ModelAttribute
    public void init(Model model){
        model.addAttribute("passwordRules",passwordService.getPasswordRules());
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("roles",roles);

    }

    @GetMapping("/password")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/password")
    public String changePassword(@Valid ChangePassword changePassword, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid", "Please provide valid password"));
            return "/ops/pword";
        }
        OperationsUserDTO user = operationsUserService.getUserByName(principal.getName());

        if(!this.passwordEncoder.matches(changePassword.getOldPassword(),user.getPassword())){
            logger.trace("Invalid old password provided for change");
            result.addError(new ObjectError("invalid", "Incorrect Old Password"));
            return "/ops/pword";
        }

        String errorMsg = passwordService.validate(changePassword.getNewPassword());
        if(!errorMsg.equals("")){
            result.addError(new ObjectError("invalid", errorMsg));
            return "/ops/pword";
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())){
            logger.trace("PASSWORD MISMATCH");
            result.addError(new ObjectError("invalid", "Passwords do not match"));
            return "/ops/pword";
        }


        if(!this.passwordEncoder.matches(changePassword.getOldPassword(),user.getPassword())){
            logger.trace("Invalid old password provided for change");
            result.addError(new ObjectError("invalid", "Incorrect Old Password"));
            return "/ops/pword";
        }

        operationsUserService.changePassword(user, changePassword.getOldPassword(), changePassword.getNewPassword());

        redirectAttributes.addFlashAttribute("message","Password changed successfully");
        return "redirect:/ops/logout";

    }

    @PostMapping("/{id}/verify")
    public String verify(@PathVariable Long id){
        logger.info("id {}", id);

        //todo check verifier role
        OperationsUser operationsUser = operationsUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);

        if (verification == null || Verification.VerificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

//        try {
//            operationsUserService.verify(verification, operationsUser);
//        } catch (IOException e) {
//            logger.error("Error occurred", e);
//        }
        return "role/add";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable Long id){

        //todo check verifier role
        OperationsUser operationsUser = operationsUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);

        if (verification == null || Verification.VerificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

       // operationsUserService.decline(verification, operationsUser, "todo get the  reason from the frontend");
        return "role/add";
    }


}
