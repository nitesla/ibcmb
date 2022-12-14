package longbridge.security.corpuser;


import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.security.CustomBruteForceService;
import longbridge.security.FailedLoginService;
import longbridge.security.IpAddressUtils;
import longbridge.security.SessionUtils;
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

    private final CorporateUserRepo corporateUserRepo;
    private final CustomBruteForceService bruteForceService;
    private final IpAddressUtils addressUtils;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CorporateRepo corporateRepo;
    private final FailedLoginService failedLoginService;

    @Autowired
    public CorporateUserDetailsService(CorporateUserRepo corporateUserRepo, CustomBruteForceService bruteForceService, IpAddressUtils addressUtils, CorporateRepo corporateRepo
    ,FailedLoginService failedLoginService,SessionUtils sessionUtils
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
        logger.trace("Corporate User with IP Address {} trying to login", ip);
        //CHECK IF THE IP HAS BEEN BLOCKED BY THE CUSTOM BRUTE FORCE SERVICE
        if (bruteForceService.isBlocked(ip)) {
            logger.trace("IP -> {} has been blocked", ip);
            throw new RuntimeException("blocked");
        }
        logger.info("login for {}",s);
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


        CorporateUser user = corporateUserRepo.findFirstByUserNameIgnoreCaseAndCorporate_CorporateIdIgnoreCase(userName,corpId);
        if (user!=null){

            if (failedLoginService.isLocked(user)) throw new RuntimeException(user.getUserName()+" is blocked");
            try {
                Corporate corporate = corporateRepo.findFirstByCorporateIdIgnoreCase(corpId);
                if (corporate != null && user != null) {
                    if(!"A".equalsIgnoreCase(corporate.getStatus())){
                        throw  new DisabledException("User is disabled");
                    }


                    if ((user.getCorporate().getCorporateId().equalsIgnoreCase(corporate.getCorporateId())) && user.getUserType() == UserType.CORPORATE) {

                        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(user);
                        userPrincipal.setIpAddress(ip);
                        return userPrincipal;
                    }
                }
                throw new UsernameNotFoundException(s);
            } catch (Exception e) {
                logger.error("An exception occurred at login", e);
                throw new RuntimeException(e);
            }
        }
        throw new UsernameNotFoundException(s);
    }



}
