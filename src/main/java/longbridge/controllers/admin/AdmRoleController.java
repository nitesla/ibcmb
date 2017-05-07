package longbridge.controllers.admin;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.models.AdminUser;
import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.User;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.RoleRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.RoleService;

import org.apache.commons.lang3.math.NumberUtils;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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


    @GetMapping("/new")
    public String addRole(Model model){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setPermissions(new ArrayList<>());
        model.addAttribute("role", new RoleDTO());
        return "adm/role/add";
    }

    @ModelAttribute("permissions")
    public Iterable<PermissionDTO> getPermissions(NativeWebRequest request){
    	 HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
    	 Map<String,String> map = (Map<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    	Long reqId = NumberUtils.createLong(map.get("reqId"));
    	Iterable<PermissionDTO> permissions;
    	if(reqId != null){
    		RoleDTO role = roleService.getRole(reqId);
    		permissions = roleService.getPermissionsNotInRole(role);
    	}else
    		permissions = roleService.getPermissions();
    	 return permissions ;
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
    public String  editRole(@PathVariable Long reqId, Model model){
        RoleDTO role = roleService.getRole(reqId);
        Iterable<PermissionDTO> permissionDTOs =roleService.getRole(reqId).getPermissions();
        model.addAttribute("role",role);
//        model.addAttribute("permissions",role);

        return "/adm/role/edit";
    }
    
    @GetMapping("/{reqId}/view")
    public String  viewRole(@PathVariable Long reqId, Model model){
        RoleDTO role = roleService.getRole(reqId);
        model.addAttribute("role",role);
        return "/adm/role/details";
    }
    
    @GetMapping(path = "/{roleId}/users")
    public @ResponseBody
    DataTablesOutput<User> getUsers(@PathVariable Long roleId, DataTablesInput input){
    	RoleDTO role = roleService.getRole(roleId);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<User> users = roleService.getUsers(role, pageable);
        DataTablesOutput<User> out = new DataTablesOutput<User>();
        out.setDraw(input.getDraw());
        out.setData(users.getContent());
        out.setRecordsFiltered(users.getTotalElements());
        out.setRecordsTotal(users.getTotalElements());
        return out;
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

    @PostMapping
    public String createRole(@ModelAttribute("role") @Valid RoleDTO roleDTO, BindingResult result,WebRequest request, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/role/add";
        }
        logger.info("Role {}", roleDTO.toString());
        List<PermissionDTO> permissionList = new ArrayList<>();
        
        String[] permissions = request.getParameterValues("permissionsList");
        for(String perm : permissions){
        	PermissionDTO pdto = new PermissionDTO();
        	pdto.setId(NumberUtils.toLong(perm));
        	permissionList.add(pdto);
        }
        roleDTO.setPermissions(permissionList);
        roleService.addRole(roleDTO);
        redirectAttributes.addFlashAttribute("message", "Role added successfully");
        return "redirect:/admin/roles";
    }

    @PostMapping("/update")
    public String updateRole(@ModelAttribute("role") RoleDTO roleDTO, BindingResult result,WebRequest request, RedirectAttributes redirectAttributes) {
    	 if(result.hasErrors()){
             return "adm/role/add";
         }
         logger.info("Role {}", roleDTO.toString());
         List<PermissionDTO> permissionList = new ArrayList<>();
         
         String[] permissions = request.getParameterValues("permissionsList");
         for(String perm : permissions){
         	PermissionDTO pdto = new PermissionDTO();
         	pdto.setId(NumberUtils.toLong(perm));
         	permissionList.add(pdto);
         }
         roleDTO.setPermissions(permissionList);
         roleService.updateRole(roleDTO);
         redirectAttributes.addFlashAttribute("message", "Role updated successfully");
         return "redirect:/admin/roles";
    }

    @GetMapping("/{roleId}/delete")
    public String deleteRole(@PathVariable Long roleId, RedirectAttributes redirectAttributes){
        roleService.deleteRole(roleId);
        redirectAttributes.addFlashAttribute("message", "Role deleted successfully");
        return "redirect:/admin/roles";
    }
}
