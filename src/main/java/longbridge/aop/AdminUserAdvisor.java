package longbridge.aop;

import longbridge.dtos.VerificationDTO;
import longbridge.models.AdminUser;
import longbridge.services.VerificationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
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
public class AdminUserAdvisor {


    @Autowired
    EntityManager entityManager;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationService verificationService;


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
    public void postAdminUserCreation(JoinPoint p, AdminUser user)
    {
    	//general user creation
    	
    	
    	//activation
    	
    	
    }
    
    //this runs after execution
    @After("isVerification() && verified() && args(verificationDto)")
    public void postAdminUserCreation2(JoinPoint p, VerificationDTO verificationDto)
    {
    	if(verificationDto.getOperation().equals("UPDATE_ADMIN_STATUS")){
    		//do post update admin
    		
    	}
    	//general user creation
    	
    	
    	//activation
    	
    	
    }
    
   

}
