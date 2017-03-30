package longbridge.services;

import longbridge.models.OperationsUser;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface OperationsUserService {

    OperationsUser getUser(Long id);

    Iterable<OperationsUser> getUsers();



    boolean  addUser(OperationsUser User);

    void resetPassword(OperationsUser User,String newPassword);

    boolean changePassword(OperationsUser User,String oldPassword, String newPassword);

    void generateAndSendPassword();
}
