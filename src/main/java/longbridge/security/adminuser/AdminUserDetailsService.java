package longbridge.security.adminuser;

import longbridge.models.AdminUser;
import longbridge.models.UserType;
import longbridge.repositories.AdminUserRepo;
import longbridge.security.CustomBruteForceService;
import longbridge.security.FailedLoginService;
import longbridge.security.IpAddressUtils;
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

@Service("adminUserDetails")
@Transactional
public class AdminUserDetailsService implements UserDetailsService {

    private AdminUserRepo adminUserRepo;
    private CustomBruteForceService bruteForceService;
    private IpAddressUtils addressUtils;
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    private FailedLoginService failedLoginService;

    @Autowired
    public AdminUserDetailsService(AdminUserRepo adminUserRepo,CustomBruteForceService bruteForceService,IpAddressUtils addressUtils
    ,FailedLoginService failedLoginService
    ) {
        this.adminUserRepo = adminUserRepo;
       this.addressUtils=addressUtils;
        this.bruteForceService=bruteForceService;
        this.failedLoginService=failedLoginService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final String ip = addressUtils.getClientIP();
        logger.trace("User with IP Address {} trying to login",ip);
        //CHECK IF THE IP HAS BEEN BLOCKED BY THE CUSTOM BRUTE FORCE SERVICE
        if (bruteForceService.isBlocked(ip)) {
            logger.trace("IP -> {} has been blocked" ,ip);
            throw new RuntimeException("blocked");
        }
        AdminUser user= adminUserRepo.findFirstByUserNameIgnoreCase(s);
        if (user!=null  ) {
            if (failedLoginService.isBlocked(user)) throw new RuntimeException("user_blocked");
            try{

                if(user.getUserType()== UserType.ADMIN) {
                    if (user.getRole().getUserType()!=null ){
                        if (user.getRole().getUserType()!= UserType.ADMIN) throw new UsernameNotFoundException(s);
                    }
                    CustomUserPrincipal userPrincipal = new CustomUserPrincipal(user);
                    userPrincipal.setIpAddress(ip);
                    return userPrincipal;
                }
                throw new UsernameNotFoundException(s);
            }
            catch (Exception e){
                logger.error("An exception occurred {}",e.getMessage());
                throw new RuntimeException(e);
            }
        }

        throw new UsernameNotFoundException(s);
    }


}
