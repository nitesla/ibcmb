package longbridge.services;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.User;
import longbridge.models.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
public interface RoleService {


    /**
     * Adds a new role to the system
     * @param  role the role to be added to the system
     */
    @PreAuthorize("hasAuthority('ADD_ROLE')")
    String addRole(RoleDTO role) throws InternetBankingException;
   // void addRole(Role role);

    /**
     * Returns a roleDTO
     * @return the role id
     */
    @PreAuthorize("hasAuthority('GET_ROLE')")
    RoleDTO getRole(Long id);

    @PreAuthorize("hasAuthority('GET_ROLE')")
    Role getRoleByUserTypeAndName(UserType userType,String name);

    //@PreAuthorize("hasAuthority('GET_ROLE')")
    Role getTheRole(String roleName);

    /**
     * Returns all  the roles in the system
     * @return a list of roles
     */
    @PreAuthorize("hasAuthority('GET_ROLE')")
    Iterable<RoleDTO> getRoles();

    @PreAuthorize("hasAuthority('GET_ROLE')")
    Page<RoleDTO> getRoles(Pageable pageDetails);
    
    @PreAuthorize("hasAuthority('GET_ROLE')")
    Page<RoleDTO> findRoles(String pattern, Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_ROLE')")
    Page<User> getUsers(RoleDTO role, Pageable pageDetails);

    /**
     * Updates the details of the specified userType
     * @param role the role to be updated
     */
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    String updateRole(RoleDTO role) throws InternetBankingException;

    /**
     * Deletes a role
     * This is a logical deletion because the data is not removed from the database
     * @param id
     */
//    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    String deleteRole(Long id) throws InternetBankingException;

    /**
     * Adds a new permission to the system
     * @param permission  the permission to be deleted
     */
//    @PreAuthorize("hasAuthority('ADD_ROLE')")
    String addPermission(PermissionDTO permission) throws InternetBankingException;

    /**
     * Returns the given  permission
     * @return the permission's id
     */
    @PreAuthorize("hasAuthority('GET_ROLE')")
    PermissionDTO getPermission(Long id);

    /**
     * Returns all  the permissions in the system
     * @return a list of permissions
     */
    @PreAuthorize("hasAuthority('GET_ROLE')")
    Iterable<PermissionDTO> getPermissions();
    
    /**
     * Returns all  the permissions not in the role
     * @return a list of permissions
     */
    @PreAuthorize("hasAuthority('GET_ROLE')")
    Iterable<PermissionDTO> getPermissionsNotInRole(RoleDTO role);

    @PreAuthorize("hasAuthority('GET_ROLE')")
    Page<PermissionDTO> getPermissions(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_ROLE')")
    Page<PermissionDTO> findPermissions(String pattern,Pageable pageDetails);
    /**
     * Updates the details of the permission
     * @param permission the permission to be deleted
     */
    @PreAuthorize("hasAuthority('ADD_ROLE')")
    String updatePermission(PermissionDTO permission) throws InternetBankingException;

    /**
     * Deletes the permissiont
     * @param id the permission
     */
    @PreAuthorize("hasAuthority('DELETE_PERMISSION')")
    String deletePermission(Long id) throws InternetBankingException;

 List<RoleDTO> getRolesByUserType(UserType userType);

    List<PermissionDTO> findPermisionsByCategory(String category);
    Permission findPermisionsByCode(String code);


}
