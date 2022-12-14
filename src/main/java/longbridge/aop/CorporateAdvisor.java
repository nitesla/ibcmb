package longbridge.aop;

import longbridge.repositories.AccountRepo;
import longbridge.services.CorporateService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * Created by Fortune on 6/28/2017.
 */
@Aspect
public class CorporateAdvisor {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private AccountRepo accountRepo;

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



}
