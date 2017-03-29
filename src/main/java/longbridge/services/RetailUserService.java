package longbridge.services;

import longbridge.models.RetailUser;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface RetailUserService {

    RetailUser getUser();

    Iterable<RetailUser> getUsers();

    void setPassword(RetailUser User, String password);

    void addUser();

    void resetPassword();

    void changePassword(String oldPassword, String newPassword);

    void generateAndSendPassword();
}
