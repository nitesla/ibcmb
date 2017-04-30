package longbridge.controllers.admin;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.models.Permission;
import longbridge.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("/admin/permissions")
public class AdmPermissionController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/new")
    public String addPermission(Model model){
        model.addAttribute("permission", new PermissionDTO());
        return "adm/permission/add";
    }
//org.springframework.web.context.request.WebRequest
    @PostMapping
    public String createPermission(@ModelAttribute("permission") PermissionDTO permission, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/permission/add";
        }
        roleService.addPermission(permission);
        redirectAttributes.addFlashAttribute("success", "Permission added successfully");
        return "redirect:/admin/permissions";
    }
    
   

    @GetMapping("/{permissionId}")
    public PermissionDTO getPermission(@PathVariable Long permissionId, Model model){
        PermissionDTO permission = roleService.getPermission(permissionId);
        model.addAttribute("permission",permission);
        return permission;
    }

    @GetMapping("/{reqId}/edit")
    public String  editConfig(@PathVariable Long reqId, Model model){
        PermissionDTO permission = roleService.getPermission(reqId);
        model.addAttribute("permission",permission);
        return "/adm/permission/edit";
    }

    @GetMapping
    public String getPermissions(Model model){
        return "adm/permission/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<PermissionDTO> getPermissions(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<PermissionDTO> permissions = roleService.getPermissions(pageable);
        DataTablesOutput<PermissionDTO> out = new DataTablesOutput<PermissionDTO>();
        out.setDraw(input.getDraw());
        out.setData(permissions.getContent());
        out.setRecordsFiltered(permissions.getTotalElements());
        out.setRecordsTotal(permissions.getTotalElements());
        return out;
    }


    @PostMapping("/update")
    public String updatePermission(@ModelAttribute("permissionForm") PermissionDTO permission, BindingResult result, RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            return "/adm/permission/add";
        }
        roleService.addPermission(permission);
        redirectAttributes.addFlashAttribute("success", "Permission updated successfully");
        return "redirect:/admin/permissions";
    }

    @GetMapping("/{permissionId}/delete")
    public String deletePermission(@PathVariable Long permissionId, RedirectAttributes redirectAttributes){
        roleService.deletePermission(permissionId);
        redirectAttributes.addFlashAttribute("success", "Permission deleted successfully");
        return "redirect:/admin/permissions";
    }
}

