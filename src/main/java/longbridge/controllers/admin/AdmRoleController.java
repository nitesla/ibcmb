package longbridge.controllers.admin;

import longbridge.dtos.RoleDTO;
import longbridge.models.AdminUser;
import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.RoleRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.RoleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Fortune on 4/5/2017.
 */

@Controller
@RequestMapping("admin/roles")
public class AdmRoleController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AdminUserRepo adminUserRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private VerificationRepo verificationRepo;


    @GetMapping("/new")
    public String addRole(RoleDTO roleDTO){
        return "adm/role/add";
    }

    @PostMapping("/new")
    public String createRole(@ModelAttribute("roleForm") RoleDTO roleDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "adm/role/add";
        }
        logger.info("Role {}", roleDTO.toString());
        AdminUser adminUser = adminUserRepo.findOne(1l);
        roleService.add(roleDTO, adminUser);
        model.addAttribute("success", "Role added successfully");
        return "/admin/roles";
    }

    @GetMapping("/{roleId}")
    public RoleDTO getRole(@PathVariable Long roleId, Model model){
        RoleDTO role = roleService.getRole(roleId);
        model.addAttribute("role",role);
        return role;
    }

    @GetMapping
    public Iterable<RoleDTO> getRoles(Model model){
        Iterable<RoleDTO> roleList = roleService.getRoles();
        model.addAttribute("roleList",roleList);
        return roleList;

    }

    @PostMapping("/{roleId}")
    public String updateRole(@ModelAttribute("role") RoleDTO roleDTO, @PathVariable Long roleId, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "add-role";
        }
        roleDTO.setId(roleId);

        AdminUser adminUser = adminUserRepo.findOne(1l);
        logger.info("Code {}", roleDTO.toString());
        roleService.modify(roleDTO, adminUser);
        model.addAttribute("success", "Role updated successfully");
        return "/admin/roles";
    }

    @PostMapping("/{roleId}/delete")
    public String deleteRole(@PathVariable Long roleId, Model model){
        roleService.deleteRole(roleId);
        model.addAttribute("success", "Role deleted successfully");
        return "redirect:/admin/roles";
    }

    @PostMapping("/{id}/verify")
    public String verify(@PathVariable Long id) {
        logger.info("id {}", id);

        //todo check verifier role
        AdminUser adminUser = adminUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);

        if (verification == null || Verification.VerificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

        try {
            roleService.verify(verification, adminUser);
        } catch (IOException e) {
            logger.error("Error occurred", e);
        }
        return "role/add";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable Long id) {
        //todo check verifier role
        AdminUser adminUser = adminUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);

        if (verification == null || Verification.VerificationStatus.PENDING != verification.getStatus())
            return "Verification not found";

        roleService.decline(verification, adminUser, "todo get the  reason from the frontend");
        return "role/add";
    }

}
