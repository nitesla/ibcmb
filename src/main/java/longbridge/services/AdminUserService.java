package longbridge.services;

import longbridge.models.AdminUser;

import java.util.List;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface AdminUserService {

    AdminUser getUser();

    List<AdminUser> getUsers();

    void setPassword(AdminUser User, String password);

    void addUser();

    void resetPassword();

    void changePassword(String oldPassword, String newPassword);

    void generateAndSendPassword();
}
