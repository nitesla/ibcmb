package longbridge.controllers.admin;

import longbridge.dtos.PermissionDTO;
import longbridge.models.Permission;
import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fortune on 4/5/2017.
 */
@RestController
@RequestMapping("admin/permissions")
public class AdmPermissionController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/new")
    public String addPermission(){
        return "add-permission";
    }

    @PostMapping
    public String createPermission(@ModelAttribute("permission") Permission permission, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add-permission";
        }

        securityService.addPermission(permission);
        model.addAttribute("success", "Permission added successfully");
        return "/admin/permissions";
    }

    @GetMapping("/{permissionId}")
    public Permission getPermission(@PathVariable Long permissionId, Model model){
        Permission permission = securityService.getPermission(permissionId);
        model.addAttribute("permission",permission);
        return permission;
    }

    @GetMapping
    public Iterable<Permission> getPermissions(Model model){
        Iterable<Permission> permissionList = securityService.getPermissions();
        model.addAttribute("permissionList",permissionList);
        return permissionList;

    }

    @PostMapping("/{permissionId}")
    public String updatePermission(@ModelAttribute("permissionForm") Permission permission, @PathVariable Long permissionId, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add-permission";
        }
        permission.setId(permissionId);
        securityService.addPermission(permission);
        model.addAttribute("success", "Permission updated successfully");
        return "/admin/permissions";
    }

    @PostMapping("/{permissionId}/delete")
    public String deletePermission(@PathVariable Long permissionId, Model model){
        Permission permission = securityService.getPermission(permissionId);
        securityService.deletePermission(permission);
        model.addAttribute("success", "Permission deleted successfully");
        return "redirect:/admin/permissions";
    }
}

