package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.dtos.CorpUserVerificationDTO;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.CorpUserVerificationRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorpUserVerificationService;
import longbridge.services.MailService;
import longbridge.utils.DateFormatter;
import longbridge.utils.PrettySerializer;
import longbridge.utils.VerificationStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Created by Showboy on 28/07/2017.
 */
@Service
//@Transactional
public class CorpUserVerificationServiceImpl implements CorpUserVerificationService {

    private static final String PACKAGE_NAME = "longbridge.models.";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CorpUserVerificationRepo corpUserVerificationRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;

    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    MailService mailService;

    @Autowired
    private ModelMapper modelMapper;

    Locale locale = LocaleContextHolder.getLocale();

    @Override
    public void saveInitiator(CorporateUser user, String operation, String description) throws VerificationException {
        try {
            String entityName = user.getClass().getSimpleName();
            if (user.getId() != null) {
                Long id = user.getId();

                CorpUserVerification pendingVerification = corpUserVerificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    logger.info("Found entity with pending verification");
                    throw new InternetBankingException(entityName + " has changes pending for verification. Approve or " +
                            "decline the changes before making another one.");
                }
            }

            CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            CorporateUser doneBy = corporateUserRepo.findByUserName(principal.getUsername());


            CorpUserVerification corpUserVerification = new CorpUserVerification();
            corpUserVerification.setEntityName(entityName);
            corpUserVerification.setInitiatedOn(new Date());
            corpUserVerification.setInitiatedBy(doneBy.getUserName());
            corpUserVerification.setCorpUserType(CorpUserType.AUTHORIZER);
            corpUserVerification.setOperation(operation);
            corpUserVerification.setDescription(description);
            ObjectMapper mapper = new ObjectMapper();
            corpUserVerification.setOriginalObject(mapper.writeValueAsString(user));


            ObjectMapper prettyMapper = new ObjectMapper();

            if (user instanceof PrettySerializer) {
                JsonSerializer<Object> serializer = ((PrettySerializer) (user)).getSerializer();

                SimpleModule module = new SimpleModule();
                module.addSerializer(user.getClass(), serializer);
                prettyMapper.registerModule(module);
                logger.debug("Registering Pretty serializer for " + user.getClass().getName());
            }

            if (user.getId() != null) {
                Long id = user.getId();
                AbstractEntity originalEntity = entityManager.find(user.getClass(), id);

                CorpUserVerification pendingVerification = corpUserVerificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    // found pending verification
                    throw new InternetBankingException(entityName + " has pending verification");
                }

                corpUserVerification.setBeforeObject(prettyMapper.writeValueAsString(originalEntity));
                corpUserVerification.setEntityId(user.getId());
            }
            corpUserVerification.setAfterObject(prettyMapper.writeValueAsString(user));
            corpUserVerification.setStatus(VerificationStatus.PENDING);
            corpUserVerificationRepo.save(corpUserVerification);

