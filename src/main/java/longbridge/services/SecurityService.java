package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;

/**
 * The {@code SecurityService} interface provides the methods for managing roles, profiles and permissions
 * @author Fortunatus Ekenachi
 * Created on 3/28/2017.
 */
public interface SecurityService {



    /**
     * Adds a new role to the system
     * @param  role the role to be added to the system
     */
    void addRole(RoleDTO role);

    /**
     * Returns a role
     * @return the role id
     */
    RoleDTO getRole(Long id);

    /**
     * Returns all  the roles in the system
     * @return a list of roles
     */
    Iterable<RoleDTO> getRoles();
    
    Page<RoleDTO> getRoles(Pageable pageDetails);

    /**
     * Updates the details of the specified userType
     * @param role the role to be updated
     */
    void updateRole(RoleDTO role);

    /**
     * Deletes a role
     * This is a logical deletion because the data is not removed from the database
     * @param id
     */
    void deleteRole(Long id);

    /**
     * Adds a new permission to the system
     * @param permission  the permission to be deleted
     */
    void addPermission(PermissionDTO permission);

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
    
    Page<PermissionDTO> getPermissions(Pageable pageDetails);

    /**
     * Updates the details of the permission
     * @param permission the permission to be deleted
     */
    void updatePermission(PermissionDTO permission);

    /**
     * Deletes the permissiont
     * @param id the permission
     */
    void deletePermission(Long id);

    /** Generates a random password of 12 characters
     *
     * @return a random password string
     */
    String generatePassword();


}
