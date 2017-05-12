package longbridge.controllers.admin;

import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.forms.ChangePassword;
import longbridge.models.CorporateUser;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
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

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/corporates/")
public class AdmCorporateUserController {
    @Autowired
    CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("{corpId}/users/new")
    public String addUser(@PathVariable Long corpId, Model model) {
        CorporateDTO corporateDTO = corporateService.getCorporate(corpId);
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "adm/corporate/addUser";
    }

    @PostMapping
    public String createUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            return "addUser";
        }
        CorporateUser corporateUser = modelMapper.map(corporateUserDTO, CorporateUser.class);
        corporateUserService.addUser(corporateUser);
        model.addAttribute("success", "Corporate user created successfully");
        return "redirect:/corporate/users";
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable Long userId, Model model) {
        CorporateUserDTO user = corporateUserService.getUser(userId);

        model.addAttribute("corporateUser", user);
        return "corporateUser";
    }


    @PostMapping("/{userId}")
    public String UpdateUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, @PathVariable Long userId, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "addUser";
        }
        corporateUserDTO.setId(userId);
        String message = corporateUserService.updateUser(corporateUserDTO);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/corporate/users";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        corporateUserService.deleteUser(userId);
        return "redirect:/corporate/users";
    }

    @GetMapping("/changePassword")
    public String changePassword() {
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@Valid ChangePassword changePassword, Long userId, BindingResult result, HttpRequest request, Model model) {
        if (result.hasErrors()) {
            return "changePassword";
        }
        CorporateUserDTO user = corporateUserService.getUser(userId);
        String oldPassword = changePassword.getOldPassword();
        String newPassword = changePassword.getNewPassword();
        String confirmPassword = changePassword.getConfirmPassword();

        //TODO validate password according to the defined password policy
        //The validations can be done on the ChangePassword class


        if (!newPassword.equals(confirmPassword)) {
            logger.info("PASSWORD MISMATCH");
        }

        user.setPassword(newPassword);
        //corporateUserService.addUser(user);
        logger.info("PASSWORD CHANGED SUCCESSFULLY");
        return "changePassword";
    }

}
