package longbridge.controllers.admin;

import longbridge.dtos.AdminUserDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Created by SYLVESTER on 31/03/2017.
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private  AdminUserService adminUserService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AdminUserRepo adminUserRepo;
    @Autowired
    private VerificationRepo verificationRepo;

    /**
     * Page for adding a new user
     * @return
     */
    @GetMapping("/new")
    public String addUser(Model model){
        return "adm/admin/add";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {

        AdminUser user = adminUserService.getUser(userId);
        model.addAttribute("user", user);
        return "adm/admin/edit";
    }

    /**
     * Creates a new user
     * @param adminUserDTO
     * @return
     * @throws Exception
     */
    @PostMapping
    public String createUser(@ModelAttribute("user") AdminUserDTO adminUserDTO, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "admin/add";
        }
        adminUserService.addUser(adminUserDTO);
        redirectAttributes.addFlashAttribute("success","Admin user created successfully");
        return "redirect:adm/admin/view";
    }


    @GetMapping(path="/all")
    public @ResponseBody Iterable<AdminUserDTO> getUsers(){
        Iterable<AdminUserDTO> adminUserList=adminUserService.getUsers();
        //model.addAttribute("adminUserList",adminUserList);
        logger.info("Admin users : {}",adminUserList);

        return adminUserList;
    }

    /**
     * Returns all users
     * @param model
     * @return
     */
    @GetMapping
    public String getUsers(Model model){
//        Iterable<AdminUser> adminUserList=adminUserService.getUsers();
//        model.addAttribute("adminUserList",adminUserList);
        return "adm/admin/view";
    }
    /**
     * Returns user
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/{userId}/details")
    public String getAdminUser(@PathVariable  Long userId, Model model){
       AdminUserDTO user =adminUserService.getAdminUser(userId);
       model.addAttribute("user",user);
       return "adm/admin/details";
    }

    /**
     * Updates the user
     * @param adminUserDTO
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @PostMapping("/{userId}")
    public String updateUser(@ModelAttribute("user") @Valid AdminUserDTO adminUserDTO, BindingResult result, @PathVariable Long userId, RedirectAttributes redirectAttributes) throws Exception{
      if(result.hasErrors()) {
          return "adm/admin/add";
      }
         adminUserDTO.setId(userId);
          boolean updated = adminUserService.updateUser(adminUserDTO);
          if (updated) {
              redirectAttributes.addFlashAttribute("success", "Admin user updated successfully");
          }
        return "redirect:/admin/users";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId){
        adminUserService.deleteUser(userId);
        return "redirect:admin/users";
    }

    @GetMapping("/password")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/password")
    public String changePassword(@Valid ChangePassword changePassword,Long userId, BindingResult result, HttpRequest request, Model model){
       if(result.hasErrors()){
            return "password";
        }
        AdminUser user=adminUserService.getUser(userId);
        String oldPassword=changePassword.getOldPassword();
        String newPassword=changePassword.getNewPassword();
        String confirmPassword=changePassword.getConfirmPassword();

        //TODO validate password according to the defined password policy
        //The validations can be done on the ChangePassword class

        if(!newPassword.equals(confirmPassword)){
            logger.info("PASSWORD MISMATCH");
        }
        adminUserService.setPassword(user,newPassword);
        logger.trace("Password for user {} changed successfully",user.getUserName());
        return "changePassword";
    }

    @PostMapping("/{id}/verify")
    public String verify(@PathVariable Long id){
        logger.info("id {}", id);

        //todo check verifier role
        AdminUser adminUser = adminUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);

        if (verification == null || Verification.VerificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

        try {
            adminUserService.verify(verification, adminUser);
        } catch (IOException e) {
            logger.error("Error occurred", e);
        }
        return "role/add";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable Long id){

        //todo check verifier role
        AdminUser adminUser = adminUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);

        if (verification == null || Verification.VerificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

        adminUserService.decline(verification, adminUser, "todo get the  reason from the frontend");
        return "role/add";
    }


}
