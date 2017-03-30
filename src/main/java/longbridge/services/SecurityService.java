package longbridge.services;

import longbridge.models.Permission;
import longbridge.models.Profile;
import longbridge.models.Role;

/**
 * The {@code SecurityService} interface provides the methods for managing roles, profiles and permissions
 * Created by Fortune on 3/28/2017.
 */
public interface SecurityService {

    /**
     * Adds a new profile to the system
     * @param profile the profile to be added
     */
    void addProfile(Profile profile);

    /**
     * Returns a profile
     * @param id the profile id
     * @return
     */
    Profile getProfile(Long id);

    /**
     * Updates a given profile
     * @param profile the profile to be updated
     */
    void updateProfile(Profile profile);

    /**
     * Deletes a profile
     * This is a logical deletion because the data is not removed from the database
     * @param id the profile id to be deleted
     */
    void deleteProfile(Long id);

    /**
     * Adds a new role to the system
     * @param role the role to be added to the system
     */
    void addRole(Role role);

    /**
     * Returns a role
     * @return the role id
     */
    Role getRole(Long id);

    /**
     * Updates the details of the specified role
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
