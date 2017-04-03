package longbridge.services;

import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.UserType;

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
    void addRole(Role role);

    /**
     * Returns a role
     * @return the role id
     */
    Role getRole(Long id);

    /**
     * Updates the details of the specified userType
     * @param role the role to be updated
     */
    void updateRole(Role role);

    /**
     * Deletes a role
     * This is a logical deletion because the data is not removed from the database
     */
    void deleteRole(Long id);

    /**
     * Adds a new permission to the system
     * @param permission  the permission to be deleted
     */
    void addPermission(Permission permission);

    /**
     * Returns the given  permission
     * @return the permission's id
     */
    Permission getPermission(Long id);

    /**
     * Updates the details of the permission
     * @param permission the permission to be deleted
     */
    void updatePermission(Permission permission);

    /**
     * Deletes the permissiont
     * @param id the permissions id
     */
    void deletePermission(Long id);
}
