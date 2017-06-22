package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.PendingVerification;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.VerificationException;
import longbridge.models.*;
import longbridge.repositories.VerificationRepo;
import longbridge.services.VerificationService;
import longbridge.utils.verificationStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

	@Autowired
	private ModelMapper modelMapper;


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
	public <T extends SerializableEntity<T>> String addNewVerificationRequest(T entity,User createdBy) throws JsonProcessingException, VerificationException {

		String classSimpleName = entity.getClass().getSimpleName();
		Verification verification = new Verification();
		verification.setBeforeObject("");
        verification.setAfterObject(entity.serialize());
        verification.setOriginal("");
        verification.setDescription("Added " + classSimpleName);
        verification.setUserType(createdBy.getUserType().name());
        verification.setEntityName(classSimpleName);
        verification.setCreatedBy(createdBy.getUserName());
        verification.setStatus(verificationStatus.PENDING);
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


	public VerificationDTO convertEntityToDTO(Verification verification)
	{
		return  modelMapper.map(verification,VerificationDTO.class);
	}

	public List<VerificationDTO> convertEntitiesToDTOs(Iterable<Verification> verifications)
	{
		List<VerificationDTO> verificationDTOArrayList = new ArrayList<>();
		for(Verification verification: verifications){
			VerificationDTO verificationDTO = convertEntityToDTO(verification);
			verificationDTOArrayList.add(verificationDTO);
		}
		return verificationDTOArrayList;
	}


	@Override
	public Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails,User createdBy)
	{
		 Page<Verification> page=verificationRepo.findByStatusAndCreatedBy(verificationStatus.PENDING,createdBy.getUserName(),pageDetails);
	//	System.out.print("This is the total length"+page.getTotalElements());
		List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
		Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs,pageDetails,t);
		return pageImpl;

	}

	@Override
	public long getTotalNumberPending(User user)
	{

		long totalNumberPending=verificationRepo.countByCreatedByAndUserTypeAndStatus(user.getUserName(),user.getUserType().name(),verificationStatus.PENDING);

		return totalNumberPending;
	}


	@Override
	public int getTotalNumberForVerification(User user)
	{

		List<Verification> b =verificationRepo.findVerificationForUser(user.getUserName(),user.getUserType().name());


		return b.size();
	}







//	@Override
//	public Page<PendingDTO> getPendingVerifications(Pageable pageable,User user)
//	{
//		List<Verification> verifications = verificationRepo.findByStatusAndCreatedByAndUserType(verificationStatus.PENDING,user.getUserName(),user.getUserType().name(),pageable);
//		Set<String> entities = new HashSet<>();
//		List<PendingDTO> pendingDTOs = new HashSet<>();
//
//		for(Verification verification: verifications)
//		{
//			entities.add(verification.getEntityName());
//		}
//		for(String entity: entities)
//		{
//			int countEntity = 0;
//			for(Verification verification: verifications){
//				if(entity.equals(verification.getEntityName())){
//					countEntity+=1;
//				}
//			}
//			PendingDTO pendingDTO = new PendingDTO();
//			pendingDTO.setEntityName(entity);
//			pendingDTO.setNumPending(countEntity);
//			pendingDTOs.add(pendingDTO);
//		}
//		return pendingDTOs;
//	}
//




	@Override
	public Page<PendingVerification> getPendingVerifications( Pageable pageable,User user)
	{
		Page<Verification> verifications = verificationRepo.findByStatusAndCreatedByAndUserType(verificationStatus.PENDING,user.getUserName(),user.getUserType().name(),pageable);
		Set<String> entities = new HashSet<>();
		List<PendingVerification> pendingVerifications = new ArrayList<>();
		for(Verification verification: verifications)
		{
			entities.add(verification.getEntityName());
		}
		for(String entity: entities)
		{
			int countEntity = 0;
			for(Verification verification: verifications)
			{
				if(entity.equals(verification.getEntityName()))
				{
					countEntity+=1;
				}
			}
			PendingVerification pendingVerification = new PendingVerification();
			pendingVerification.setEntityName(entity);
			pendingVerification.setNumPending(countEntity);
			pendingVerifications.add(pendingVerification);
		}
		PageImpl<PendingVerification> pendVerifications = new PageImpl<PendingVerification>(pendingVerifications,pageable,verifications.getTotalElements());
		return pendVerifications;
	}


	public List<Verification> getVerificationForUser(User user)
	{
		List<Verification> verifications = verificationRepo.findVerificationForUser(user.getUserName(),user.getUserType().name());
		return  verifications;

	}




}
