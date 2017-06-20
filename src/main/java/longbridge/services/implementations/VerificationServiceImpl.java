package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.VerificationException;
import longbridge.models.AbstractEntity;
import longbridge.models.AdminUser;
import longbridge.models.SerializableEntity;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.services.VerificationService;
import longbridge.utils.verificationStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

@Service
@Transactional
public class VerificationServiceImpl implements VerificationService {

	private static final String PACKAGE_NAME = "longbridge.models.";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private VerificationRepo verificationRepo;
	@Autowired
    private EntityManager eman;
	@Autowired
	private MessageSource messageSource;

	Locale locale = LocaleContextHolder.getLocale();

	@Override
	public String decline(Verification verification, String declineReason) throws VerificationException {
//		verification.setDeclinedBy(decliner);
		verification.setDeclinedOn(new Date());
        verification.setDeclineReason(declineReason);
        verificationRepo.save(verification);
        return messageSource.getMessage("verification.decline",null,locale);
	}

	@Override
	@Transactional
	public String verify(Long verId) throws VerificationException {
		//check if it is verified
		Verification t = verificationRepo.findOne(verId);
		if(t.getVerifiedBy() != null){
			//already verified
			logger.debug("Already verified");
            return messageSource.getMessage("verification.verify",null,locale);
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
			logger.info("Object retrieved {}", t.getAfterObject());
			logger.info("Object returned {}", returned.toString());
			logger.debug("Class {} ", returned.toString());
			if(t.getEntityId()==null) {
				eman.persist(cc.cast(returned));

			}
			else{
				Object obj = eman.find(cc,t.getEntityId());
				ModelMapper modelMapper = new ModelMapper();
				modelMapper.map(returned,obj);
				eman.persist(cc.cast(obj));
			}
			AbstractEntity entity = (AbstractEntity) returned;

			t.setEntityId(entity.getId());
			t.setStatus(verificationStatus.VERIFIED);
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
		return messageSource.getMessage("verification.verify",null,locale);
	}


	@Override
	public Verification getVerification(Long id) {
		return verificationRepo.findOne(id);
	}


	@Override

	public <T extends SerializableEntity<T>> String addNewVerificationRequest(T entity) throws JsonProcessingException, VerificationException {

		String classSimpleName = entity.getClass().getSimpleName();
		Verification verification = new Verification();
		verification.setBeforeObject("");
        verification.setAfterObject(entity.serialize());
        verification.setOriginal("");
        verification.setDescription("Added " + classSimpleName);
        //TODO get the Operation Code
//        verification.setOperationCode(entity.getAddCode());
        verification.setEntityName(classSimpleName);
        verification.setStatus(verificationStatus.PENDING);
        //TODO use the current user as the initiator
        //verification.setInitiatedBy(initiator);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(classSimpleName + " creation request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());

		return String.format(messageSource.getMessage("verification.add.success",null,locale),classSimpleName);


//		public <T extends SerializableEntity<T>> String makerCheckerSave(T originalEntity, T entity) throws JsonProcessingException, VerificationException {
//
//			AbstractEntity originalEntity1 = (AbstractEntity) (originalEntity);
//
//			if (originalEntity1.getId() == null) {
//				String message = verificationService.addNewVerificationRequest(entity);
//				return message;
//
//			} else {
//
//				String message = verificationService.addModifyVerificationRequest(originalEntity, entity);
//				return message;
//
//			}
//
//
//		}
	}


	@Override
	public <T extends SerializableEntity<T>> String addModifyVerificationRequest(T originalEntity, T entity) throws JsonProcessingException,DuplicateObjectException {

		String className = entity.getClass().getSimpleName();

		AbstractEntity originalEntity1 = (AbstractEntity)originalEntity;

		Verification verification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(className,originalEntity1.getId(),verificationStatus.PENDING);

		if(verification!=null){
			throw new DuplicateObjectException("Entity has pending verification");
		}
		verification = new Verification();
		verification.setEntityName(className);
		verification.setEntityId(originalEntity1.getId());
		verification.setBeforeObject(originalEntity.serialize());
		verification.setAfterObject(entity.serialize());
		verification.setOriginal(originalEntity.serialize());
		verification.setDescription("Modified " + className);
		//TODO get the Operation Code
//		verification.setOperationCode(entity.getModifyCode());
		verification.setStatus(verificationStatus.PENDING);
        //TODO use the current user as the initiator
        //verification.setInitiatedBy(initiator);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(className + " Modification request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());
		return String.format(messageSource.getMessage("verification.modify.success",null,locale),className);
	}
}
