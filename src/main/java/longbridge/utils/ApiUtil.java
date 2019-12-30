package longbridge.utils;
import longbridge.apiLayer.models.ApiResponse;
import longbridge.apiLayer.models.ApiUser;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.CustomerInternetBankingPassWordEncoder;
import longbridge.security.SessionUtils;
import longbridge.security.api.JwtTokenUtil;
import longbridge.security.api.TokenAuthenticationService;
import longbridge.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
@Service
public class ApiUtil {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    MailService mailService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    @Qualifier("apiUserDetails")
    private UserDetailsService userDetailsService;
    @Autowired
    CorporateUserRepo corporateUserRepo;
    @Autowired
    @Qualifier("bCryptPasswordEncoder")
    CustomerInternetBankingPassWordEncoder bCryptPasswordEncoder;
    @Value("${ibanking.mobile.api}")
    private String mobileIP;
    @Autowired
    private SessionUtils sessionUtils;
    @Autowired
    RetailUserRepo retailUserRepo;
    private Locale locale = LocaleContextHolder.getLocale();
    Logger logger = LoggerFactory.getLogger(this.getClass());
    public Object validateUser(ApiUser passedUser) {
        Map<String, Object> responseMap = new HashMap();
        ApiResponse response =new ApiResponse("100", true,"Error occured ", responseMap);
        try {
            try {
                /*
                    Generating Token for user and this will be required for all request.
                 */
                logger.info("passduser {} ", passedUser.getUserName());
                final UserDetails userDetails = userDetailsService.loadUserByUsername(passedUser.getUserName());
                logger.info("userdetails {} ", userDetails);
                boolean pass_check = bCryptPasswordEncoder.matches(passedUser.getPassWord() , userDetails.getPassword());
                if(!pass_check){
                    throw  new InternetBankingException("wrong password");
                }
                final String token = jwtTokenUtil.generateToken(passedUser.getUserName());
                responseMap.put("token", token);
//                responseMap.put("firstName",)
//                logger.info("APi login response {} ", response);
                if (userDetails !=null) {
                    RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(userDetails.getUsername());
                    if (user != null) {
                        responseMap.put("firstName", user.getFirstName());
                        responseMap.put("lastName", user.getLastName());
                        responseMap.put("email", user.getEmail());
                        responseMap.put("phoneNumber", user.getPhoneNumber());
                        response = new ApiResponse("00",false, "Login successful", responseMap);
                        logger.info("APi login response {} ", response);
                        user.setLastLoginDate(new Date());
                        user.setNoOfLoginAttempts(0);
                        user.setLockedUntilDate(null);
                        retailUserRepo.save(user);
                        user.setEmailTemplate("mail/loginMobile.html");
                        logger.info("TEMPLATE {}",user.getEmailTemplate());

                        sessionUtils.sendAlert(user);
                    } else {
                        CorporateUser corpuser = corporateUserRepo.findFirstByUserNameIgnoreCase(userDetails.getUsername());
                        logger.info("corporate user {} ", corpuser);
                        if (corpuser != null) {
                            responseMap.put("firstName", corpuser.getFirstName());
                            responseMap.put("lastName", corpuser.getLastName());
                            responseMap.put("email", corpuser.getEmail());
                            responseMap.put("phoneNumber", corpuser.getPhoneNumber());
                            response = new ApiResponse("00",false, "Login successful", responseMap);
                            logger.info("APi login response {} ", response);
                            corpuser.setLastLoginDate(new Date());
                            corporateUserRepo.save(corpuser);
                            corpuser.setEmailTemplate("mail/loginMobile.html");
                            sessionUtils.sendAlert(corpuser);
                        }
                    }
                }
                return response;
            } catch (UsernameNotFoundException | InternetBankingException e) {
                e.printStackTrace();
                response = new ApiResponse("99", true,  "Bad credentials", responseMap);
                return response;
            }
        }catch (Exception e){
//            ApiResponse response = new ApiResponse("100", true,"Error occured ", responseMap);
            return response;
        }
    }
    public Object validateUserUsingEncryptedPass(ApiUser passedUser) {
        Map<String, Object> responseMap = new HashMap();
        ApiResponse response =new ApiResponse("100", true,"Error occured ", responseMap);
        try {
            try {
                /*
                    Generating Token for user and this will be required for all request.
                 */
                logger.info("passduser {} ", passedUser.getUserName());
                final UserDetails userDetails = userDetailsService.loadUserByUsername(passedUser.getUserName());
                logger.info("APi userdetails {} ", passedUser.getPassWord());
//                if (userDetails !=null) {
                RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(passedUser.getUserName());
                if (user != null) {
                    logger.info("user password {} ", user.getPassword());
                    boolean encryptedPass = user.getPassword().equalsIgnoreCase(passedUser.getPassWord());
                    logger.info("encrypted state {} ", encryptedPass);
                    if(!encryptedPass){
                        throw  new InternetBankingException("wrong password");
                    }
                    responseMap.put("firstName", user.getFirstName());
                    responseMap.put("lastName", user.getLastName());
                    responseMap.put("email", user.getEmail());
                    responseMap.put("phoneNumber", user.getPhoneNumber());
                    response = new ApiResponse("00",false, "Login successful", responseMap);
                    logger.info("APi login response {} ", response);
                    final String token = jwtTokenUtil.generateToken(passedUser.getUserName());
                    responseMap.put("token", token);
                    user.setLastLoginDate(new Date());
                    user.setNoOfLoginAttempts(0);
                    user.setLockedUntilDate(null);
                    retailUserRepo.save(user);
                    user.setEmailTemplate("mail/loginMobile.html");
                    logger.info("TEMPLATE {}",user.getEmailTemplate());
                    sessionUtils.sendAlert(user);
                } else {
                    CorporateUser corpuser = corporateUserRepo.findFirstByUserNameIgnoreCase(userDetails.getUsername());
                    logger.info("corporate user {} ", corpuser);
                    if (corpuser != null) {
                        boolean encryptedPass = corpuser.getPassword().equalsIgnoreCase(passedUser.getPassWord());
                        logger.info("encrypted state {} ", encryptedPass);
                        if(!encryptedPass){
                            throw  new InternetBankingException("wrong password");
                        }
                        responseMap.put("firstName", corpuser.getFirstName());
                        responseMap.put("lastName", corpuser.getLastName());
                        responseMap.put("email", corpuser.getEmail());
                        responseMap.put("phoneNumber", corpuser.getPhoneNumber());
                        response = new ApiResponse("00",false, "Login successful", responseMap);
                        logger.info("APi login response {} ", response);
                        final String token = jwtTokenUtil.generateToken(passedUser.getUserName());
                        responseMap.put("token", token);
                        corpuser.setLastLoginDate(new Date());
                        corporateUserRepo.save(corpuser);
                        corpuser.setEmailTemplate("mail/loginMobile.html");
                        logger.info("TEMPLATE {}",corpuser.getEmailTemplate());
                        sessionUtils.sendAlert(user);
                    }
                }
//                }
                return response;
            } catch (UsernameNotFoundException | InternetBankingException e) {
                e.printStackTrace();
                response = new ApiResponse("99", true,  "Bad credentials", responseMap);
                return response;
            }
        }catch (Exception e){
//            ApiResponse response = new ApiResponse("100", true,"Error occured ", responseMap);
            return response;
        }
    }
}
