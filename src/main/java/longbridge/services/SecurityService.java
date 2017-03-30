package longbridge.services;

import longbridge.models.Permission;
import longbridge.models.Profile;
import longbridge.models.Role;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface SecurityService {

    void addProfile();

    Profile getProfile();

    void updateProfile();

    void deleteProfile();

    void addRole();

    Role getRole();

    void updateRole();

    void deleteRole();

    void addPermission();

    Permission getPermission();

    void updatePermission();

    void deletePermission();
}
