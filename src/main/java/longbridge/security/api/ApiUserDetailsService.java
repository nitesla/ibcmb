package longbridge.security.api;

import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.models.UserType;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.RetailUserRepo;
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

@Service("apiUserDetails")
@Transactional
public class ApiUserDetailsService implements UserDetailsService{

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private CustomBruteForceService bruteForceService;
    @Autowired
    private IpAddressUtils addressUtils;
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private FailedLoginService failedLoginService;
    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private CorporateRepo corporateRepo;
    @Autowired
    private SessionUtils sessionUtils;



    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final String ip = addressUtils.getClientIP();
        logger.trace("User with IP Address {} trying to login",ip);
        logger.info("s {} ", s);

        RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(s);

logger.info("about checking user");
        if (user!=null  ) {

            if (failedLoginService.isLocked(user)) throw new RuntimeException("user_blocked");
            try{

                if(user.getUserType()== UserType.RETAIL) {

                    if (user.getRole().getUserType()!=null )
                    {
                        if (user.getRole().getUserType()!= UserType.RETAIL) throw new UsernameNotFoundException(s);
                    }
                    CustomUserPrincipal userPrincipal = new CustomUserPrincipal(user);
                    userPrincipal.setIpAddress(ip);
                    return userPrincipal;
                }
                throw new UsernameNotFoundException(s);
            }
            catch (Exception e){
                logger.error("An exception occurred {}",e);
                throw new RuntimeException(e);
            }
        }

        else {
            logger.info("check if corporate user ");
            String userName = "";
            String corpId = "";
            if (s != null) {
                try {
                    userName = s.split(":")[0];
                    corpId = s.split(":")[1];

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            CorporateUser corporateUser = corporateUserRepo.findFirstByUserName(userName.trim());

            if (corporateUser != null) {

                if (failedLoginService.isLocked(corporateUser)) throw new RuntimeException("user_blocked");
                try {
                    logger.info("corp id {}", corpId);
                    Corporate corporate = corporateRepo.findFirstByCorporateIdIgnoreCase(corpId);
                    logger.info("corporate {}", corporate);
                    if (corporate != null && corporateUser != null) {
                        if (!"A".equalsIgnoreCase(corporate.getStatus())) {
                            throw new DisabledException("User is disabled");
                        }
                        logger.info("corporate id {}", corporateUser.getCorporate().getCorporateId());


                        if ((corporateUser.getCorporate().getCorporateId().equalsIgnoreCase(corporate.getCorporateId())) && corporateUser.getUserType() == UserType.CORPORATE) {

                            CustomUserPrincipal userPrincipal = new CustomUserPrincipal(corporateUser);
                            userPrincipal.setIpAddress(ip);
                            return userPrincipal;
                        }
                    }
                    throw new UsernameNotFoundException(s);
                } catch (Exception e) {
                    logger.error("An exception occurred {}", e);
                    throw new RuntimeException(e);
                }
            }

        }
        throw new UsernameNotFoundException(s);

    }

}
