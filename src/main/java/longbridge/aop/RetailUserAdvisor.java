package longbridge.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.VerificationDTO;
import longbridge.models.RetailUser;
import longbridge.models.Verification;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.PasswordPolicyService;
import longbridge.services.RetailUserService;
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
public class RetailUserAdvisor {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private RetailUserRepo retailUserRepo;

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
    public void postRetailUserActivation(JoinPoint p, VerificationDTO verificationDto) throws IOException {

        Verification verification  = verificationRepo.findById(verificationDto.getId()).get();
        if(verification.getOperation().equals("UPDATE_RETAIL_STATUS")){

            logger.info("Executing post UPDATE_RETAIL_STATUS operation");

            RetailUser user = retailUserRepo.findById(verification.getEntityId()).get();
            entityManager.detach(user);
            ObjectMapper objectMapper = new ObjectMapper();
            RetailUser retailUser = objectMapper.readValue(verification.getOriginalObject(),RetailUser.class);
            if("A".equals(retailUser.getStatus())){
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveRetailPassword(user);
                retailUserRepo.save(user);
                logger.info("Retail user {} status changed to {}",retailUser.getUserName(),retailUser.getStatus());
                retailUserService.sendActivationCredentials(retailUser, password);
            }
            else {
                retailUserRepo.save(user);
            }
        }

    }



}
