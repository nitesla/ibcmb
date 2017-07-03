package longbridge.aop;

import com.querydsl.core.types.Template;
import longbridge.dtos.VerificationDTO;
import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.VerificationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import sun.rmi.runtime.Log;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created by Fortune on 6/28/2017.
 */
@Aspect
public class OpsUserAdvisor {


    @Autowired
    EntityManager entityManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationService verificationService;

    @Autowired
    private OperationsUserService operationsUserService;

    @Autowired
    private OperationsUserRepo operationsUserRepo;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    VerificationRepo verificationRepo;

    Logger logger = LoggerFactory.getLogger(this.getClass());


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

        logger.info("Executing ADD_OPS_USER operation");
        operationsUserService.createUserOnEntrust(user);
        logger.info("Created User on entrust");
//        user.setEntrustId(user.getUserName());
//        entityManager.merge(user);
        logger.info("After Executing first Post Ops Advice");
    }

    // this runs after execution
    @After("isVerification() && verified() && args(verificationDto)")
    public void postAdminUserCreation2(JoinPoint p, VerificationDTO verificationDto)
    {

        logger.info("Entering Second Post Ops Advice...");
        logger.info("Verification dto {}",verificationDto);
        logger.info("VerificationRepo {}",verificationRepo);

        Verification verification  = verificationRepo.findOne(verificationDto.getId());
        if(verification.getOperation().equals("UPDATE_OPS_STATUS")){

            OperationsUser user = operationsUserRepo.findOne(verification.getEntityId());
            String fullName = user.getFirstName()+" "+user.getLastName();
            String password = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveOpsPassword(user);
            operationsUserRepo.save(user);
            operationsUserService.sendPostActivateMessage(user, fullName,user.getUserName(),password);
        }




        //general user creation


        //activation


    }



}
