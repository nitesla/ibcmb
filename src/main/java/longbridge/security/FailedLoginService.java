package longbridge.security;

import longbridge.dtos.SettingDTO;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.services.ConfigurationService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FailedLoginService {

    @Autowired
    private ConfigurationService configService;
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private AdminUserRepo adminUserRepo;
    @Autowired
    private OperationsUserRepo operationsUserRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private CorporateRepo corporateRepo;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private int MAX_ATTEMPT = 3;


    private int expiryTime = 2;


    public FailedLoginService() {
    }
    //


    public boolean unLockUser(User user) {
        boolean ok = false;
        try {
            user.setNoOfLoginAttempts(0);
            user.setStatus("A");
            user.setLockedUntilDate(null);
            updateFailedLogin(user);
            ok = true;

        } catch (Exception e) {
            e.printStackTrace();


        }
        return ok;


    }


    public boolean isLockOutDurationExpired(User user) {
        boolean unlocked = false;

        if (user != null) {
            try {

                if (user.getLockedUntilDate() == null) {
                    unLockUser(user);
                    return true;
                }


                DateTime dateTime = new DateTime(user.getLockedUntilDate());
                Duration duration = new Duration(dateTime, DateTime.now());
                if (duration.getStandardMinutes() >= getExpiryTime()) {
                    logger.trace("update user to no attempts login");
                    unLockUser(user);
                    unlocked = true;

                }


            } catch (Exception e) {
                logger.trace("Exception occurred ", e);
                e.printStackTrace();


            }

        }


        return unlocked;

    }

    public void loginSucceeded(final User user) {
        unLockUser(user);

    }

    public void loginFailed(final User user) {
        int attempts = 0;
        try {
            attempts = user.getNoOfLoginAttempts();

        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        ++attempts;


        user.setNoOfLoginAttempts(attempts);
        if (getMaxAttempt() != 0 && user.getNoOfLoginAttempts() >= getMaxAttempt()) {
            user.setStatus("L");
            user.setLockedUntilDate(new DateTime().plusMinutes(getExpiryTime()).toDate());
        }

        updateFailedLogin(user);


    }

    public boolean isBlocked(final User user) {
        try {

            boolean isLocked = user.getStatus().equalsIgnoreCase("L");
            return isLocked && !isLockOutDurationExpired(user);

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Transactional
    private void updateFailedLogin(User user) {
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
            logger.error("Exception occurred ", e);
            e.printStackTrace();
        }


    }

    private int getMaxAttempt() {
        try {
            SettingDTO settingDTO = configService.getSettingByName("MAX_FAILED_LOGIN_TRIALS");
            if (settingDTO.isEnabled()) {
                int timeout = Integer.parseInt(settingDTO.getValue());

                return timeout;

            }
        } catch (Exception e) {

            logger.error("EXCEPTION OCCURRED ", e);
            return MAX_ATTEMPT;
        }
        return MAX_ATTEMPT;
    }

    private int getExpiryTime() {
        try {
            SettingDTO settingDTO = configService.getSettingByName("LOCK_OUT_DURATION");
            if (settingDTO.isEnabled()) {
                int timeout = Integer.parseInt(settingDTO.getValue());

                return timeout;

            }
        } catch (Exception e) {

            logger.error("EXCEPTION OCCURRED ", e);
            return expiryTime;
        }
        return expiryTime;
    }


}
