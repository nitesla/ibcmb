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


    public boolean removalListener(User user) {
        boolean ok = false;

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
                    ok = true;

                }


            } catch (Exception e) {
                logger.trace("Exception occurred   {}", e);
                e.printStackTrace();


            }

        }


        return ok;

    }
    public void loginSucceeded(final User key) {
        unLockUser(key);

    }

    public void loginFailed(final User key) {
        int attempts = 0;
        try {
            attempts = key.getNoOfLoginAttempts();

        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        ++attempts;


        key.setNoOfLoginAttempts(attempts);
        if (getMaxAttempt()!=0 && key.getNoOfLoginAttempts() >= getMaxAttempt()) {
            key.setStatus("L");
            key.setLockedUntilDate( new DateTime().plusMinutes(getExpiryTime()).toDate());
        }

        updateFailedLogin(key);


    }

    public boolean isBlocked(final User key) {
        try {
            //  return attemptsCache.get(key) >= getMaxAttempt();

            boolean ok = key.getStatus().equalsIgnoreCase("L");
            if (ok && !removalListener(key))


                return
                        key.getStatus().equalsIgnoreCase("L");


            // return key.getStatus()>= getMaxAttempt();

        } catch (final Exception e) {
            e.printStackTrace();
            return false;
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
            logger.error("Exception occurred {}", e.getMessage());
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

            logger.error("EXCEPTION OCCURRED {}", e.getMessage());
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

            logger.error("EXCEPTION OCCURRED {}", e.getMessage());
            return expiryTime;
        }
        return expiryTime;
    }


}
