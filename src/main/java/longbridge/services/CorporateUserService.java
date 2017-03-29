package longbridge.services;

import longbridge.models.CorporateUser;

import java.util.List;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface CorporateUserService {
    CorporateUser getUser();

    List<CorporateUser> getUsers();

    void setPassword(CorporateUser User, String password);

    void addUser();

    void resetPassword();

    void changePassword(String oldPassword, String newPassword);

    void generateAndSendPassword();
}
