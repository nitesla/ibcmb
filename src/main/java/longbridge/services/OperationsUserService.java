package longbridge.services;

import longbridge.models.OperationsUser;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface OperationsUserService {

    OperationsUser getUser();

    Iterable<OperationsUser> getUsers();

    void setPassword(OperationsUser User, String password);

    void addUser();

    void resetPassword();

    void changePassword(String oldPassword, String newPassword);

    void generateAndSendPassword();
}
