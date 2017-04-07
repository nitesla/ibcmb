package longbridge.services;

import longbridge.models.Permission;
import longbridge.models.Role;

import java.math.BigInteger;
import java.security.SecureRandom;

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
     * @param permission the permission object
     */
    void deletePermission(Permission permission);

    /** Generates a random password of 12 characters
     *
     * @return a random password string
     */
    String generatePassword();


}
