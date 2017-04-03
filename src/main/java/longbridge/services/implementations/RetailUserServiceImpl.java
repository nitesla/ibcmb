package longbridge.services.implementations;

import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;

import java.util.Collection;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
public class RetailUserServiceImpl implements RetailUserService {


    @Override
    public RetailUser getUser(Long id) {
        return null;
    }

    @Override
    public Collection<RetailUser> getUsers() {
        return null;
    }

    @Override
    public void setPassword(RetailUser User, String password) {

    }

    @Override
    public void addUser(RetailUser retailUser) {

    }

    @Override
    public void resetPassword(RetailUser retailUser) {

    }

    @Override
    public void changePassword(RetailUser retailUser, String oldPassword, String newPassword) {

    }

    @Override
    public void generateAndSendPassword(RetailUser retailUser) {

    }
}
