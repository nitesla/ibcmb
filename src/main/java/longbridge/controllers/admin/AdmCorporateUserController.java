package longbridge.controllers.admin;

import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.forms.ChangePassword;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import longbridge.services.RoleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/corporates")
public class AdmCorporateUserController {
    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;


    @GetMapping("{corpId}/users/new")
    public String addUser(@PathVariable Long corpId, Model model) {
        CorporateDTO corporateDTO = corporateService.getCorporate(corpId);
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "adm/corporate/adduser";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            CorporateDTO corporateDTO = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
            CorporateUserDTO corporateUser = new CorporateUserDTO();
            Iterable<RoleDTO> roles = roleService.getRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("corporate", corporateDTO);
            model.addAttribute("corporateUser", corporateUser);
            return "adm/corporate/adduser";
        }
        try {
            String message = corporateUserService.addUser(corporateUserDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/corporates/";
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating admin user {}", corporateUserDTO.getUserName(), doe);
            CorporateDTO corporateDTO = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
            CorporateUserDTO corporateUser = new CorporateUserDTO();
            Iterable<RoleDTO> roles = roleService.getRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("corporate", corporateDTO);
            model.addAttribute("corporateUser", corporateUser);
            return "adm/corporate/adduser";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating admin user", ibe);
            CorporateDTO corporateDTO = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
            CorporateUserDTO corporateUser = new CorporateUserDTO();
            Iterable<RoleDTO> roles = roleService.getRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("corporate", corporateDTO);
            model.addAttribute("corporateUser", corporateUser);
            return "adm/corporate/adduser";
        }
    }

    @GetMapping("/users/{userId}")
    public String getUser(@PathVariable Long userId, Model model) {
        CorporateUserDTO user = corporateUserService.getUser(userId);

        model.addAttribute("corporateUser", user);
        return "corporateUser";
    }


    @PostMapping("/users/{userId}")
    public String UpdateUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, @PathVariable Long userId, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "addUser";
        }
        corporateUserDTO.setId(userId);
        String message = corporateUserService.updateUser(corporateUserDTO);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/corporate/users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        corporateUserService.deleteUser(userId);
        return "redirect:/corporate/users";
    }

    @GetMapping("/users/changePassword")
    public String changePassword() {
        return "changePassword";
    }

    @PostMapping("/users/changePassword")
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
