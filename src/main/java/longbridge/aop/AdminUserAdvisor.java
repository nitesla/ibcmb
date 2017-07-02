package longbridge.aop;

import longbridge.dtos.VerificationDTO;
import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
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
public class AdminUserAdvisor {


    @Autowired
    EntityManager entityManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationService verificationService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminUserRepo adminUserRepo;

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

    @Pointcut("withincode(* verify(..)) && args(verificationDto)")
    public void isVerify(VerificationDTO verificationDto) {
    }


    @Pointcut("call(* merge(..))")
    public void isMerging() {
    }


    @Pointcut("execution(public * verify(..)) && args(verificationDto)")
    public void verified(VerificationDTO verificationDto) {
    }



    //this is after merge of verification
    @After("isVerification() && isMerging() && isVerify(verificationDto) && args(user)")
    public void postAdminUserCreation(JoinPoint p, AdminUser user,VerificationDTO verificationDto) {

        logger.info("Executing ADD_ADMIN_USER operation");
        adminUserService.createUserOnEntrust(user);
        logger.info("Created User on entrust");
//        user.setEntrustId(user.getUserName());
//        entityManager.merge(user);
        logger.info("After Executing first Post Admin Advice");
    }
    
   // this runs after execution
    @After("isVerification() && verified(verificationDto)")
    public void postAdminUserCreation2(JoinPoint p, VerificationDTO verificationDto)
    {

        logger.info("Entering Second Post Admin Advice...");
        logger.info("Verification dto {}",verificationDto);
        logger.info("VerificationRepo {}",verificationRepo);


        if(verificationDto.getOperation().equals("UPDATE_ADMIN_STATUS")){
            AdminUser user = adminUserRepo.findOne(verificationDto.getEntityId());
    	    String fullName = user.getFirstName()+" "+user.getLastName();
            String password = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveAdminPassword(user);
            adminUserRepo.save(user);
            adminUserService.sendPostActivateMessage(user, fullName,user.getUserName(),password);
    	}




    	//general user creation

    	
    	//activation
    	
    	
    }
    
   

}
