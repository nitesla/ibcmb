package longbridge.services;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
public interface RoleService {


    /**
     * Adds a new role to the system
     * @param  role the role to be added to the system
     */
    String addRole(RoleDTO role) throws InternetBankingException;
   // void addRole(Role role);

    /**
     * Returns a roleDTO
     * @return the role id
     */
    RoleDTO getRole(Long id);


    Role getTheRole(Long id);

    /**
     * Returns all  the roles in the system
     * @return a list of roles
     */
    Iterable<RoleDTO> getRoles();

    Page<RoleDTO> getRoles(Pageable pageDetails);

    Page<User> getUsers(RoleDTO role, Pageable pageDetails);

    /**
     * Updates the details of the specified userType
     * @param role the role to be updated
     */
    String updateRole(RoleDTO role) throws InternetBankingException;

    /**
     * Deletes a role
     * This is a logical deletion because the data is not removed from the database
     * @param id
     */
    String deleteRole(Long id) throws InternetBankingException;

    /**
     * Adds a new permission to the system
     * @param permission  the permission to be deleted
     */
    String addPermission(PermissionDTO permission) throws InternetBankingException;

    /**
     * Returns the given  permission
     * @return the permission's id
     */
    PermissionDTO getPermission(Long id);

    /**
     * Returns all  the permissions in the system
     * @return a list of permissions
     */
    Iterable<PermissionDTO> getPermissions();
    
    /**
     * Returns all  the permissions not in the role
     * @return a list of permissions
     */
    Iterable<PermissionDTO> getPermissionsNotInRole(RoleDTO role);


    Page<PermissionDTO> getPermissions(Pageable pageDetails);

    /**
     * Updates the details of the permission
     * @param permission the permission to be deleted
     */
    String updatePermission(PermissionDTO permission) throws InternetBankingException;

    /**
     * Deletes the permissiont
     * @param id the permission
     */
    String deletePermission(Long id) throws InternetBankingException;

 
}
