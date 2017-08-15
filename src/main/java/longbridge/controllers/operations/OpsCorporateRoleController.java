package longbridge.controllers.operations;

import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateRoleDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Fortune on 6/8/2017.
 */

@Controller
@RequestMapping("/ops/corporates")
public class OpsCorporateRoleController {

    @Autowired
    CorporateService corporateService;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    MessageSource messageSource;
    Logger logger = LoggerFactory.getLogger(this.getClass());

//
//    @ModelAttribute("users")
//    public Iterable<CorporateUserDTO> getUsers(NativeWebRequest request) {
//
//        HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
//        Map<String, String> map = (Map<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        Long corpId = null;
//        try {
//            corpId = NumberUtils.createLong(map.get("corpId"));
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//        Iterable<CorporateUserDTO> users =null;
//
//        if(corpId!=null){
//        }
////        else {
////            users = corporateUserService.getUsers(corpId);
////        }
//
//
//        return users;
//    }





    @GetMapping("/{corpId}/roles/new")
    public String addRole(@PathVariable Long corpId, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(corpId);
        CorporateRoleDTO roleDTO = new CorporateRoleDTO();
        List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(corpId);
        model.addAttribute("users",users);
        model.addAttribute("corporate",corporate);
        model.addAttribute("role", roleDTO);
        return "ops/corporate/addrole";
    }
    
    @PostMapping("/roles")
    public String createRole(@ModelAttribute("role") @Valid CorporateRoleDTO roleDTO, BindingResult result, WebRequest request, RedirectAttributes redirectAttributes, Locale locale,Model model) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
            List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
            model.addAttribute("users",users);
            model.addAttribute("corporate",corporate);

            return "ops/corporate/addrole";
        }
        Set<CorporateUserDTO> usersList = new HashSet<CorporateUserDTO>();

        String[] userIds = request.getParameterValues("usersList");
        if (userIds != null) {
            for (String id : userIds) {
                CorporateUserDTO userDTO = new CorporateUserDTO();
                userDTO.setId(NumberUtils.toLong(id));
                usersList.add(userDTO);
            }
        }

        roleDTO.setUsers(usersList);
        try {
            String message = corporateService.addCorporateRole(roleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates/" + roleDTO.getCorporateId() + "/view";
        }
        catch (DuplicateObjectException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
            List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
            model.addAttribute("users",users);
            model.addAttribute("corporate",corporate);
            logger.error("Error creating role", ibe);
            return "ops/corporate/addrole";
        }
        catch (InternetBankingException ibe) {
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
            model.addAttribute("corporate",corporate);
            List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
            model.addAttribute("users",users);
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error creating role", ibe);
            return "ops/corporate/addrole";
        }
    }


    @GetMapping("/roles/{roleId}/edit")
    public String editRole(@PathVariable Long roleId, Model model) {
        CorporateRoleDTO roleDTO = corporateService.getCorporateRole(roleId);
        List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
        CorporateDTO corporate =  corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
        model.addAttribute("users",users);
        model.addAttribute("role",roleDTO);
        model.addAttribute("usersInRole",roleDTO.getUsers());
        logger.info("Users in role: "+roleDTO.getUsers());
        model.addAttribute("corporate",corporate);
        return "ops/corporate/editrole";
    }

    @PostMapping("/roles/update")
    public String updateRole(@ModelAttribute("role") @Valid CorporateRoleDTO roleDTO, BindingResult result, WebRequest request, RedirectAttributes redirectAttributes, Locale locale, Model model) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
            List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
            CorporateRoleDTO role = corporateService.getCorporateRole(roleDTO.getId());
            model.addAttribute("usersInRole",role.getUsers());
            model.addAttribute("users",users);
            model.addAttribute("corporate",corporate);
            return "ops/corporate/editrole";
        }


        Set<CorporateUserDTO> usersList = new HashSet<CorporateUserDTO>();

        String[] userIds = request.getParameterValues("usersList");
        if (userIds != null) {
            for (String id : userIds) {
                CorporateUserDTO userDTO = new CorporateUserDTO();
                userDTO.setId(NumberUtils.toLong(id));
                usersList.add(userDTO);
            }
        }
        roleDTO.setUsers(usersList);

        try {
            String message = corporateService.updateCorporateRole(roleDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/corporates/" + roleDTO.getCorporateId() + "/view";
        }
        catch (DuplicateObjectException ibe) {
            result.addError(new ObjectError("invalid", ibe.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
            List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
            CorporateRoleDTO role = corporateService.getCorporateRole(roleDTO.getId());
            model.addAttribute("usersInRole",role.getUsers());
            model.addAttribute("users",users);
            model.addAttribute("corporate",corporate);
            return "ops/corporate/editrole";
        }
        catch (InternetBankingException ibe) {
            result.addError(new ObjectError("invalid", ibe.getMessage()));
            CorporateDTO corporate = corporateService.getCorporate(NumberUtils.toLong(roleDTO.getCorporateId()));
            List<CorporateUserDTO> users = corporateUserService.getUsersWithoutRole(NumberUtils.toLong(roleDTO.getCorporateId()));
            CorporateRoleDTO role = corporateService.getCorporateRole(roleDTO.getId());
            model.addAttribute("usersInRole",role.getUsers());
            model.addAttribute("users",users);
            model.addAttribute("corporate",corporate);
            return "ops/corporate/editrole";
        }
    }

    @GetMapping("/roles/{roleId}/delete")
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes redirectAttributes, Locale locale) {

       CorporateRoleDTO roleDTO = corporateService.getCorporateRole(roleId);
        try {
            String message = corporateService.deleteCorporateRole(roleId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error deleting role", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/ops/corporates/" + roleDTO.getCorporateId() + "/view";
    }
}
