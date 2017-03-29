package longbridge.services;

import longbridge.models.CorporateUser;

import java.util.List;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface CorporateUserService {

    CorporateUser getUser(Long id);

    List<CorporateUser> getUsers();

    void setPassword(CorporateUser User, String password);

    void addUser(CorporateUser user);

    void resetPassword(CorporateUser user);

    void deleteUser(CorporateUser user);

    void enableUser(CorporateUser user);

    void disableUser(CorporateUser user);

    void changePassword(CorporateUser user, String oldPassword, String newPassword);

    void generateAndSendPassword(CorporateUser user);
}
