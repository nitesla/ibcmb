package longbridge.aop;

import com.querydsl.core.types.Template;
import longbridge.dtos.VerificationDTO;
import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.models.RetailUser;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.*;
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
public class RetailUserAdvisor {


    @Autowired
    EntityManager entityManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationService verificationService;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private RetailUserRepo retailUserRepo;

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





    // this runs after execution
    @After("isVerification() && verified() && args(verificationDto)")
    public void postRetailUserCreation(JoinPoint p, VerificationDTO verificationDto)
    {

        Verification verification  = verificationRepo.findOne(verificationDto.getId());
        if(verification.getOperation().equals("UPDATE_RETAIL_STATUS")){

            RetailUser user = retailUserRepo.findOne(verification.getEntityId());
            String fullName = user.getFirstName()+" "+user.getLastName();
            String password = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveRetailPassword(user);
            retailUserRepo.save(user);
            retailUserService.sendPostActivateMessage(user, fullName,user.getUserName(),password);
        }

    }



}