            logger.info(entityName + " has been saved for verification");

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            throw new VerificationInterruptedException(description + " has gone for verification");

        }catch (JsonProcessingException e){
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void saveAuthorizer(CorporateUser user, String operation, String description) throws VerificationException {
        try {
            String entityName = user.getClass().getSimpleName();
            if (user.getId() != null) {
                Long id = user.getId();

                Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    logger.info("Found entity with pending verification");
                    throw new InternetBankingException(entityName + " has changes pending for verification. Approve or " +
                            "decline the changes before making another one.");
                }
            }

            CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            CorporateUser doneBy = corporateUserRepo.findByUserName(principal.getUsername());


            Verification verification = new Verification();
            verification.setEntityName(entityName);
            verification.setInitiatedOn(new Date());
            verification.setInitiatedBy(doneBy.getUserName());
            verification.setUserType(UserType.OPERATIONS);
            verification.setOperation(operation);
            verification.setDescription(description);
            ObjectMapper mapper = new ObjectMapper();
            verification.setOriginalObject(mapper.writeValueAsString(user));


            ObjectMapper prettyMapper = new ObjectMapper();

            if (user instanceof PrettySerializer) {
                JsonSerializer<Object> serializer = ((PrettySerializer) (user)).getSerializer();

                SimpleModule module = new SimpleModule();
                module.addSerializer(user.getClass(), serializer);
                prettyMapper.registerModule(module);
                logger.debug("Registering Pretty serializer for " + user.getClass().getName());
            }

            if (user.getId() != null) {
                Long id = user.getId();
                AbstractEntity originalEntity = entityManager.find(user.getClass(), id);

                Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    // found pending verification
                    throw new InternetBankingException(entityName + " has pending verification");
                }

                verification.setBeforeObject(prettyMapper.writeValueAsString(originalEntity));
                verification.setEntityId(user.getId());
            }
            verification.setAfterObject(prettyMapper.writeValueAsString(user));


            verification.setStatus(VerificationStatus.PENDING);

            logger.info("verification >>>>  " + verification);
            verificationRepo.save(verification);

            logger.info(entityName + " has been saved for verification with the bank");

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            throw new VerificationInterruptedException(description + " has gone for verification with the bank");

        }catch (JsonProcessingException e){
            logger.error(e.getMessage(), e);
            throw new VerificationException(e.getMessage());
        }

    }

    @Override
    public String decline(VerificationDTO dto) throws VerificationException {

        CorpUserVerification corpUserVerification = corpUserVerificationRepo.findOne(dto.getId());
        String verifiedBy = getCurrentUserName();

        if(verifiedBy.equals(corpUserVerification.getInitiatedBy())){
            throw new VerificationException("You cannot verify what you initiated");
        }

        if (!VerificationStatus.PENDING.equals(corpUserVerification.getStatus())) {
            throw new VerificationException("Verification is not pending for the operation");
        }

        try {
            corpUserVerification.setVersion(dto.getVersion());
            corpUserVerification.setVerifiedBy(getCurrentUserName());
            corpUserVerification.setComments(dto.getComments());
            corpUserVerification.setVerifiedOn(new Date());
            corpUserVerification.setStatus(VerificationStatus.DECLINED);
            corpUserVerificationRepo.save(corpUserVerification);
            notifyInitiator(corpUserVerification);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("verification.decline.failure",null,locale),e);
        }
        return messageSource.getMessage("verification.decline", null, locale);
    }


    @Override
    public String verify(Long id) throws VerificationException{
        CorpUserVerificationDTO corpUserVerificationDTO = getVerification(id);
        return verify(corpUserVerificationDTO);
    }

    @Override
    public String verify(CorpUserVerificationDTO dto) throws VerificationException {

        CorpUserVerification corpUserVerification = corpUserVerificationRepo.findOne(dto.getId());
        String verifiedBy = getCurrentUserName();
        if(verifiedBy.equals(corpUserVerification.getInitiatedBy())){
            throw new VerificationException("You cannot verify what you initiated");
        }

        if (!VerificationStatus.PENDING.equals(corpUserVerification.getStatus())) {
            throw new VerificationException("Verification is not pending for the operation");
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            Class<?> clazz  = Class.forName(PACKAGE_NAME + corpUserVerification.getEntityName());
            Object object = mapper.readValue(corpUserVerification.getOriginalObject(), clazz);
            logger.info("Trying to merge..{}",object.toString());
            entityManager.merge(object);
            corpUserVerification.setId(dto.getId());
            corpUserVerification.setVersion(dto.getVersion());
            corpUserVerification.setVerifiedBy(getCurrentUserName());
            corpUserVerification.setVerifiedOn(new Date());
            corpUserVerification.setComments(dto.getComments());
            corpUserVerification.setStatus(VerificationStatus.APPROVED);
            corpUserVerificationRepo.save(corpUserVerification);
            notifyInitiator(corpUserVerification);

        }
        catch (Exception e) {
            logger.error("Error verifying operation");
            throw new InternetBankingException("Failed to verify the operation",e);
        }
        return messageSource.getMessage("verification.verify", null, locale);
    }

    @Async
    private void notifyInitiator(CorpUserVerification corpUserVerification){

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User verifiedBy = principal.getUser();

        String initiator = corpUserVerification.getInitiatedBy();

        User initiatedBy = corporateUserRepo.findFirstByUserName(initiator);

        if(initiatedBy!=null) {
            if (initiatedBy.getEmail() != null) {
                String initiatorName = initiatedBy.getFirstName()+" "+initiatedBy.getLastName();
                String verifierName = verifiedBy.getFirstName()+" "+verifiedBy.getLastName();
                Date date = corpUserVerification.getVerifiedOn();
                String operation = corpUserVerification.getDescription();
                String comment = corpUserVerification.getComments();
                String status = corpUserVerification.getStatus().name();
                Email email = new Email.Builder()
                        .setRecipient(initiatedBy.getEmail())
                        .setSubject(messageSource.getMessage("verification.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("verification.message", null, locale),initiatorName, verifierName, operation, status, DateFormatter.format(date),comment))
                        .build();
                new Thread(() -> {
                    mailService.send(email);
                }).start();
            }
        }
    }

    @Override
    public CorpUserVerificationDTO getVerification(Long id) {
        return convertEntityToDTO(corpUserVerificationRepo.findOne(id));
    }


    @Override
    public CorpUserVerificationDTO convertEntityToDTO(CorpUserVerification corpUserVerification) {
        CorpUserVerificationDTO corpUserVerificationDTO = modelMapper.map(corpUserVerification, CorpUserVerificationDTO.class);
        return corpUserVerificationDTO;
    }

    @Override
    public List<CorpUserVerificationDTO> convertEntitiesToDTOs(Iterable<CorpUserVerification> corpUserVerifications) {
        List<CorpUserVerificationDTO> corpUserVerificationDTOList = new ArrayList<>();
        for (CorpUserVerification corpUserVerification : corpUserVerifications) {
            CorpUserVerificationDTO corpUserVerificationDTO = convertEntityToDTO(corpUserVerification);
            corpUserVerificationDTOList.add(corpUserVerificationDTO);
        }
        return corpUserVerificationDTOList;
    }

    @Override
    public long getTotalNumberPending() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CorporateUser doneBy = (CorporateUser) principal.getUser();
        long totalNumberPending = corpUserVerificationRepo.countByInitiatedByAndCorpUserTypeAndStatus(doneBy.getUserName(),doneBy.getCorpUserType(), VerificationStatus.PENDING);
        return totalNumberPending;
    }


    @Override
    public int getTotalNumberForVerification() {

        try {
            CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CorporateUser doneBy = (CorporateUser) principal.getUser();
            List<String> permissions = getPermissionCodes(doneBy.getRole());
            List<CorpUserVerification> b = corpUserVerificationRepo.findVerificationForUser(doneBy.getUserName(), doneBy.getCorpUserType(), permissions);
            return b.size();
        }
        catch (Exception e){
            logger.error("Error retrieving verification",e);
        }
        return 0;
    }


    @Override
    public Page<CorpUserVerificationDTO> getAllRequests(Pageable pageable) {
        Page<CorpUserVerification> page = corpUserVerificationRepo.findAll(pageable);
        List<CorpUserVerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t =page.getTotalElements();
        Page<CorpUserVerificationDTO> pageImpl = new PageImpl<CorpUserVerificationDTO>(dtOs,pageable,t);
        return pageImpl;
    }

    public List<String> getPermissionCodes(Role role) {
        Collection<Permission> permissions = role.getPermissions();
        List<String> permCodes = new ArrayList<>();
        for (Permission permission : permissions) {
            permCodes.add(permission.getCode());
        }
        return permCodes;
    }

    private String getCurrentUserName(){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return user.getUserName();
    }
}