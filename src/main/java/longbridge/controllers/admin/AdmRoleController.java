package longbridge.controllers.admin;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.dtos.ServiceReqConfigDTO;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

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
    private AdminUserRepo adminUserRepo;

    @Autowired
    private VerificationRepo verificationRepo;


    @GetMapping("/new")
    public String addRole(Model model){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setPermissions(new ArrayList<>());
        model.addAttribute("role", new RoleDTO());
        Iterable<PermissionDTO> permissions=roleService.getPermissions();
//        List<PermissionDTO> getArrayList=new ArrayList<>();
        model.addAttribute("permissions",permissions);
//        model.addAttribute("getArrayList",getArrayList);
        return "adm/role/add";
    }

    @PostMapping
    public String createRole(@ModelAttribute("role") @Valid Role roleDTO, BindingResult result,WebRequest request, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/role/add";
        }
        logger.info("Role {}", roleDTO.toString());
        roleService.addRole(roleDTO);
        redirectAttributes.addFlashAttribute("success", "Role added successfully");
        return "redirect:/admin/roles";
    }

    @GetMapping("/{roleId}")
    public String getRole(@PathVariable Long roleId, Model model){
        RoleDTO role = roleService.getRole(roleId);
        model.addAttribute("role",role);
        return "adm/role/edit";
    }

    @GetMapping
    public String getRoles(Model model){
        return "adm/role/view";
    }

    @GetMapping("/{reqId}/edit")
    public String  editConfig(@PathVariable Long reqId, Model model){
        RoleDTO role = roleService.getRole(reqId);
        Iterable<PermissionDTO> permissionDTOs =roleService.getRole(reqId).getPermissions();
        model.addAttribute("role",role);
        model.addAttribute("permissions",role);

        return "/adm/role/edit";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<RoleDTO> getRoles(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<RoleDTO> roles = roleService.getRoles(pageable);
        DataTablesOutput<RoleDTO> out = new DataTablesOutput<RoleDTO>();
        out.setDraw(input.getDraw());
        out.setData(roles.getContent());
        out.setRecordsFiltered(roles.getTotalElements());
        out.setRecordsTotal(roles.getTotalElements());
        return out;
    }

    @PostMapping("/update")
    public String updateRole(@ModelAttribute("role") RoleDTO roleDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "adm/role/add";
        }
        //roleDTO.setId(roleId);
        roleService.addRole(roleDTO);
//        AdminUser adminUser = adminUserRepo.findOne(1l);
//        logger.info("Code {}", roleDTO.toString());
//        roleService.modify(roleDTO, adminUser);
         redirectAttributes.addFlashAttribute("success", "Role updated successfully");
        return "redirect:/admin/roles";
    }

    @GetMapping("/{roleId}/delete")
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes redirectAttributes){
        roleService.deleteRole(roleId);
        redirectAttributes.addFlashAttribute("success", "Role deleted successfully");
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
            logger.error("Error occurred verifying Role", e);
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
