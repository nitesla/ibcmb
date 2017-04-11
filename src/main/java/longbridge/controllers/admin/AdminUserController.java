package longbridge.controllers.admin;

import longbridge.dtos.AdminUserDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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

    /**
     * Page for adding a new user
     * @return
     */
    @GetMapping("/new")

    public String addUser(AdminUserDTO adminUserDTO)
    {

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

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") AdminUserDTO adminUserDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws Exception{

        if(result.hasErrors()){
            return "add/admin/add";
        }

      //  return "redirect:/admin/users/";

        adminUserService.addUser(adminUserDTO);
        redirectAttributes.addFlashAttribute("success","Admin user created successfully");
        return "redirect:adm/admin/view";

    }


    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<AdminUserDTO> getUsers(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AdminUserDTO> adminUsers = adminUserService.getUsers(pageable);
        DataTablesOutput<AdminUserDTO> out = new DataTablesOutput<AdminUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(adminUsers.getContent());
        out.setRecordsFiltered(adminUsers.getTotalElements());
        out.setRecordsTotal(adminUsers.getTotalElements());

        return out;
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
       AdminUser user =adminUserService.getUser(userId);
       AdminUserDTO adminUserDTO = modelMapper.map(user,AdminUserDTO.class);
       model.addAttribute("user",adminUserDTO);
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

}
