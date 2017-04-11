package longbridge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.models.*;

import java.io.IOException;
import java.util.Date;

/**
 * Created by LB-PRJ-020 on 4/11/2017.
 */
public interface RoleService extends Verifiable<Role> {

    /**
     * Returns a role
     * @return the role id
     */
    Role getRole(Long id);

    /**
     * Returns all  the roles in the system
     * @return a list of roles
     */
    Iterable<Role> getRoles();

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
     * Returns the given  permission
     * @return the permission's id
     */
    Permission getPermission(Long id);

    /**
     * Returns all  the permissions in the system
     * @return a list of permissions
     */
    Iterable<Permission> getPermissions();

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
