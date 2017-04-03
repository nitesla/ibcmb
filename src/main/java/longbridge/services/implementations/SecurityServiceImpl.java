package longbridge.services.implementations;

import longbridge.models.Permission;
import longbridge.models.Profile;
import longbridge.models.Role;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.ProfileRepo;
import longbridge.repositories.RoleRepo;
import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    ProfileRepo profileRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    PermissionRepo permissionRepo;

    /**
     * Adds a new profile to the system
     * @param profile the profile to be added
     */
    @Override
    public void addProfile(Profile profile) {
        profileRepo.save(profile);

    }

    /**
     * Returns a profile specified by the {@code id}
     * @param id the profile id
     * @return
     */
    @Override
    public Profile getProfile(Long id) {
        return profileRepo.findOne(id);
    }

    /**
     * Updates the given profile
     * @param profile the profile to be updated
     */
    @Override
    public void updateProfile(Profile profile) {
      profileRepo.save(profile);
    }

    /**
     * Deletes a profile
     * This is a logical deletion because the data is not removed from the database
     * @param id the profile id to be deleted
     */
    @Override
    public void deleteProfile(Long id) {
     //roleRepo.delete(id); //implement logical delete
    }

    /**
     * Adds a new role to the system
     * @param role the role to be added to the system
     */
    @Override
    public void addRole(Role role) {
    roleRepo.save(role);
    }

    /**
     * Returns the specified role
     * @param id the role's id
     * @return the role id
     */
    @Override
    public Role getRole(Long id) {
        return roleRepo.findOne(id);
    }

    /**
     * Updates the details of the specified role
     * @param role the role to be updated
     */
    @Override
    public void updateRole(Role role) {
    roleRepo.save(role);
    }

    /**
     * Deletes a role specified by the {@code id}
     * This is a logical deletion because the data is not removed from the database
     * @param id the role id to be deleted
     */
    @Override
    public void deleteRole(Long id) {
    //roleRepo.delete(id);//implement logical delete
    }

    /**
     * Adds a new permission to the system
     * @param permission the permission to be deleted
     */
    @Override
    public void addPermission(Permission permission) {
    permissionRepo.save(permission);
    }

    /**
     * Returns the given  permission
     * @param id the permission's id
     * @return the permission
     */
    @Override
    public Permission getPermission(Long id) {
        return  permissionRepo.findOne(id);
    }

    /**
     * Updates the details of the given permission
     * @param permission the permission to be deleted
     */
    @Override
    public void updatePermission(Permission permission) {
    permissionRepo.save(permission);
    }

    /**
     * Deletes the permission specified by the {@code id}
     * @param id the permission's id
     */
    @Override
    public void deletePermission(Long id) {
    //permissionRepo.delete(id);//implement logical delete
    }
}
