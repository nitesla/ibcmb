package longbridge.services.implementations;

import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.UserType;
import longbridge.services.SecurityService;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
public class SecurityServiceImpl implements SecurityService {

    /**
     * Adds a new role to the system
     *
     * @param role the role to be added
     */
    @Override
    public void addProfile(Role role) {

    }

    /**
     * Returns a profile
     *
     * @param id the profile id
     * @return
     */
    @Override
    public Role getProfile(Long id) {
        return null;
    }

    /**
     * Updates a given role
     *
     * @param role the role to be updated
     */
    @Override
    public void updateProfile(Role role) {

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
     * Adds a new userType to the system
     *
     * @param userType the userType to be added to the system
     */
    @Override
    public void addRole(UserType userType) {

    }

    /**
     * Returns a role
     *
     * @param id
     * @return the role id
     */
    @Override
    public UserType getRole(Long id) {
        return null;
    }

    /**
     * Updates the details of the specified userType
     *
     * @param userType the userType to be updated
     */
    @Override
    public void updateRole(UserType userType) {

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
