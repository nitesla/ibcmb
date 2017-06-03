package longbridge.security;

import com.google.common.cache.*;
import longbridge.dtos.SettingDTO;
import longbridge.models.*;
import longbridge.repositories.*;


import longbridge.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    RemovalListener<User, Integer> removalListener = removal -> {

        if (removal.wasEvicted()) {
            User user = removal.getKey();
            logger.trace("update user to no attempts login");
            user.setNoOfLoginAttempts(0);
            updateFailedLogin(user);
        }


    };
    private int MAX_ATTEMPT = 2;
    private int expiryTime = 5;
    private LoadingCache<User, Integer> attemptsCache;

    //

    public FailedLoginService() {
        super();

        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(getExpiryTime(), TimeUnit.MINUTES)
                .removalListener(removalListener)
                .build(new CacheLoader<User, Integer>() {
                    @Override
                    public Integer load(final User key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(final User key) {
        key.setNoOfLoginAttempts(0);
        updateFailedLogin(key);
        attemptsCache.invalidate(key);

    }

    public void loginFailed(final User key) {
        int attempts = 0;
        try {
            // attempts = key.getNoOfLoginAttempts();
            attempts = attemptsCache.get(key);

        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        attempts++;
        int noOfAttempts = 0;
        try {
            noOfAttempts = key.getNoOfLoginAttempts();
        } catch (Exception e) {

        }

        key.setNoOfLoginAttempts(noOfAttempts + attempts);
        updateFailedLogin(key);

        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final User key) {
        try {
            //  return attemptsCache.get(key) >= getMaxAttempt();

            return attemptsCache.get(key) >= getMaxAttempt();

        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateFailedLogin(User user) {
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

                    retailUserRepo.save((RetailUser) user);
                    break;
                }

            }
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
