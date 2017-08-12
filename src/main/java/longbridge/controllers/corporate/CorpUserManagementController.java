package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.services.*;
import longbridge.utils.VerificationStatus;
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
    private MakerCheckerService makerCheckerService;

    @Autowired
    CodeService codeService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private VerificationService verificationService;

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
        List<CorporateRoleDTO> corporateRoleDTO = corporateService.getRoles(corporateUser.getCorporate().getId());
        logger.info("CORP RULES >>>> " + corporateRoleDTO);
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        model.addAttribute("corporateUser", corporateUserDTO);
        model.addAttribute("corporate", corporate);

        model.addAttribute("corporateRoles", corporateRoleDTO);
        return "corp/user/add";
    }

    @PostMapping
    public String createUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, WebRequest webRequest, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        if (result.hasErrors()) {
            return "corp/user/add";
        }

        CorporateUser corpUser = corporateUserService.getUserByName(corporateUserDTO.getUserName());
        if (corpUser != null) {
            model.addAttribute("failure", messageSource.getMessage("user.exists", null, locale));
            return "corp/user/add";
        }
        Corporate corporate = corporateService.getCorp(Long.parseLong(corporateUserDTO.getCorporateId()));
        CorporateUser corporateUser = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, corporateUserDTO.getEmail());
        if (corporateUser != null) {
            model.addAttribute("failure", messageSource.getMessage("email.exists", null, locale));
            return "corp/user/add";
        }

        try {

            if (corporateUserDTO.isAuthorizer()){
                CorporateRoleDTO corporateRole = corporateService.getCorporateRole(corporateUserDTO.getCorporateRoleId());
                corporateUserDTO.setCorporateRole(corporateRole.getName() + " " + corporateRole.getRank());

                if (makerCheckerService.isEnabled("ADD_AUTHORIZER_FROM_CORPORATE_ADMIN")){
                    corpUserVerificationService.saveAuthorizer(corporateUserDTO, "ADD_AUTHORIZER_FROM_CORPORATE_ADMIN", "Add an authorizer by corporate Admin");
                }else {
                    corporateUserService.addAuthorizer(corporateUserDTO);
                }

            }else{
                if (makerCheckerService.isEnabled("ADD_INITIATOR_FROM_CORPORATE_ADMIN")){
                    corpUserVerificationService.saveInitiator(corporateUserDTO, "ADD_INITIATOR_FROM_CORPORATE_ADMIN", "Add an initiator by corporate Admin");
                }else {
                    corporateUserService.addInitiator(corporateUserDTO);
                }
            }

            return "redirect:/corporate/users/";
        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user {}", corporateUserDTO.getUserName(), doe);
            model.addAttribute("failure", doe.getMessage());
            return "corp/user/add";
        } catch (VerificationInterruptedException ib){
            redirectAttributes.addFlashAttribute("message", ib.getMessage());
            return "redirect:/corporate/users/";
        }catch (VerificationException e){
            result.addError(new ObjectError("error", e.getMessage()));
            logger.error("Error creating corporate user", e);
            model.addAttribute("failure", messageSource.getMessage("user.add.failure", null, locale));
            return "corp/user/add";
        }catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);
            model.addAttribute("failure", messageSource.getMessage("failure",null,locale));
            return "corp/user/add";
        }
    }

    @GetMapping("{id}/edit")
    public String editUser(@PathVariable Long id, Model model){
        CorporateUserDTO corporateUser = corporateUserService.getUser(id);
        CorporateDTO corporate = corporateService.getCorporate(Long.parseLong(corporateUser.getCorporateId()));
        List<CorporateRoleDTO> corporateRoleDTO = corporateService.getRoles(Long.parseLong(corporateUser.getCorporateId()));
        logger.info("CORP RULES >>>> " + corporateRoleDTO);
        model.addAttribute("corporateUser", corporateUser);
        model.addAttribute("corporate", corporate);

        model.addAttribute("corporateRoles", corporateRoleDTO);
        return "corp/user/add";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("corporateUser") @Valid CorporateUserDTO corporateUserDTO, BindingResult result, WebRequest webRequest, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        if (result.hasErrors()) {
            return "corp/user/add";
        }

        try {
            CorporateUserDTO corporateUser = corporateUserService.getUser(corporateUserDTO.getId());
            if (!corporateUserDTO.getEmail().equals(corporateUser.getEmail())) {
                Corporate corporate = corporateService.getCorp(Long.parseLong(corporateUserDTO.getCorporateId()));
                CorporateUser cp = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, corporateUserDTO.getEmail());
                if (cp != null) {
                    model.addAttribute("failure", messageSource.getMessage("email.exists", null, locale));
                    return "corp/user/add";
                }
            }


            if (corporateUserDTO.isAuthorizer() != corporateUser.isAuthorizer()){
                if (makerCheckerService.isEnabled("UPDATE_USER_FROM_CORPORATE_ADMIN")){
                    corpUserVerificationService.saveAuthorizer(corporateUserDTO, "UPDATE_USER_FROM_CORPORATE_ADMIN", "Edit an authorizer by corporate Admin");
                }else {
                    corporateUserService.updateUserFromCorpAdmin(corporateUserDTO);
                }

            }else{
                if (makerCheckerService.isEnabled("UPDATE_USER_FROM_CORPORATE_ADMIN")){
                    corpUserVerificationService.saveInitiator(corporateUserDTO, "UPDATE_USER_FROM_CORPORATE_ADMIN", "Edit an initiator by corporate Admin");
                }else {
                    corporateUserService.updateUserFromCorpAdmin(corporateUserDTO);
                }
            }

            return "redirect:/corporate/users/";

        } catch (DuplicateObjectException doe) {
            result.addError(new ObjectError("error", doe.getMessage()));
            logger.error("Error creating corporate user {}", corporateUserDTO.getUserName(), doe);
            model.addAttribute("failure", doe.getMessage());
            return "corp/user/add";
        } catch (VerificationInterruptedException ib){
            model.addAttribute("message", ib.getMessage());
            return "redirect:/corporate/users/";
        }catch (VerificationException e){
            result.addError(new ObjectError("error", e.getMessage()));
            logger.error("Error creating corporate user", e);
            model.addAttribute("failure", messageSource.getMessage("user.add.failure", null, locale));
            return "corp/user/add";
        }catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);
            model.addAttribute("failure", messageSource.getMessage("failure",null,locale));
            return "corp/user/add";
        }
    }

    @GetMapping("/{id}/status")
    public String activationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes){

        try {
            String message = "";
            if (makerCheckerService.isEnabled("UPDATE_CORP_USER_STATUS")){
                message = corpUserVerificationService.changeStatusFromCorporateAdmin(id);
            }else {
                message = corporateUserService.changeActivationStatusFromCorpAdmin(id);
            }
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException ibe){
            logger.error("Error changing corporate user activation status", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/corporate/users";
    }


    @GetMapping("/{id}/password/reset")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {


        if(verificationService.isPendingVerification(id, CorporateUser.class.getSimpleName())){
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/corporate/users";

        }

        if(corpUserVerificationService.isPendingVerification(id, CorporateUser.class.getSimpleName())){
            redirectAttributes.addFlashAttribute("failure", "User has pending changes to be verified");
            return "redirect:/corporate/users";

        }


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

    @GetMapping("/{id}/unblock")
    public String unblock(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            String message = corporateUserService.unlockUser(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (PasswordException pe) {
            redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            logger.error("Error unblocking corporate user", pe);
        }
        catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            logger.error("Error unblocking corporate user", ibe);
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
    DataTablesOutput<CorpUserVerificationDTO> getAllVerification(Principal principal, DataTablesInput input)
    {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpUserVerificationDTO> page = corpUserVerificationService.getRequestsByCorpId(corporateUser.getCorporate().getId(), pageable);
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

        if (VerificationStatus.PENDING.equals(verification.getStatus())) {
            boolean status = true;
            model.addAttribute("status", status);
        }

        return "corp/user/details";
    }

    @PostMapping("/verify")
    public String verify(@ModelAttribute("verification") @Valid CorpUserVerificationDTO corpUserVerification, BindingResult result, WebRequest request, Model model, RedirectAttributes redirectAttributes,  Locale locale) {

        String approval = request.getParameter("approve");

        try {
            if ("true".equals(approval))
            {
                corpUserVerificationService.verify(corpUserVerification);
                redirectAttributes.addFlashAttribute("message", "Operation approved successfully");

            } else if ("false".equals(approval))
            {
                if (result.hasErrors())
                {
                    CorpUserVerificationDTO corpUserVerification2=corpUserVerificationService.getVerification(corpUserVerification.getId());
                    model.addAttribute("verify", corpUserVerification2);
                    if (VerificationStatus.PENDING.equals(corpUserVerification2.getStatus())) {
                        boolean status = true;
                        model.addAttribute("status", status);
                    }
                    return "corp/user/details";
                }
                corpUserVerificationService.decline(corpUserVerification);
                redirectAttributes.addFlashAttribute("message", "Operation declined successfully");

            }
        }
        catch (VerificationException ve){
            logger.error("Error verifying the operation",ve);
            redirectAttributes.addFlashAttribute("failure", ve.getMessage());
        }
        catch (InternetBankingException ibe){
            logger.error("Error verifying the operation",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/corporate/users/approvals";
    }

}
