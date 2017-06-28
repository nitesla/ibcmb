package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.PendingVerification;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.VerificationException;
import longbridge.models.*;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.MakerCheckerService;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

//    @Autowired
//    MakerCheckerService makerCheckerService;


    Locale locale = LocaleContextHolder.getLocale();

    @Override
    public String decline(Verification verification, String declineReason) throws VerificationException {
		verification.setDeclinedBy(getCurrentUserName());
        verification.setDeclinedOn(new Date());
        verification.setDeclineReason(declineReason);
        verificationRepo.save(verification);
        return messageSource.getMessage("verification.decline", null, locale);
    }


    @Override
    public String verify(Long verId) throws VerificationException {
        //check if it is verified
        Verification verification = verificationRepo.findOne(verId);
        if (verification.getVerifiedBy() != null) {
            logger.debug("Already verified");
            return messageSource.getMessage("verification.verify", null, locale);
        }
        verification.setVerifiedBy(getCurrentUserName());
        verification.setVerifiedOn(new Date());

        Class<?> cc ;
        Method method;

        try {
            cc = Class.forName(PACKAGE_NAME + verification.getEntityName());
            logger.info("Class {}", cc.getName());
            method = cc.getMethod("deserialize", String.class);
            Object returned = cc.newInstance();
            method.invoke(returned, verification.getAfterObject());
            if (verification.getEntityId() == null) {
                eman.persist(cc.cast(returned));

            } else {
                Object obj = eman.find(cc, verification.getEntityId());
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.map(returned, obj);
                eman.merge(cc.cast(obj));
            }
            AbstractEntity entity = (AbstractEntity) returned;
            verification.setEntityId(entity.getId());
            verification.setStatus(verificationStatus.VERIFIED);
            verificationRepo.save(verification);

        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
            logger.error("Error", e1);
        } catch (IllegalAccessException e) {
            logger.error("Error", e);
        } catch (IllegalArgumentException e) {
            logger.error("Error", e);
        } catch (InvocationTargetException e) {
            logger.error("Error", e);
        } catch (InstantiationException e) {
            logger.error("Error", e);
        }
        return messageSource.getMessage("verification.verify", null, locale);
    }


    @Override
    public VerificationDTO getVerification(Long id) {
        return convertEntityToDTO(verificationRepo.findOne(id));
    }


    @Override
    public <T extends SerializableEntity<T>> String addNewVerificationRequest(T entity, User createdBy, String operation) throws JsonProcessingException, VerificationException {

        String classSimpleName = entity.getClass().getSimpleName();
        Verification verification = new Verification();
        verification.setBeforeObject("");
        verification.setAfterObject(entity.serialize());
        verification.setOriginalObject("");
        verification.setDescription("Added " + classSimpleName);
        verification.setUserType(createdBy.getUserType());
        verification.setEntityName(classSimpleName);
        verification.setInitiatedBy(createdBy.getUserName());
        verification.setStatus(verificationStatus.PENDING);
        verification.setInitiatedOn(new Date());
        verificationRepo.save(verification);
        logger.info(classSimpleName + " creation request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());

        return String.format(messageSource.getMessage("verification.add.success", null, locale), classSimpleName);

    }


    @Override
    public <T extends SerializableEntity<T>> String addModifyVerificationRequest(T modifiedEntity, User doneBy, String operation) throws JsonProcessingException, DuplicateObjectException {

        String className = modifiedEntity.getClass().getSimpleName();
        AbstractEntity originalEntity1 = (AbstractEntity) modifiedEntity;

        try {
            AbstractEntity originalEntity = (AbstractEntity)eman.find(modifiedEntity.getClass(), ((AbstractEntity) modifiedEntity).getId());
            Verification verification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(className, originalEntity1.getId(), verificationStatus.PENDING);

            if (verification != null) {
                throw new DuplicateObjectException("Entity has pending verification");
            }
            verification = new Verification();
            verification.setEntityName(className);
            verification.setEntityId(originalEntity1.getId());
            verification.setBeforeObject(originalEntity.serialize());
            verification.setAfterObject(modifiedEntity.serialize());
            verification.setOriginalObject(originalEntity.serialize());
            verification.setOperation(operation);
            verification.setDescription("Modified " + className);
            verification.setStatus(verificationStatus.PENDING);
            verification.setUserType(doneBy.getUserType());
            verification.setInitiatedOn(new Date());
            verificationRepo.save(verification);
            logger.info(className + " Modification request has been added. Before {}, After {}", verification.getBeforeObject(), verification.getAfterObject());
            return String.format(messageSource.getMessage("verification.modify.success", null, locale), className);
        } catch (Exception e) {
            logger.error("Error updating entity");
            return String.format(messageSource.getMessage("verification.modify.success", null, locale), className);
        }
    }


    public VerificationDTO convertEntityToDTO(Verification verification) {
        return modelMapper.map(verification, VerificationDTO.class);
    }

    public List<VerificationDTO> convertEntitiesToDTOs(Iterable<Verification> verifications) {
        List<VerificationDTO> verificationDTOArrayList = new ArrayList<>();
        for (Verification verification : verifications) {
            VerificationDTO verificationDTO = convertEntityToDTO(verification);
            verificationDTOArrayList.add(verificationDTO);
        }
        return verificationDTOArrayList;
    }


    @Override
    public Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails, User createdBy) {
        Page<Verification> page = verificationRepo.findByStatusAndInitiatedBy(verificationStatus.PENDING, createdBy.getUserName(), pageDetails);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageDetails, t);
        return pageImpl;

    }

    @Override
    public Page<VerificationDTO> getPendingOperations(String operation,User user,Pageable pageable) {
        Page<Verification> page = verificationRepo.findByOperationAndInitiatedByAndUserTypeAndStatus(operation,user.getUserName(),user.getUserType(),verificationStatus.PENDING,pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageable, t);
        return pageImpl;

    }

    @Override
    public long getTotalNumberPending(User user) {

        long totalNumberPending = verificationRepo.countByInitiatedByAndUserTypeAndStatus(user.getUserName(), user.getUserType(), verificationStatus.PENDING);
        return totalNumberPending;
    }


    @Override
    public int getTotalNumberForVerification(User user) {

        List<Verification> b = verificationRepo.findVerificationForUser(user.getUserName(), user.getUserType());
        return b.size();
    }



    @Override
    public Page<PendingVerification> getPendingVerifications(User user, Pageable pageable) {
        Page<Verification> verifications = verificationRepo.findByStatusAndInitiatedByAndUserType(verificationStatus.PENDING, user.getUserName(), user.getUserType(), pageable);
        Set<String> entities = new HashSet<>();
        List<PendingVerification> pendingVerifications = new ArrayList<>();
        for (Verification verification : verifications) {
            entities.add(verification.getOperation());
        }
        for (String entity : entities) {
            int countOp = 0;
            PendingVerification pendingVerification = new PendingVerification();

            for (Verification verification : verifications) {
                if (entities.equals(verification.getOperation())) {
                    countOp += 1;
                    pendingVerification.setId(verification.getId());
                }
            }
            pendingVerification.setEntityName(entity);
            pendingVerification.setNumPending(countOp);
            pendingVerifications.add(pendingVerification);
        }
        PageImpl<PendingVerification> pendVerifications = new PageImpl<PendingVerification>(pendingVerifications, pageable, verifications.getTotalElements());
        return pendVerifications;
    }


    public List<VerificationDTO> getVerificationsForUser(User user) {
        List<Verification> verifications = verificationRepo.findVerificationForUser(user.getUserName(), user.getUserType());

        return convertEntitiesToDTOs(verifications);

    }


    public List<VerificationDTO> getPendingForUser(User user) {
        List<Verification> verifications = verificationRepo.findByInitiatedByAndUserType(user.getUserName(), user.getUserType());
        for(Verification verification: verifications){
            if(verification.getVerifiedBy()==null){
                verification.setVerifiedBy("PENDING...");
            }
        }
        return convertEntitiesToDTOs(verifications);

    }

    public <T extends SerializableEntity<T>> String save(T originalEntity, T modifiedEntity, User doneBy, String operation) throws JsonProcessingException, VerificationException {

        AbstractEntity entity = (AbstractEntity) (originalEntity);

        if (entity.getId() == null) {
            String message = addNewVerificationRequest(originalEntity, doneBy, operation);
            return message;
        } else {
            String message = addModifyVerificationRequest(modifiedEntity, doneBy, operation);
            return message;

        }
    }


    private String getCurrentUserName(){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return user.getUserName();
    }
}
