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
     * @param role the role to be added
     */
    void addProfile(Role role);

    /**
     * Returns a profile that has the specified {@code id}
     * @param id the profile id
     * @return the profile
     */
    Role getProfile(Long id);

    /**
     * Updates a given role
     * @param role the role to be updated
     */
    void updateProfile(Role role);

    /**
     * Deletes a profile
     * This is a logical deletion because the data is not removed from the database
     * @param id the profile id to be deleted
     */
    void deleteProfile(Long id);

    /**
     * Adds a new userType to the system
     * @param userType the userType to be added to the system
     */
    void addRole(UserType userType);

    /**
     * Returns a role
     * @return the role id
     */
    UserType getRole(Long id);

    /**
     * Updates the details of the specified userType
     * @param userType the userType to be updated
     */
    void updateRole(UserType userType);

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
