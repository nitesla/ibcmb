package longbridge.controllers.admin;

import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.*;
import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.RoleService;
import longbridge.services.VerificationService;
import longbridge.utils.DataTablesUtils;
import longbridge.validator.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;

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
    private RoleService roleService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private EmailValidator emailValidator;




    @ModelAttribute
    public void init(Model model){
        model.addAttribute("passwordRules", passwordPolicyService.getPasswordRules());
        Iterable<RoleDTO> roles = roleService.getRoles();
        model.addAttribute("roles",roles);

    }

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
    public String createUser(@ModelAttribute("operationsUser") @Valid OperationsUserDTO operationsUser, BindingResult result, RedirectAttributes redirectAttributes, Locale locale){
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/operation/add";
        }

        if(!emailValidator.validate(operationsUser.getEmail())){
            result.addError(new ObjectError("invalid", messageSource.getMessage("email.invalid",null,locale)));
            logger.error("Invalid User email {}", operationsUser.getEmail());
            return "adm/operation/add";
        }


        try {
            String message = operationsUserService.addUser(operationsUser);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/operations/users";
        }
        catch (EntrustException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating Operations user on Entrust", se);
            return "adm/operation/add";
        }
        catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating operation user {}", operationsUser.getUserName(), doe);
            return "adm/operation/add";
        }
        catch (InternetBankingSecurityException se) {
            result.addError(new ObjectError("error", se.getMessage()));
            logger.error("Error creating operation user", se);
            return "adm/operation/add";
        }
        catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating operation user", ibe);
            return "adm/operation/add";
        }
    }


    @GetMapping("/{id}/activation")
    public String changeActivationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            String message = operationsUserService.changeActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error changing user activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/operations/users";

    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<OperationsUserDTO> getUsers(DataTablesInput input, @RequestParam("csearch") String search){

        Pageable pageable = DataTablesUtils.getPageable(input);
        
        Page<OperationsUserDTO> operationsUsers = null;
        if (StringUtils.isNoneBlank(search)) {
        	operationsUsers = operationsUserService.findUsers(search,pageable);
		}else{
			operationsUsers = operationsUserService.getUsers(pageable);
		}
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
        model.addAttribute("operationsUser",operationsUser);
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
    public String updateUser(@ModelAttribute("operationsUser") @Valid OperationsUserDTO operationsUser, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) throws Exception{
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/operation/edit";
        }


        if(!emailValidator.validate(operationsUser.getEmail())){
            result.addError(new ObjectError("invalid", messageSource.getMessage("email.invalid",null,locale)));
            logger.error("Invalid User email {}", operationsUser.getEmail());
            return "adm/operation/edit";
        }

        try {
            String message = operationsUserService.updateUser(operationsUser);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/operations/users";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error updating operation user", ibe);
            return "adm/operation/edit";
        }
    }

    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes){

        if(verificationService.isPendingVerification(id,OperationsUser.class.getSimpleName())){
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/admin/operations/users";

        }

        try {
            String message = operationsUserService.resetPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for operation user", pe);
        }
        return "redirect:/admin/operations/users";
    }

    @GetMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes){

        if(verificationService.isPendingVerification(userId,OperationsUser.class.getSimpleName())){
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/admin/users";

        }
        try {
            String message = operationsUserService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            logger.error("Error updating operations user", ibe);
        }
        return "redirect:/admin/operations/users";
    }



}
