package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.models.AbstractEntity;
import longbridge.models.SerializableEntity;
import longbridge.models.Verification;
import longbridge.models.Verification.VerificationStatus;
import longbridge.repositories.VerificationRepo;
import longbridge.services.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

@Service
@Transactional
public class VerificationServiceImpl implements VerificationService {

	private static final String PACKAGE_NAME = "longbridge.models.";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private VerificationRepo verificationRepo;
	@Autowired
    private EntityManager eman;

	@Override
	public void decline(Verification verification, String declineReason) {
//		verification.setDeclinedBy(decliner);
        verification.setDeclinedOn(new Date());
        verification.setDeclineReason(declineReason);
        verificationRepo.save(verification);
	}

	@Override
	public void verify(Verification t) {
		//check if it is verified
		if(t.getVerifiedBy() != null){
            //already verified
            logger.debug("Already verified");
            return;
        }
		//TODO Get the current logged in user and use as verifier
//        t.setVerifiedBy(verifier);
        t.setVerifiedOn(new Date());
//        logger.info("Verified by: "+ verifier.getUserName());

		Class<?> cc = null;
		Method method = null;

		try {
			cc = Class.forName(PACKAGE_NAME + t.getEntityName());
			logger.info("Class {}", cc.getName());
			method = cc.getMethod("deserialize", String.class);

			Object returned = cc.newInstance();
			method.invoke(returned, t.getAfterObject());
			logger.info("Object returned {}", returned.toString());
			logger.debug("Class {} ", returned.toString() );
			eman.persist(cc.cast(returned));

			AbstractEntity entity = (AbstractEntity) returned;
			t.setEntityId(entity.getId());
			t.setStatus(VerificationStatus.VERIFIED);
			verificationRepo.save(t);

		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
			logger.error("Error", e1);
		} catch (IllegalAccessException e) {
			logger.error("Error", e);
		} catch (IllegalArgumentException e) {
			logger.error("Error", e);
		} catch (InvocationTargetException e) {
			logger.error("Error", e);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			logger.error("Error", e);
		}

	}


	@Override
	public Verification getVerification(Long id) {
		return verificationRepo.findOne(id);
	}


	@Override
	public <T extends SerializableEntity<T>> String addNewVerificationRequest(T entity) throws JsonProcessingException {
		String classSimpleName = entity.getClass().getSimpleName();
		Verification verification = new Verification();
        verification.setBeforeObject("");
        verification.setAfterObject(entity.serialize());
        verification.setOriginal("");
        verification.setDescription("Added " + classSimpleName);
        //TODO get the Operation Code
//        verification.setOperationCode(entity.getAddCode());
        verification.setEntityName(classSimpleName);
        verification.setStatus(VerificationStatus.PENDING);
        //TODO use the current user as the initiator
        //verification.setInitiatedBy(initiator);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(classSimpleName + " creation request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());

		return classSimpleName + " creation request has been added";
	}


	@Override
	public void addModifyVerificationRequest(AbstractEntity originalEntity, AbstractEntity entity) throws JsonProcessingException {
		String classSimpleName = entity.getClass().getSimpleName();
		Verification verification = new Verification();
		verification.setBeforeObject(originalEntity.serialize());
		verification.setAfterObject(entity.serialize());
		verification.setOriginal(originalEntity.serialize());
		verification.setDescription("Modified " + classSimpleName);
		//TODO get the Operation Code
//		verification.setOperationCode(entity.getModifyCode());
		verification.setStatus(VerificationStatus.PENDING);
        //TODO use the current user as the initiator
        //verification.setInitiatedBy(initiator);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(classSimpleName + " Modification request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());
	}
}
