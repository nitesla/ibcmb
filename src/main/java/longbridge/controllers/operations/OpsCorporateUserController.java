package longbridge.controllers.operations;

import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.PasswordException;
import longbridge.forms.ChangePassword;
import longbridge.security.FailedLoginService;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import longbridge.services.RoleService;
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/ops/corporates/users/")
public class OpsCorporateUserController {
    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;



    @ModelAttribute
    public void init(Model model) {
        Iterable<RoleDTO> roles = roleService.getRoles();
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()) {
            RoleDTO roleDTO = roleDTOIterator.next();
            if (roleDTO.getName().equals("Sole Admin")) {
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles", roles);
    }


    @GetMapping("{corpId}/new")
    public String addUser(@PathVariable Long corpId, Model model, RedirectAttributes redirectAttributes) {
        CorporateDTO corporateDTO = corporateService.getCorporate(corpId);
        if (corporateDTO.getCorporateType().equals("SOLE")) {
            redirectAttributes.addFlashAttribute("failure", "Corporate entity has sole user");
            return "redirect:/ops/corporates";
        }


        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "/ops/corporate/addUser";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, HttpSession session, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            if (session.getAttribute("corporate") == null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            } else {
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);
            }
            return "/ops/corporate/addUser";
        }
        try {

            if (session.getAttribute("corporate") != null) {
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                String message = corporateService.addCorporate(corporate, corporateUserDTO);
                session.removeAttribute("corporate");
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/ops/corporates/";
            } else {
                String message = corporateUserService.addUser(corporateUserDTO);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/ops/corporates/";
            }
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user ", doe);
            if (session.getAttribute("corporate") == null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            } else {
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);
            }
            return "/ops/corporate/addUser";

        } catch (InternetBankingSecurityException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating corporate user on Entrust ", se);
            if (session.getAttribute("corporate") == null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            } else {
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);
            }
            return "/ops/corporate/addUser";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);

            if (session.getAttribute("corporate") == null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            } else {
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);

            }
            return "/ops/corporate/addUser";
        }
    }


    @GetMapping("/{userId}/unlock")
    public String unlockUser(@PathVariable Long userId, RedirectAttributes redirectAttributes, Locale locale) {

        String corpId = corporateUserService.getUser(userId).getCorporateId();

        try {
            String message = corporateUserService.unlockUser(userId);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException e) {
            logger.error("Error unlocking user", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());

        }

        return "redirect:/ops/corporates/" + corpId + "/view";
    }

    @GetMapping("/{userId}/edit")
    public String getUser(@PathVariable Long userId, Model model) {
        CorporateUserDTO user = corporateUserService.getUser(userId);
        model.addAttribute("corporateUser", user);
        return "/ops/corporate/editUser";
    }

    @PostMapping("edit")
    public String UpdateUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/ops/corporate/editUser";
        }
      try {
          String message = corporateUserService.updateUser(corporateUserDTO);
          redirectAttributes.addFlashAttribute("message", message);
      }
      catch (InternetBankingException ibe){
            logger.error("Failed to update corporate user",ibe);
          redirectAttributes.addFlashAttribute("failure", ibe.getMessage());


      }

        return "redirect:/ops/corporates/" + corporateUserDTO.getCorporateId() + "/view";
    }

    @GetMapping("/{id}/activation")
    public String changeUserActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String corpId = corporateUserService.getUser(id).getCorporateId();

        try {
            String message = corporateUserService.changeActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing corporate activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/ops/corporates/" + corpId + "/view";
    }

    @GetMapping("{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        String corpId = corporateUserService.getUser(userId).getCorporateId();

        try {
            String message = corporateUserService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error deleting user", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/ops/corporates/" + corpId + "/view";

    }

    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String corpId = corporateUserService.getUser(id).getCorporateId();

        try {
            String message = corporateUserService.resetPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for operation user", pe);
        }
        catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            logger.error("Error resetting password for operation user", ibe);
        }
        return "redirect:/ops/corporates/" + corpId + "/view";
    }

    @GetMapping("changePassword")
    public String changePassword() {
        return "changePassword";
    }

    @PostMapping("changePassword")
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
