package longbridge.security.retailuser;


import longbridge.models.RetailUser;
import longbridge.models.UserType;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.CustomBruteForceService;
import longbridge.security.FailedLoginService;
import longbridge.security.IpAddressUtils;
import longbridge.security.SessionUtils;
import longbridge.security.userdetails.CustomUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("retailUserDetails")
public class RetailUserDetailsService implements UserDetailsService {

    private final RetailUserRepo retailUserRepo;
    private final CustomBruteForceService bruteForceService;
    private final IpAddressUtils addressUtils;
    private final FailedLoginService failedLoginService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public RetailUserDetailsService(RetailUserRepo retailUserRepo, CustomBruteForceService bruteForceService, IpAddressUtils addressUtils
            , FailedLoginService failedLoginService,SessionUtils sessionUtils) {
        this.retailUserRepo = retailUserRepo;
        this.bruteForceService = bruteForceService;
        this.addressUtils = addressUtils;
        this.failedLoginService = failedLoginService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final String ip = addressUtils.getClientIP();
        logger.trace("Retail User with IP Address {} trying to login", ip);
        //CHECK IF THE IP HAS BEEN BLOCKED BY THE CUSTOM BRUTE FORCE SERVICE
        if (bruteForceService.isBlocked(ip)) {
            logger.trace("IP -> {} has been blocked", ip);
            throw new RuntimeException("blocked");
        }
        RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(s);

        if (user != null && failedLoginService.isLocked(user)) {
            throw new RuntimeException("User " + user.getUserName() + " is locked");
        }

        try {
            if (user != null && user.getUserType() == UserType.RETAIL && user.getRole().getUserType().equals(UserType.RETAIL)) {
                CustomUserPrincipal userPrincipal = new CustomUserPrincipal(user);
                userPrincipal.setIpAddress(ip);
                logger.info("PRINCIPAL == {} ", userPrincipal.toString());
                logger.info("PRINCIPAL == {} ", userPrincipal.getUser());
                return userPrincipal;
            }
            throw new UsernameNotFoundException(s);
        } catch (Exception e) {
            logger.error("An exception occurred {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
