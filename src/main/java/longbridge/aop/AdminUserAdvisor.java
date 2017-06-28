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
    private VerificationRepo verificationRepo;

    @Autowired
    MakerCheckerService makerCheckerService;

    @Autowired
    VerificationService verificationService;

    @Autowired
    EntityManager entityManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());



    @Pointcut("within( longbridge.services..*)")
    public void inServiceLayer() {
    }

    @Pointcut("@withincode(longbridge.utils.Verifiable)")
    public void isInVerifiable() {
    }

    @Pointcut("withincode(@longbridge.utils.Verifiable * *(..)) &&  @withincode(verifier)")
    public void isInVerifiable2(Verifiable verifier) {
    }



    @Pointcut("call(* save(..))")
    public void isSaving() {
    }

    @Pointcut("call(* delete(..))")
    public void isDeleting() {
    }

    @Pointcut("execution(* save(..))")
    public void isSaving2() {
    }

    @After("isSaving2() && args(user))")
    public void temp(JoinPoint p, AdminUser user) {

    }

}
