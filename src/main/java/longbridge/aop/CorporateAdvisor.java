package longbridge.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.VerificationDTO;
import longbridge.models.AdminUser;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.Verification;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.CorporateService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.VerificationService;
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
