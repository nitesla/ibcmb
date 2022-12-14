package longbridge.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.VerificationDTO;
import longbridge.models.CorporateUser;
import longbridge.models.Verification;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.CorporateUserService;
import longbridge.services.PasswordPolicyService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Fortune on 6/28/2017.
 */
@Aspect
public class CorporateUserAdvisor {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CorporateUserRepo corporateUserRepo;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationRepo verificationRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Pointcut("within( longbridge.services.implementations.VerificationServiceImpl)")
    public void isVerification() {
    }

    @Pointcut("withincode(* verify(..))")
    public void isVerify() {
    }


    @Pointcut("call(* merge(..))")
    public void isMerging() {
    }


    @Pointcut("execution(public * verify(..))")
    public void verified() {
    }





    // this runs after execution
    @After("isVerification() && verified() && args(verificationDto)")
    public void postCorporateUserActivation(JoinPoint p, VerificationDTO verificationDto) throws IOException {

        Verification verification  = verificationRepo.findById(verificationDto.getId()).get();
        if(verification.getOperation().equals("UPDATE_CORP_USER_STATUS")){

            logger.info("Executing post UPDATE_CORP_USER_STATUS operation");

            CorporateUser user = corporateUserRepo.getOne(verification.getEntityId());
            entityManager.detach(user);
            ObjectMapper objectMapper = new ObjectMapper();
            CorporateUser corpUser = objectMapper.readValue(verification.getOriginalObject(),CorporateUser.class);
            if("A".equals(corpUser.getStatus())){
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveCorporatePassword(user);
                corporateUserRepo.save(user);
                logger.info("Corporate user {} status changed to {}",corpUser.getUserName(),corpUser.getStatus());
                corporateUserService.sendActivationCredentials(corpUser, password);
            }
            else{
                corporateUserRepo.save(user);
            }
        }

        if(verification.getOperation().equals("ADD_CORPORATE_USER")) {
            ObjectMapper objectMapper = new ObjectMapper();
            CorporateUser corpUser = objectMapper.readValue(verification.getOriginalObject(),CorporateUser.class);
            corporateUserService.createUserOnEntrustAndSendCredentials(corpUser);
        }

        if(verification.getOperation().equals("UPDATE_CORPORATE_USER")) {
            ObjectMapper objectMapper = new ObjectMapper();
            CorporateUser corpUser = objectMapper.readValue(verification.getOriginalObject(),CorporateUser.class);
            corporateUserService.removeUserFromAuthorizerRole(corpUser);
        }

    }



}
