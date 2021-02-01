package longbridge.security;

import longbridge.dtos.SettingDTO;
import longbridge.models.*;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.SettingsService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FailedLoginService {

    @Autowired
    private SettingsService configService;
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private AdminUserRepo adminUserRepo;
    @Autowired
    private OperationsUserRepo operationsUserRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    public FailedLoginService() {
    }


    public boolean unLockUser(User user) {
        boolean unlocked = false;

        try {
            user.setNoOfLoginAttempts(0);
            user.setStatus("A");
            user.setLockedUntilDate(null);
            updateFailedLogin(user);
            unlocked = true;

            logger.info("User {} has been unlocked",user.getUserName());


        } catch (Exception e) {
            logger.error("Error occurred unlocking user {}",user.getUserName(),e);
        }
        return unlocked;


    }


    public boolean isLockOutDurationExpired(User user) {
        boolean expired = false;

             if (user.getLockedUntilDate() == null) {
                    unLockUser(user);
                    return true;
                }

                DateTime lockedUntilDate = new DateTime(user.getLockedUntilDate());
                if (DateTime.now().isAfter(lockedUntilDate)) {
                    logger.info("User {} locked out period has expired", user.getUserName());
                    unLockUser(user);
                    expired = true;

                }

        return expired;

    }

    public void loginSucceeded(final User user) {
        unLockUser(user);

    }

    public void loginFailed(final User user) {
        int attempts = 0;

        attempts = user.getNoOfLoginAttempts();

        ++attempts;


        user.setNoOfLoginAttempts(attempts);
        if (getMaxAttempt() != 0 && user.getNoOfLoginAttempts() >= getMaxAttempt()) {
            user.setStatus("L");
            user.setLockedUntilDate(new DateTime().plusMinutes(getExpiryTime()).toDate());
            logger.info("User {} has reached max failed logins and is now locked", user.getUserName());
        }

        updateFailedLogin(user);


    }

    public boolean isLocked(final User user) {
            boolean isLocked = user.getStatus().equalsIgnoreCase("L");
            return isLocked && !isLockOutDurationExpired(user);


    }

    @Transactional
    void updateFailedLogin(User user) {
        try {
            if (user != null && user.getUserType() != null) {
                UserType type = user.getUserType();
                switch (type) {
                    case ADMIN: {

                        adminUserRepo.save((AdminUser) user);
                        break;
                    }

                    case RETAIL: {

                        retailUserRepo.save((RetailUser) user);
                        break;
                    }
                    case OPERATIONS: {

                        operationsUserRepo.save((OperationsUser) user);
                        break;
                    }
                    case CORPORATE: {

                        corporateUserRepo.save((CorporateUser) user);
                        break;
                    }

                }
            }
        } catch (Exception e) {
            logger.error("Error occurred updating failed login ", e);
        }


    }

    private int getMaxAttempt() {
        int MAX_ATTEMPT = 3;
        try {
            SettingDTO settingDTO = configService.getSettingByName("MAX_FAILED_LOGIN_TRIALS");
            if (settingDTO.isEnabled()) {
                return Integer.parseInt(settingDTO.getValue());

            }
        } catch (Exception e) {

            logger.error("EXCEPTION OCCURRED ", e);
            return MAX_ATTEMPT;
        }
        return MAX_ATTEMPT;
    }

    private int getExpiryTime() {
        int expiryTime = 2;
        try {
            SettingDTO settingDTO = configService.getSettingByName("LOCK_OUT_DURATION");
            if (settingDTO.isEnabled()) {
                return Integer.parseInt(settingDTO.getValue());

            }
        } catch (Exception e) {

            logger.error("EXCEPTION OCCURRED ", e);
            return expiryTime;
        }
        return expiryTime;
    }


}
