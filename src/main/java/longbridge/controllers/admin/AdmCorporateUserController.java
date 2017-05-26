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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/corporates/users/")
public class AdmCorporateUserController {
    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MessageSource messageSource;


    @ModelAttribute
    public void init(Model model){
        Iterable<RoleDTO> roles = roleService.getRoles();
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()){
            RoleDTO roleDTO = roleDTOIterator.next();
            if(roleDTO.getName().equals("Sole Admin")){
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles",roles);

    }


    @GetMapping("{corpId}/new")
    public String addUser(@PathVariable Long corpId, Model model, RedirectAttributes redirectAttributes) {
        CorporateDTO corporateDTO = corporateService.getCorporate(corpId);
        if(corporateDTO.getCorporateType().equals("SOLE")){
            redirectAttributes.addFlashAttribute("failure","Corporate entity has sole user");
            return "redirect:/admin/corporates";
        }
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporate", corporateDTO);
        model.addAttribute("corporateUser", corporateUserDTO);
        return "adm/corporate/adduser";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, HttpSession session, Model model,RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            if(session.getAttribute("corporate")==null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            }
            else{
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);

            }
            return "adm/corporate/adduser";
        }
        try {

            if(session.getAttribute("corporate")!=null) {
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                String message = corporateService.addCorporate(corporate,corporateUserDTO);
                session.removeAttribute("corporate");
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/admin/corporates/";
            }
            else{
                String message = corporateUserService.addUser(corporateUserDTO);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/admin/corporates/";
            }
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user {}", corporateUserDTO.getUserName(), doe);
            if(session.getAttribute("corporate")==null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            }
            else{
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);

            }
            return "adm/corporate/adduser";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);

            if(session.getAttribute("corporate")==null) {
                CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUserDTO.getCorporateId()));
                model.addAttribute("corporate", corporate);
            }
            else{
                CorporateDTO corporate = (CorporateDTO) session.getAttribute("corporate");
                model.addAttribute("corporate", corporate);

            }            return "adm/corporate/adduser";
        }
    }

    @GetMapping("{userId}/edit")
    public String getUser(@PathVariable Long userId, Model model) {
        CorporateUserDTO user = corporateUserService.getUser(userId);
        model.addAttribute("corporateUser", user);
        return "adm/corporate/edituser";
    }

    @PostMapping("edit")
    public String UpdateUser(@ModelAttribute("corporateUser") CorporateUserDTO corporateUserDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "adm/corporate/edituser";
        }

        String message = corporateUserService.updateUser(corporateUserDTO);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/admin/corporates/"+corporateUserDTO.getCorporateId()+"/view";
    }

    @PostMapping("{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        corporateUserService.deleteUser(userId);
        return "redirect:/corporate/users";
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
