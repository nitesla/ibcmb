package longbridge.security.corpuser;


import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.security.CustomBruteForceService;
import longbridge.security.FailedLoginService;
import longbridge.security.IpAddressUtils;

import longbridge.security.userdetails.CustomUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ayoade_farooq@yahoo.com on 4/11/2017.
 */

@Service("corpUserDetails")
public class CorporateUserDetailsService implements UserDetailsService {

    private CorporateUserRepo corporateUserRepo;
    private CustomBruteForceService bruteForceService;
    private IpAddressUtils addressUtils;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CorporateRepo corporateRepo;
    private FailedLoginService failedLoginService;

    @Autowired
    public CorporateUserDetailsService(CorporateUserRepo corporateUserRepo, CustomBruteForceService bruteForceService, IpAddressUtils addressUtils, CorporateRepo corporateRepo
    ,FailedLoginService failedLoginService
    ) {
        this.corporateUserRepo = corporateUserRepo;
        this.bruteForceService = bruteForceService;
        this.addressUtils = addressUtils;
        this.corporateRepo = corporateRepo;
        this.failedLoginService=failedLoginService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final String ip = addressUtils.getClientIP();
        logger.trace("User with IP Address {} trying to login", ip);
        //CHECK IF THE IP HAS BEEN BLOCKED BY THE CUSTOM BRUTE FORCE SERVICE
        if (bruteForceService.isBlocked(ip)) {
            logger.trace("IP -> {} has been blocked", ip);
            throw new RuntimeException("blocked");
        }
        String userName = "";
        String corpId = "";
        if (s!=null){
           try{
               userName = s.split(":")[0];
               corpId = s.split(":")[1];

           }catch (Exception e){
               e.printStackTrace();

           }
        }


        CorporateUser user = corporateUserRepo.findFirstByUserNameIgnoreCaseAndCorporate_CustomerIdIgnoreCase(userName,corpId);
        if (user!=null){

            if (failedLoginService.isBlocked(user)) throw new RuntimeException("user_blocked");
            try {
                Corporate corporate = corporateRepo.findFirstByCustomerId(corpId);
                if (corporate != null && user != null) {
                    if(!corporate.getStatus().equalsIgnoreCase("A")){
                        throw  new DisabledException("User is disabled");
                    }


                    if ((user.getCorporate().getCustomerId().equalsIgnoreCase(corporate.getCustomerId())) && user.getUserType() == UserType.CORPORATE) {
                        return new CustomUserPrincipal(user);
                    }
                }
                throw new UsernameNotFoundException(s);
            } catch (Exception e) {
                logger.error("An exception occurred {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        throw new UsernameNotFoundException(s);
    }


}
