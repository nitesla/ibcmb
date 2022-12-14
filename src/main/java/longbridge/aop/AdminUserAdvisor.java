package longbridge.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.VerificationDTO;
import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
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
public class AdminUserAdvisor {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminUserRepo adminUserRepo;

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



    //this is after merge of verification
    @After("isVerification() && isMerging() && isVerify() && args(user)")
    public void postAdminUserCreation(JoinPoint p, AdminUser user) {

         adminUserService.createUserOnEntrustAndSendCredentials(user);

    }

   // this runs after execution
    @After("isVerification() && verified() && args(verificationDto)")
    public void postAdminUserStatusUpdate(JoinPoint p, VerificationDTO verificationDto) throws IOException {

      Verification verification  = verificationRepo.findById(verificationDto.getId()).get();

        if(verification.getOperation().equals("UPDATE_ADMIN_STATUS")){

            logger.info("Executing post UPDATE_ADMIN_STATUS operation");

            AdminUser user = adminUserRepo.findById(verification.getEntityId()).get();
            entityManager.detach(user);
            ObjectMapper objectMapper = new ObjectMapper();
            AdminUser adminUser = objectMapper.readValue(verification.getOriginalObject(),AdminUser.class);
            if("A".equals(adminUser.getStatus())){
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveAdminPassword(user);
                adminUserRepo.save(user);
                logger.info("Admin user {} status changed to {}",user.getUserName(),adminUser.getStatus());
                adminUserService.sendActivationCredentials(adminUser, password);

            }
    	}


    }



}
