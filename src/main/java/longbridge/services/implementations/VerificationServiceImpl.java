package longbridge.services.implementations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import longbridge.models.AbstractEntity;
import longbridge.models.AdminUser;
import longbridge.models.OperationCode;
import longbridge.models.Role;
import longbridge.models.SerializableEntity;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.services.VerificationService;

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
			method = cc.getMethod("deserialize", String.class);

			Object returned = method.invoke(null, t.getAfterObject());
			logger.debug("Class {} ", cc.cast(returned).toString() );
			eman.persist(cc.cast(returned));

			AbstractEntity entity = (AbstractEntity) returned;
			t.setEntityId(entity.getId());
			verificationRepo.save(t);

		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
			logger.error("Error", e1);
		} catch (IllegalAccessException e) {
			logger.error("Error", e);
		} catch (IllegalArgumentException e) {
			logger.error("Error", e);
		} catch (InvocationTargetException e) {
			logger.error("Error", e);
		}
	
	}


	@Override
	public Verification getVerification(Long id) {
		return verificationRepo.findOne(id);
	}

	@Override
	public <T extends SerializableEntity<T>> void addNewVerificationRequest(T entity) throws JsonProcessingException {
		String classSimpleName = entity.getClass().getSimpleName();
		Verification verification = new Verification();
        verification.setBeforeObject("");
        verification.setAfterObject(entity.serialize());
        verification.setOriginal("");
        verification.setDescription("Added " + classSimpleName);
        verification.setOperationCode(entity.getAddCode());
        verification.setEntityName(classSimpleName);
        //TODO use the current user as the initiator
        //verification.setInitiatedBy(initiator);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(classSimpleName + " creation request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());
	}


	@Override
	public <T extends SerializableEntity<T>> void addModifyVerificationRequest(T originalEntity, T entity) throws JsonProcessingException {
		String classSimpleName = entity.getClass().getSimpleName();
		Verification verification = new Verification();
		verification.setBeforeObject(originalEntity.serialize());
		verification.setAfterObject(entity.serialize());       
		verification.setOriginal(originalEntity.serialize());
		verification.setDescription("Modified " + classSimpleName);
		verification.setOperationCode(entity.getModifyCode());
        //TODO use the current user as the initiator
        //verification.setInitiatedBy(initiator);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(classSimpleName + " Modification request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());
	}
}
