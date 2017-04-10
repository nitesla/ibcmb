package longbridge.services.implementations;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.models.Permission;
import longbridge.models.Role;

import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;

import longbridge.services.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */

@Service
public class SecurityServiceImpl implements SecurityService {


    RoleRepo roleRepo;

    PermissionRepo permissionRepo;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public SecurityServiceImpl(RoleRepo roleRepo, PermissionRepo permissionRepo, BCryptPasswordEncoder passwordEncoder){
       this.roleRepo = roleRepo;
       this.permissionRepo = permissionRepo;
       this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void addRole(RoleDTO roleDTO) {
        Role role = convertDTOToEntity(roleDTO);
        roleRepo.save(role);
    }

    @Override
    public RoleDTO getRole(Long id) {
        Role role = roleRepo.findOne(id);
        return convertEntityToDTO(role);
    }

    @Override
    public Iterable<RoleDTO> getRoles() {
        Iterable<Role> roles = roleRepo.findAll();
        return convertRoleEntitiesToDTOs(roles);
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        Role role =convertDTOToEntity(roleDTO);
        roleRepo.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepo.findOne(id);
        role.setDelFlag("Y");
        roleRepo.save(role);
    }

    public void addPermission(PermissionDTO permissionDTO) {
        Permission permission = convertDTOToEntity(permissionDTO);
        permissionRepo.save(permission);
    }

    @Override
    public PermissionDTO getPermission(Long id) {
        Permission permission = permissionRepo.findOne(id);
        return convertEntityToDTO(permission);
    }

    @Override
    public Iterable<PermissionDTO> getPermissions() {
        Iterable<Permission> permissions = permissionRepo.findAll();
        return convertPermissionEntitiesToDTOs(permissions);
    }

    @Override
    public void updatePermission(PermissionDTO permissionDTO) {
        Permission permission =convertDTOToEntity(permissionDTO);
        permissionRepo.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        Permission permission = permissionRepo.findOne(id);
        permission.setDelFlag("Y");
        permissionRepo.save(permission);
    }


    @Override
    public String generatePassword(){
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(0,12);
    }


    private RoleDTO convertEntityToDTO(Role role){
        return  modelMapper.map(role,RoleDTO.class);
    }

    private Role convertDTOToEntity(RoleDTO roleDTO){
        return  modelMapper.map(roleDTO,Role.class);
    }

    private Iterable<RoleDTO> convertRoleEntitiesToDTOs(Iterable<Role> roles){
        List<RoleDTO> roleDTOs = new ArrayList<>();
        for(Role role: roles){
            RoleDTO roleDTO = modelMapper.map(role,RoleDTO.class);
            roleDTOs.add(roleDTO);
        }
        return roleDTOs;
    }

    private PermissionDTO convertEntityToDTO(Permission permission){
        return  modelMapper.map(permission,PermissionDTO.class);
    }

    private Permission convertDTOToEntity(PermissionDTO permissionDTO){
        return  modelMapper.map(permissionDTO,Permission.class);
    }

    private Iterable<PermissionDTO> convertPermissionEntitiesToDTOs(Iterable<Permission> permissions){
        List<PermissionDTO> permissionDTOs = new ArrayList<>();
        for(Permission permission: permissions){
            PermissionDTO permissionDTO = modelMapper.map(permission,PermissionDTO.class);
            permissionDTOs.add(permissionDTO);
        }
        return permissionDTOs;
    }

}
