package longbridge.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.VerificationDTO;
import longbridge.models.OperationsUser;
import longbridge.models.Verification;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.OperationsUserService;
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
public class OpsUserAdvisor {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OperationsUserService operationsUserService;

    @Autowired
    private OperationsUserRepo operationsUserRepo;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationRepo verificationRepo;


    private Logger logger = LoggerFactory.getLogger(this.getClass());


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


    //this is after merge of verification
    @After("isVerification() && isMerging() && isVerify() && args(user)")
    public void postOpsUserCreation(JoinPoint p, OperationsUser user) {

        operationsUserService.createUserOnEntrustAndSendCredentials(user);
    }

    // this runs after execution
    @After("isVerification() && verified() && args(verificationDto)")
    public void postOpsUserStatusUpdate(JoinPoint p, VerificationDTO verificationDto) throws IOException {

        Verification verification = verificationRepo.findById(verificationDto.getId()).get();
        if (verification.getOperation().equals("UPDATE_OPS_STATUS")) {

            logger.info("Executing post UPDATE_OPS_STATUS operation");

            OperationsUser user = operationsUserRepo.findById(verification.getEntityId()).get();
            entityManager.detach(user);
            ObjectMapper objectMapper = new ObjectMapper();
            OperationsUser opsUser = objectMapper.readValue(verification.getOriginalObject(), OperationsUser.class);
            if ("A".equals(opsUser.getStatus())) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveOpsPassword(user);
                operationsUserRepo.save(user);
                logger.info("Ops user {} status changed to {}",opsUser.getUserName(),opsUser.getStatus());
                operationsUserService.sendActivationCredentials(opsUser, password);
            }
        }

    }

}
