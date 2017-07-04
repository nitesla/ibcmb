package longbridge.controllers.corporate;

import longbridge.dtos.CorpCorporateUserDTO;
import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorporateUser;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import longbridge.services.RoleService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Iterator;
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
    private RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(Model model, Principal principal){
        Iterable<RoleDTO> roles = roleService.getRoles();
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()){
            RoleDTO roleDTO = roleDTOIterator.next();
            if(roleDTO.getName().equals("Sole Admin")){
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles",roles);
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
        CorpCorporateUserDTO corporateUserDTO = new CorpCorporateUserDTO();
        model.addAttribute("corporateUser", corporateUserDTO);
        model.addAttribute("corporate", corporate);
        Iterable<RoleDTO> roles = roleService.getRoles();
        Iterator<RoleDTO> roleDTOIterator = roles.iterator();
        while (roleDTOIterator.hasNext()){
            RoleDTO roleDTO = roleDTOIterator.next();
            if(roleDTO.getName().equals("Sole Admin")){
                roleDTOIterator.remove();
            }
        }
        model.addAttribute("roles",roles);
        return "corp/user/add";
    }

    @PostMapping
    public String createUser(@ModelAttribute("corporateUser") @Valid CorpCorporateUserDTO corporateUserDTO, BindingResult result, HttpSession session, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

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
            return "corp/user/add";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating corporate user", ibe);

            return "corp/user/add";
        }
    }

    @GetMapping("/{id}/status")
    public String activationStatus(@PathVariable Long id, RedirectAttributes redirectAttributes){

        try {
            String message = corporateUserService.changeCorpActivationStatus(id);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException ibe){
            logger.error("Error creating corporate user", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }

        return "redirect:/corporate/users";
    }

}
