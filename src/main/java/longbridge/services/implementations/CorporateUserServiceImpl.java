package longbridge.services.implementations;

import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.services.CorporateUserService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Fortune on 4/4/2017.
 */

@Service
public class CorporateUserServiceImpl implements CorporateUserService {


    public CorporateUserServiceImpl(){}

    @Override
    public CorporateUser getUser(Long id) {
        return null;
    }

    @Override
    public Iterable<CorporateUser> getUsers(Corporate Corporate) {
        return null;
    }

    @Override
    public Iterable<CorporateUser> getUsers() {
        return null;
    }

    @Override
    public void setPassword(CorporateUser user, String hashedPassword) {

    }

    @Override
    public boolean updateUser(CorporateUser user) {
        return false;
    }

    @Override
    public void addUser(CorporateUser user) {

    }

    @Override
    public void resetPassword(CorporateUser user) {

    }

    @Override
    public void deleteUser(CorporateUser user) {

    }

    @Override
    public void enableUser(CorporateUser user) {

    }

    @Override
    public void disableUser(CorporateUser user) {

    }

    @Override
    public void lockUser(CorporateUser user, Date unlockat) {

    }

    @Override
    public void changePassword(CorporateUser user, String oldPassword, String newPassword) {

    }

    @Override
    public void generateAndSendPassword(CorporateUser user) {

    }
}
