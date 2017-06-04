package longbridge.security.opsuser;

import longbridge.models.OperationsUser;
import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.UserType;
import longbridge.repositories.OperationsUserRepo;

import longbridge.security.CustomBruteForceService;
import longbridge.security.FailedLoginService;
import longbridge.security.IpAddressUtils;
import longbridge.security.userdetails.CustomUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("operationUserDetails")
public class OperationUserDetailsService implements UserDetailsService {

    private OperationsUserRepo operationsUserRepo;

    private CustomBruteForceService bruteForceService;
    private IpAddressUtils addressUtils;
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    private FailedLoginService failedLoginService;

     @Autowired
    public OperationUserDetailsService(OperationsUserRepo operationsUserRepo, CustomBruteForceService bruteForceService, IpAddressUtils addressUtils
     ,FailedLoginService failedLoginService
     ) {
        this.operationsUserRepo = operationsUserRepo;
        this.bruteForceService = bruteForceService;
        this.addressUtils = addressUtils;
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
        OperationsUser user = operationsUserRepo.findFirstByUserName(s);
        if (user!=null && failedLoginService.isBlocked(user)) throw new RuntimeException("user_blocked");
        try{



            if(user!=null && user.getUserType()== UserType.OPERATIONS
                    &&
                    user.getRole().getUserType()!=null
                    && user.getRole().getUserType().equals(UserType.OPERATIONS)
                    ) {
                return new CustomUserPrincipal(user);
            }
            throw new UsernameNotFoundException(s);
        }
        catch (Exception e){
            logger.error("An exception occurred {}",e.getMessage());
            throw new RuntimeException(e);
        }

    }



}
