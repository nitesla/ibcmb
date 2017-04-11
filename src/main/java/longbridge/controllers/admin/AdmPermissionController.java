package longbridge.controllers.admin;

import longbridge.dtos.PermissionDTO;
import longbridge.models.Permission;
import longbridge.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("admin/permissions")
public class AdmPermissionController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/new")
    public String addPermission(){
        return "add-permission";
    }

    @PostMapping
    public String createPermission(@ModelAttribute("permission") PermissionDTO permission, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add-permission";
        }

        roleService.addPermission(permission);
        model.addAttribute("success", "Permission added successfully");
        return "/admin/permissions";
    }

    @GetMapping("/{permissionId}")
    public PermissionDTO getPermission(@PathVariable Long permissionId, Model model){
        PermissionDTO permission = roleService.getPermission(permissionId);
        model.addAttribute("permission",permission);
        return permission;
    }

    @GetMapping
    public Iterable<PermissionDTO> getPermissions(Model model){
        Iterable<PermissionDTO> permissionList = roleService.getPermissions();
        model.addAttribute("permissionList",permissionList);
        return permissionList;
    }

    @PostMapping("/{permissionId}")
    public String updatePermission(@ModelAttribute("permissionForm") PermissionDTO permission, BindingResult result, @PathVariable Long permissionId,  Model model){

        if(result.hasErrors()){
            return "add-permission";
        }
        permission.setId(permissionId);
        roleService.addPermission(permission);
        model.addAttribute("success", "Permission updated successfully");
        return "/admin/permissions";
    }

    @PostMapping("/{permissionId}/delete")
    public String deletePermission(@PathVariable Long permissionId, Model model){
        PermissionDTO permission = roleService.getPermission(permissionId);
        roleService.deletePermission(permissionId);
        model.addAttribute("success", "Permission deleted successfully");
        return "redirect:/admin/permissions";
    }
}

