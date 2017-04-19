package longbridge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Date;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
public interface RoleService extends Verifiable<RoleDTO> {


    /**
     * Adds a new role to the system
     * @param  role the role to be added to the system
     */
    void addRole(RoleDTO role);

    /**
     * Returns a roleDTO
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

    /**
     * Updates the details of the specified userType
     * @param role the role to be updated
     */
    void updateRole(Role role);

    /**
     * Deletes a role
     * This is a logical deletion because the data is not removed from the database
     */
    void deleteRole(Role role);

    /**
     * Adds a new permission to the system
     * @param permission  the permission to be deleted
     */
    void addPermission(Permission permission);



    /**
     * Updates the details of the permission
     * @param permission the permission to be deleted
     */
    void updatePermission(Permission permission);

    /**
     * Deletes the permissiont
     * @param permission the permission
     */
    void deletePermission(Permission permission);
}
