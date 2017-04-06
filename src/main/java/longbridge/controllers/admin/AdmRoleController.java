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
        Role role = modelMapper.map(roleDTO,Role.class);
        securityService.addRole(role);
        model.addAttribute("success", "Role added successfully");
        return "/admin/roles";
    }

    @GetMapping("/{roleId}")
    public Role getRole(@PathVariable Long roleId, Model model){
        Role role = securityService.getRole(roleId);
        RoleDTO roleDTO = modelMapper.map(role,RoleDTO.class);
        model.addAttribute("role",roleDTO);
        return role;
    }

    @GetMapping
    public Iterable<Role> getRoles(Model model){
        Iterable<Role> roleList = securityService.getRoles();
        model.addAttribute("roleList",roleList);
        return roleList;

    }

    @PostMapping("/{roleId}")
    public String updateRole(@ModelAttribute("role") RoleDTO roleDTO, @PathVariable Long roleId, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add-role";
        }
        roleDTO.setId(roleId);
        Role role = modelMapper.map(roleDTO,Role.class);
        securityService.addRole(role);
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
