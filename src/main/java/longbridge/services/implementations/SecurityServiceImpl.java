package longbridge.services.implementations;

import longbridge.models.Permission;
import longbridge.models.Role;

import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;

import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */

@Service
public class SecurityServiceImpl implements SecurityService {


    @Autowired
    RoleRepo roleRepo;
    @Autowired
    PermissionRepo permissionRepo;


    @Override
    public void addRole(Role role) {

    }

    @Override
    public Role getRole(Long id) {
        return null;
    }

    @Override
    public Iterable<Role> getRoles() {
        return null;
    }

    @Override
    public void updateRole(Role role) {

    }

    @Override
    public void deleteRole(Long id) {

    }

    @Override
    public void addPermission(Permission permission) {

    }

    @Override
    public Permission getPermission(Long id) {
        return null;
    }

    @Override
    public Iterable<Permission> getPermissions() {
        return null;
    }

    @Override
    public void updatePermission(Permission permission) {

    }

    @Override
    public void deletePermission(Long id) {

    }
}
