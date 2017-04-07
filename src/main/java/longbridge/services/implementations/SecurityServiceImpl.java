package longbridge.services.implementations;

import longbridge.models.Permission;
import longbridge.models.Role;

import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;

import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
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
    public SecurityServiceImpl(RoleRepo roleRepo, PermissionRepo permissionRepo, BCryptPasswordEncoder passwordEncoder){
       this.roleRepo = roleRepo;
       this.permissionRepo = permissionRepo;
       this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void addRole(Role role) {
        roleRepo.save(role);
    }

    @Override
    public Role getRole(Long id) {
        return roleRepo.findOne(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepo.findAll();
    }

    @Override
    public void updateRole(Role role) {
        roleRepo.save(role);
    }

    @Override
    public void deleteRole(Role role) {
        role.setDelFlag("Y");
        roleRepo.save(role);
    }

    @Override
    public void addPermission(Permission permission) {
        permissionRepo.save(permission);
    }

    @Override
    public Permission getPermission(Long id) {
        return permissionRepo.findOne(id);
    }

    @Override
    public List<Permission> getPermissions() {
        return permissionRepo.findAll();
    }

    @Override
    public void updatePermission(Permission permission) {
        permissionRepo.save(permission);
    }
    @Override
    public String generatePassword(){
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(0,12);
    }

    @Override
    public void deletePermission(Permission permission) {
        permission.setDelFlag("Y");
        permissionRepo.save(permission);
    }


}
