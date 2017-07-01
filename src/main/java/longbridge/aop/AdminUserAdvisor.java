package longbridge.aop;

import longbridge.models.AdminUser;
import longbridge.repositories.VerificationRepo;
import longbridge.services.MakerCheckerService;
import longbridge.services.VerificationService;
import longbridge.utils.Verifiable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * Created by Fortune on 6/28/2017.
 */
@Aspect
public class AdminUserAdvisor {


    @Autowired
    EntityManager entityManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Pointcut("within( longbridge.services.implementations.VerificationServiceImpl)")
    public void isVerification() {
    }

    @Pointcut("withincode(* verify(..))")
    public void isVerify() {
    }


    @Pointcut("call(* merge(..))")
    public void isMerging() {
    }


    @After("isVerification() && isMerging() && isVerify() && args(user)")
    public void postAdminUserCreation(JoinPoint p, AdminUser user)
    {

    	
    }

}
