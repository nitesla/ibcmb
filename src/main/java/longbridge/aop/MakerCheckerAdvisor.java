package longbridge.aop;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.AbstractEntity;
import longbridge.models.User;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.MakerCheckerService;
import longbridge.utils.PrettySerializer;
import longbridge.utils.Verifiable;
import longbridge.utils.VerificationStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.util.Date;

@Aspect
public class MakerCheckerAdvisor {

    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private MakerCheckerService makerCheckerService;

    @Autowired
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("this(org.springframework.data.repository.Repository)")
    public void inRepositoryLayer() {
    }

    @Pointcut("within( longbridge.services..*)")
    public void inServiceLayer() {
    }

    @Pointcut("@withincode(longbridge.utils.Verifiable)")
    public void isInVerifiable() {
    }

    @Pointcut("withincode(@longbridge.utils.Verifiable * *(..)) &&  @withincode(verifier)")
    public void isInVerifiable2(Verifiable verifier) {

    }

    @Pointcut("execution(@longbridge.utils.Verifiable * *(..))")
    public void isVerifiable() {

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

    @Around(value = "isSaving() && inServiceLayer() && isInVerifiable2(verifier) && args(entity)", argNames = "pjp,entity,verifier")
    public Object proceed1(ProceedingJoinPoint pjp, AbstractEntity entity, Verifiable verifier) throws Throwable {
        log.debug("Executing Maker Checker for saving entity");
        entityManager.detach(entity);
        log.debug("In operation [ " + verifier.operation() + "] ...{" + verifier.description() + "}");

        log.debug("JB Around: " + pjp);

        if (!makerCheckerService.isEnabled(verifier.operation())) {
            log.info("Maker checker is disabled for operation: "+verifier.operation());

            if (entity.getId() != null) {
                Long id = entity.getId();
                String entityName = entity.getClass().getSimpleName();

                Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    log.info("Found entity with pending verification");
                    throw new InternetBankingException(entityName + " has changes pending for verification. Approve or " +
                            "decline the changes before making another one.");
                }
            }

            pjp.proceed();
            return entity;
        }

        log.info("Maker checker is enabled for operation: "+verifier.operation());


        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User doneBy = principal.getUser();
        String entityName = entity.getClass().getSimpleName();

        Verification verification = new Verification();
        verification.setEntityName(entityName);
        verification.setInitiatedOn(new Date());
        verification.setInitiatedBy(doneBy.getUserName());
        verification.setUserType(doneBy.getUserType());
        verification.setOperation(verifier.operation());
        verification.setDescription(verifier.description());
        ObjectMapper mapper = new ObjectMapper();
        verification.setOriginalObject(mapper.writeValueAsString(entity));


        ObjectMapper prettyMapper = new ObjectMapper();

        if (entity instanceof PrettySerializer) {
            JsonSerializer<Object> serializer = ((PrettySerializer) (entity)).getSerializer();

            SimpleModule module = new SimpleModule();
            module.addSerializer(entity.getClass(), serializer);
            prettyMapper.registerModule(module);
            log.debug("Registering Pretty serializer for " + entity.getClass().getName());
        }

        if (entity.getId() != null) {
            Long id = entity.getId();
            AbstractEntity originalEntity = entityManager.find(entity.getClass(), id);

            Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                    id, VerificationStatus.PENDING);
            if (pendingVerification != null) {
                // found pending verification
                throw new InternetBankingException(entityName + " has pending verification");
            }

            verification.setBeforeObject(prettyMapper.writeValueAsString(originalEntity));
            verification.setEntityId(entity.getId());
        }
        verification.setAfterObject(prettyMapper.writeValueAsString(entity));
        verification.setStatus(VerificationStatus.PENDING);
        verificationRepo.save(verification);

        log.info(entityName + " has been saved for verification");

        throw new VerificationInterruptedException(verifier.description() + " has gone for verification");

    }


    @Around(value = "isDeleting() && inServiceLayer() && isInVerifiable2(verifier) && args(entity)", argNames = "pjp,entity,verifier")
    public Object proceed2(ProceedingJoinPoint pjp, AbstractEntity entity, Verifiable verifier) throws Throwable {
        log.debug("Executing Maker Checker for deleting entity");
        entityManager.detach(entity);
        log.debug("In operation [ " + verifier.operation() + "] ...{" + verifier.description() + "}");

        log.debug("JB Around: " + pjp);

        if (!makerCheckerService.isEnabled(verifier.operation())) {
            log.info("Maker checker is disabled for the operation: "+verifier.operation());
            pjp.proceed();
            return entity;
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User doneBy = principal.getUser();
        String entityName = entity.getClass().getSimpleName();

        Verification verification = new Verification();
        verification.setEntityName(entityName);
        verification.setInitiatedOn(new Date());
        verification.setInitiatedBy(doneBy.getUserName());
        verification.setUserType(doneBy.getUserType());
        verification.setOperation(verifier.operation());
        verification.setDescription(verifier.description());
        ObjectMapper mapper = new ObjectMapper();
        entity.setDelFlag("Y");
        verification.setOriginalObject(mapper.writeValueAsString(entity));

        ObjectMapper prettyMapper = new ObjectMapper();

        if (entity instanceof PrettySerializer) {
            JsonSerializer<Object> serializer = ((PrettySerializer) (entity)).getSerializer();

            SimpleModule module = new SimpleModule();
            module.addSerializer(entity.getClass(), serializer);
            prettyMapper.registerModule(module);
            log.debug("Registering Pretty serializer for " + entity.getClass().getName());
        }

        Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                entity.getId(), VerificationStatus.PENDING);
        if (pendingVerification != null) {
            // found pending verification
            throw new InternetBankingException(entityName + " has pending verification");
        }

        verification.setBeforeObject(prettyMapper.writeValueAsString(entity));
        verification.setEntityId(entity.getId());
        verification.setStatus(VerificationStatus.PENDING);
        verificationRepo.save(verification);
        log.info(entityName + " has been saved for verification");
        throw new VerificationInterruptedException(verifier.description() + " has gone for verification");
    }


}
