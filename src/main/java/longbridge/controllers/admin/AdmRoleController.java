package longbridge.controllers.admin;

import longbridge.dtos.RoleDTO;
import longbridge.models.Role;
import longbridge.services.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fortune on 4/5/2017.
 */

@RestController
@RequestMapping("admin/roles")
public class AdmRoleController {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/new")
    public String addRole(){
        return "add-role";
    }

    @PostMapping
    public String createRole(@ModelAttribute("roleForm") RoleDTO roleDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add-role";
        }
        securityService.addRole(roleDTO);
        model.addAttribute("success", "Role added successfully");
        return "/admin/roles";
    }

    @GetMapping("/{roleId}")
    public RoleDTO getRole(@PathVariable Long roleId, Model model){
        RoleDTO role = securityService.getRole(roleId);
        model.addAttribute("role",role);
        return role;
    }

    @GetMapping
    public Iterable<RoleDTO> getRoles(Model model){
        Iterable<RoleDTO> roleList = securityService.getRoles();
        model.addAttribute("roleList",roleList);
        return roleList;

    }

    @PostMapping("/{roleId}")
    public String updateRole(@ModelAttribute("role") RoleDTO roleDTO, @PathVariable Long roleId, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add-role";
        }
        roleDTO.setId(roleId);
        securityService.addRole(roleDTO);
        model.addAttribute("success", "Role updated successfully");
        return "/admin/roles";
    }

    @PostMapping("/{roleId}/delete")
    public String deleteRole(@PathVariable Long roleId, Model model){
        securityService.deleteRole(roleId);
        model.addAttribute("success", "Role deleted successfully");
        return "redirect:/admin/roles";
    }
}
