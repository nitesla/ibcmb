package longbridge.aop;

import java.util.Date;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.exception.InternetBankingException;
import longbridge.models.AbstractEntity;
import longbridge.models.User;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.MakerCheckerService;
import longbridge.services.VerificationService;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
public class MakerCheckerAdvisor {

	@Autowired
	private VerificationRepo verificationRepo;

	@Autowired
	private MakerCheckerService makerCheckerService;

	@Autowired
	private VerificationService verificationService;

	@Autowired
	EntityManager entityManager;

	private Logger log = LoggerFactory.getLogger(this.getClass());

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

	@Around("isSaving() && inServiceLayer() && isInVerifiable2(verifier) && args(entity)")
	public Object proceed3(ProceedingJoinPoint pjp, AbstractEntity entity, Verifiable verifier) throws Throwable {
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		log.info("Again testing that this save works");
		entityManager.detach(entity);
		log.info("In operation [ " + verifier.operation() + "] ...{" + verifier.description() + "}");

		// pjp.getSignature().getDeclaringType().getAnnotations()
//		log.info(entity.toString());
		log.info("JB Around: " + pjp);

		if (!makerCheckerService.isEnabled(verifier.operation())) {
			log.info("Maker checker is disabled for operation");
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

		log.info(entityName+ "has been saved for verification");

		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		return entity;
	}



}
