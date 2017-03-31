package longbridge.services.implementations;

import longbridge.models.Permission;
import longbridge.models.Profile;
import longbridge.models.Role;
import longbridge.services.SecurityService;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
public class SecurityServiceImpl implements SecurityService {

    /**
     * Adds a new profile to the system
     *
     * @param profile the profile to be added
     */
    @Override
    public void addProfile(Profile profile) {

    }

    /**
     * Returns a profile
     *
     * @param id the profile id
     * @return
     */
    @Override
    public Profile getProfile(Long id) {
        return null;
    }

    /**
     * Updates a given profile
     *
     * @param profile the profile to be updated
     */
    @Override
    public void updateProfile(Profile profile) {

    }

    /**
     * Deletes a profile
     * This is a logical deletion because the data is not removed from the database
     *
     * @param id the profile id to be deleted
     */
    @Override
    public void deleteProfile(Long id) {

    }

    /**
     * Adds a new role to the system
     *
     * @param role the role to be added to the system
     */
    @Override
    public void addRole(Role role) {

    }

    /**
     * Returns a role
     *
     * @param id
     * @return the role id
     */
    @Override
    public Role getRole(Long id) {
        return null;
    }

    /**
     * Updates the details of the specified role
     *
     * @param role the role to be updated
     */
    @Override
    public void updateRole(Role role) {

    }

    /**
     * Deletes a role
     * This is a logical deletion because the data is not removed from the database
     *
     * @param id the role id to be deleted
     */
    @Override
    public void deleteRole(Long id) {

    }

    /**
     * Adds a new permission to the system
     *
     * @param permission the permission to be deleted
     */
    @Override
    public void addPermission(Permission permission) {

    }

    /**
     * Returns the given  permission
     *
     * @param id
     * @return the permission's id
     */
    @Override
    public Permission getPermission(Long id) {
        return null;
    }

    /**
     * Updates the details of the permission
     *
     * @param permission the permission to be deleted
     */
    @Override
    public void updatePermission(Permission permission) {

    }

    /**
     * Deletes the permissiont
     *
     * @param id the permissions id
     */
    @Override
    public void deletePermission(Long id) {

    }
}
