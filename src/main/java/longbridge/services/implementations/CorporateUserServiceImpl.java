package longbridge.services.implementations;

import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.EmailDetail;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateCustomerRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.CorporateUserService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

/**
 * Created by Fortune on 4/4/2017.
 */
@Service
public class CorporateUserServiceImpl implements CorporateUserService {

    private CorporateUserRepo corporateUserRepo;

    private CorpLimitRepo corpLimitRepo;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityService securityService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorporateUserServiceImpl(CorporateUserRepo corporateUserRepo, BCryptPasswordEncoder passwordEncoder, SecurityService securityService) {
        this.corporateUserRepo = corporateUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public CorporateUser getUser(Long id) {
        return corporateUserRepo.findOne(id);
    }

    @Override
    public Iterable<CorporateUser> getUsers(Corporate corporate) {
        return corporate.getUsers();
    }

    @Override
    public Iterable<CorporateUser> getUsers() {
        return corporateUserRepo.findAll();
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
        corporateUserRepo.save(user);
    }

    @Override
    public void resetPassword(CorporateUser user) {

    }

    @Override
    public void deleteUser(Long userId) {
        CorporateUser repo = corporateUserRepo.findOne(userId);
        repo.setDelFlag("Y");
        corporateUserRepo.save(repo);
    }

    @Override
    public void enableUser(CorporateUser user) {
        user.setEnabled(true);
        corporateUserRepo.save(user);
    }

    @Override
    public void disableUser(CorporateUser user) {
        user.setEnabled(false);
        corporateUserRepo.save(user);
    }

    @Override
    public void lockUser(CorporateUser user, Date unlockat) {
        //todo
    }

    @Override
    public void changePassword(CorporateUser user, String oldPassword, String newPassword) {
        try {
            if (user == null) {
                logger.error("User does not exist");
                return;
            }

            if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(newPassword));
                corporateUserRepo.save(user);
                logger.info("USER {}'s password has been updated", user.getUserName());
                return;
            } else {
                logger.error("Invalid current password supplied for {}", user.getUserName());
                return;
            }
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
        }
    }

    @Override
    public void generateAndSendPassword(CorporateUser user) {
        String newPassword = securityService.generatePassword();
        try {
            if (user == null) {
                logger.error("User does not exist");
                return;
            }
            user.setPassword(this.passwordEncoder.encode(newPassword));
            corporateUserRepo.save(user);
            logger.info("USER {}'s password has been updated", user.getUserName());
            //todo send the email.. messagingService.sendEmail(EmailDetail);
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
        }
    }


}
