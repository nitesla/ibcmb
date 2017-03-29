package longbridge.services.implementations;

import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
public class OperationsUserServiceImpl  implements OperationsUserService {

    public OperationsUserServiceImpl() {

    }
    /*
    @Autowired
    public OperationsUserServiceImpl() {

    }
    */

    @Override
    public OperationsUser getUser() {
        return null;
    }

    @Override
    public Iterable<OperationsUser> getUsers() {
        return null;
    }

    @Override
    public void setPassword(OperationsUser User, String password) {

    }

    @Override
    public void addUser() {

    }

    @Override
    public void resetPassword() {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public void generateAndSendPassword() {

    }
}
