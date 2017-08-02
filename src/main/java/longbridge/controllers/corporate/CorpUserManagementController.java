package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 26/05/2017.
 */

@Controller
@RequestMapping("/corporate/users")
public class CorpUserManagementController {
    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private CorporateService corporateService;

    @Autowired
    private CorpUserVerificationService corpUserVerificationService;

    @Autowired
    CodeService codeService;
    @Autowired
    private RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(Model model, Principal principal){
        List<CodeDTO> corporateTypes = codeService.getCodesByType("CORPORATE_TYPE");
        model.addAttribute("corporateTypes", corporateTypes);

        Iterable<RoleDTO> roles = roleService.getRolesByUserType(UserType.CORPORATE);
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()) {
            RoleDTO roleDTO = roleDTOIterator.next();
            if (roleDTO.getName().equals("SOLE")) {
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles", roles);
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
        model.addAttribute("corporate", corporate);
    }

//    @ModelAttribute
//    public void setModelAttribute(Principal principal, Model model){
//        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
//        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
//        model.addAttribute("corporate", corporate);
//    }

    @GetMapping
    public String viewUsers(Principal principal, Model model){
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
        model.addAttribute("corporate", corporate);
        return "corp/user/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<CorporateUserDTO> getUsers(Principal principal, DataTablesInput input) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateUserDTO> users = corporateUserService.getUsers(corporate.getId(), pageable);
        DataTablesOutput<CorporateUserDTO> out = new DataTablesOutput<CorporateUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(users.getContent());
        out.setRecordsFiltered(users.getTotalElements());
        out.setRecordsTotal(users.getTotalElements());
        return out;
    }

    @GetMapping("/add")
    public String addUser(Principal principal, Model model){
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(corporateUser.getCorporate().getId());
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporateUser", corporateUserDTO);
        model.addAttribute("corporate", corporate);
        return "corp/user/add";
    }

    @PostMapping
    public String createUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, WebRequest webRequest, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        if (result.hasErrors()) {
            return "corp/user/add";
        }

        try {
            String message = corporateUserService.addUserFromCorporateAdmin(corporateUserDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corporate/users/";
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user {}", corporateUserDTO.getUserName(), doe);
            model.addAttribute("failure", messageSource.getMessage("corp.user.creation.duplicate",null,locale));
            return "corp/user/add";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);
            model.addAttribute("failure", messageSource.getMessage("failure",null,locale));
            return "corp/user/add";
        }
    }

    @GetMapping("/{id}/status")
    public String activationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes){

        try {
            String message = corporateUserService.changeStatusFromCorporateAdmin(id);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException ibe){
            logger.error("Error changing corporate user activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }

        return "redirect:/corporate/users";
    }


    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            String message = corporateUserService.resetCorpPassword(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error resetting password for corporate user", pe);
        }
        catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            logger.error("Error resetting password for corporate user", ibe);
        }
        return "redirect:/corporate/users";
    }

    @GetMapping("/approvals")
    public String approvals(Principal principal, Model model){
        return "/corp/user/approval";
    }

    @GetMapping(path = "/approvals/all")
    public
    @ResponseBody
    DataTablesOutput<CorpUserVerificationDTO> getAllVerification(DataTablesInput input)
    {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpUserVerificationDTO> page = corpUserVerificationService.getAllRequests(pageable);
        DataTablesOutput<CorpUserVerificationDTO> out = new DataTablesOutput<CorpUserVerificationDTO>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/approvals")
    public String getObjectsForVerification(@PathVariable Long id, Model model, Principal principal)
    {
        CorpUserVerificationDTO verification = corpUserVerificationService.getVerification(id);
        model.addAttribute("verification",new CorpUserVerificationDTO());
        model.addAttribute("verify", verification);


        return "corp/user/details";
    }

}
