package longbridge.controllers.admin;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.RoleService;

import org.apache.commons.lang3.StringUtils;
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

import java.util.Locale;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("/admin/permissions")
public class AdmPermissionController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MessageSource messageSource;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/new")
    public String addPermission(Model model) {
        model.addAttribute("permission", new PermissionDTO());
        return "adm/permission/add";
    }

    //org.springframework.web.context.request.WebRequest
    @PostMapping
    public String createPermission(@ModelAttribute("permission") PermissionDTO permission, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/permission/add";
        }
        try {
            String message = roleService.addPermission(permission);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/permissions";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", messageSource.getMessage(ibe.getMessage(),null, locale)));
            logger.error("Error creating permission", ibe);
            return "adm/permission/add";
        }
    }

    @GetMapping("/{permissionId}")
    public PermissionDTO getPermission(@PathVariable Long permissionId, Model model) {
        PermissionDTO permission = roleService.getPermission(permissionId);
        model.addAttribute("permission", permission);
        return permission;
    }

    @GetMapping
    public String getPermissions(Model model) {
        return "adm/permission/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<PermissionDTO> getPermissions(DataTablesInput input,@RequestParam("csearch") String search) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<PermissionDTO> permissions = null;
        if (StringUtils.isNoneBlank(search)) {
        	permissions = roleService.findPermissions(search,pageable);
		}else{
			permissions = roleService.getPermissions(pageable);
		}
        DataTablesOutput<PermissionDTO> out = new DataTablesOutput<PermissionDTO>();
        out.setDraw(input.getDraw());
        out.setData(permissions.getContent());
        out.setRecordsFiltered(permissions.getTotalElements());
        out.setRecordsTotal(permissions.getTotalElements());
        return out;
    }

    @GetMapping("/{reqId}/edit")
    public String editPermission(@PathVariable Long reqId, Model model) {
        PermissionDTO permission = roleService.getPermission(reqId);
        model.addAttribute("permission", permission);
        return "/adm/permission/edit";
    }

    @PostMapping("/update")
    public String updatePermission(@ModelAttribute("permission") PermissionDTO permission, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("permission.update.failure", null, locale)));
            return "/adm/permission/edit";
        }
        try {
            String message = roleService.updatePermission(permission);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/permissions";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            logger.error("Error updating permission", ibe);
            return "adm/permission/edit";
        }
    }

    @GetMapping("/{permissionId}/delete")
    public String deletePermission(@PathVariable Long permissionId, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = roleService.deletePermission(permissionId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            logger.error("Error deleting permission", ibe);
        }
        return "redirect:/admin/permissions";
    }
}

