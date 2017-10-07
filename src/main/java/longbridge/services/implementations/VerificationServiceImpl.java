package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import longbridge.services.MailService;
import longbridge.services.VerificationService;
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
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class VerificationServiceImpl implements VerificationService {

    private static final String PACKAGE_NAME = "longbridge.models.";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationRepo verificationRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private OperationsUserRepo operationsUserRepo;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private MailService mailService;

    @Autowired
    private ModelMapper modelMapper;

    private Locale locale = LocaleContextHolder.getLocale();


    @Override
    public String cancel(VerificationDTO dto) throws VerificationException {

        Verification verification = verificationRepo.findOne(dto.getId());
        String cancelledBy = getCurrentUserName();

        if (!cancelledBy.equals(verification.getInitiatedBy())) {
            throw new VerificationException("You cannot cancel what you did not initiate");
        }

        if (!VerificationStatus.PENDING.equals(verification.getStatus())) {
            throw new VerificationException("Verification is not pending for the operation");
        }

        try {
            verification.setVersion(dto.getVersion());
            verification.setVerifiedBy(getCurrentUserName());
            verification.setComments(dto.getComments());
            verification.setVerifiedOn(new Date());
            verification.setStatus(VerificationStatus.CANCELLED);
            verificationRepo.save(verification);
            logger.warn(verification.getOperation()+" cancelled by {}",cancelledBy);
            return messageSource.getMessage("verification.cancel.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("verification.cancel.failure", null, locale), e);
        }
    }


    @Override
    public String decline(VerificationDTO dto) throws VerificationException {

        Verification verification = verificationRepo.findOne(dto.getId());
        String verifiedBy = getCurrentUserName();

        if (verifiedBy.equals(verification.getInitiatedBy())) {
            throw new VerificationException("You cannot verify what you initiated");
        }

        if (!VerificationStatus.PENDING.equals(verification.getStatus())) {
            throw new VerificationException("Verification is not pending for the operation");
        }

        try {
            verification.setVersion(dto.getVersion());
            verification.setVerifiedBy(getCurrentUserName());
            verification.setComments(dto.getComments());
            verification.setVerifiedOn(new Date());
            verification.setStatus(VerificationStatus.DECLINED);
            verificationRepo.save(verification);
            notifyInitiator(verification);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("verification.decline.failure", null, locale), e);
        }
        return messageSource.getMessage("verification.decline", null, locale);
    }


    @Override
    public String verify(Long id) throws VerificationException {
        VerificationDTO verificationDTO = getVerification(id);
        return verify(verificationDTO);
    }

    @Override
    public String verify(VerificationDTO dto) throws VerificationException {

        Verification verification = verificationRepo.findOne(dto.getId());
        String verifiedBy = getCurrentUserName();
        if (verifiedBy.equals(verification.getInitiatedBy())) {
            throw new VerificationException("You cannot verify what you initiated");
        }

        if (!VerificationStatus.PENDING.equals(verification.getStatus())) {
            throw new VerificationException("Verification is not pending for the operation");
        }

        ObjectMapper mapper = new ObjectMapper();


        try {

            Class<?> clazz;
            Object object;

            if ("ADD_CORPORATE".equals(verification.getOperation())) {
                CorporateRequestDTO requestDTO = mapper.readValue(verification.getOriginalObject(), CorporateRequestDTO.class);
                corporateService.saveCorporateRequest(requestDTO);
            } else if ("ADD_AUTHORIZER_FROM_CORPORATE_ADMIN".equals(verification.getOperation())) {

                CorporateUserDTO corpUser = mapper.readValue(verification.getOriginalObject(), CorporateUserDTO.class);
                corporateUserService.addAuthorizer(corpUser);
            } else if ("UPDATE_USER_FROM_CORPORATE_ADMIN".equals(verification.getOperation())) {

                CorporateUserDTO corpUser = mapper.readValue(verification.getOriginalObject(), CorporateUserDTO.class);
                corporateUserService.updateUserFromCorpAdmin(corpUser);
            } else {
                clazz = Class.forName(PACKAGE_NAME + verification.getEntityName());
                object = mapper.readValue(verification.getOriginalObject(), clazz);
                entityManager.merge(object);
            }
            verification.setId(dto.getId());
            verification.setVersion(dto.getVersion());
            verification.setVerifiedBy(getCurrentUserName());
            verification.setVerifiedOn(new Date());
            verification.setComments(dto.getComments());
            verification.setStatus(VerificationStatus.APPROVED);
            verificationRepo.save(verification);
            notifyInitiator(verification);

        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            logger.error("Error verifying operation");
            throw new InternetBankingException("Failed to verify the operation", e);
        }
        return messageSource.getMessage("verification.verify", null, locale);
    }

    @Override
    public String add(Object object, String operation, String description) throws JsonProcessingException {

        User doneBy = getCurrentUser();
        String entityName = object.getClass().getSimpleName();

        Verification verification = new Verification();
        verification.setEntityName(entityName);
        verification.setInitiatedOn(new Date());
        verification.setInitiatedBy(doneBy.getUserName());
        verification.setUserType(doneBy.getUserType());
        verification.setOperation(operation);
        verification.setDescription(description);
        ObjectMapper mapper = new ObjectMapper();
        verification.setOriginalObject(mapper.writeValueAsString(object));


        ObjectMapper prettyMapper = new ObjectMapper();

        if (object instanceof PrettySerializer) {
            JsonSerializer<Object> serializer = ((PrettySerializer) (object)).getSerializer();

            SimpleModule module = new SimpleModule();
            module.addSerializer(object.getClass(), serializer);
            prettyMapper.registerModule(module);
            logger.debug("Registering Pretty serializer for " + object.getClass().getName());
        }


        verification.setAfterObject(prettyMapper.writeValueAsString(object));
        verification.setStatus(VerificationStatus.PENDING);
        verificationRepo.save(verification);

        logger.info(entityName + " has been saved for verification");

        return description + " has gone for verification";

    }

    @Async
    private void notifyInitiator(Verification verification) {

        User verifiedBy = getCurrentUser();
        String initiator = verification.getInitiatedBy();

        User initiatedBy = null;

        switch (verification.getUserType()) {
            case ADMIN:
                initiatedBy = adminUserRepo.findFirstByUserName(initiator);
                break;
            case OPERATIONS:
                initiatedBy = operationsUserRepo.findFirstByUserName(initiator);
                break;
        }
        if (initiatedBy != null && initiatedBy.getEmail() != null) {
                String initiatorName = initiatedBy.getFirstName() + " " + initiatedBy.getLastName();
                String verifierName = verifiedBy.getFirstName() + " " + verifiedBy.getLastName();
                Date date = verification.getVerifiedOn();
                String operation = verification.getDescription();
                String comment = verification.getComments();
                String status = verification.getStatus().name();
                Email email = new Email.Builder()
                        .setRecipient(initiatedBy.getEmail())
                        .setSubject(messageSource.getMessage("verification.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("verification.message", null, locale), initiatorName, verifierName, operation, status, DateFormatter.format(date), comment))
                        .build();
                new Thread(() -> mailService.send(email)).start();
            }

    }

    @Override
    public boolean isPendingVerification(Long entityId, String entityName) {

        Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                entityId, VerificationStatus.PENDING);
        if (pendingVerification != null) {
            return true;
        }

        return false;
    }

    @Override
    public VerificationDTO getVerification(Long id) {
        return convertEntityToDTO(verificationRepo.findOne(id));
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
    public Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails) {

        User doneBy = getCurrentUser();
        Page<Verification> page = verificationRepo.findByStatusAndInitiatedByOrderByInitiatedOnDesc(VerificationStatus.PENDING, doneBy.getUserName(), pageDetails);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageDetails, t);
        return pageImpl;

    }

    @Override
    public Page<VerificationDTO> getPendingOperations(String operation, Pageable pageable) {
        User doneBy = getCurrentUser();
        Page<Verification> page = verificationRepo.findByOperationAndInitiatedByAndUserTypeAndStatusOrderByInitiatedOnDesc(operation, doneBy.getUserName(), doneBy.getUserType(), VerificationStatus.PENDING, pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageable, t);
        return pageImpl;

    }

    @Override
    public long getTotalNumberPending() {
        User doneBy = getCurrentUser();
        long totalNumberPending = verificationRepo.countByInitiatedByAndUserTypeAndStatus(doneBy.getUserName(), doneBy.getUserType(), VerificationStatus.PENDING);
        return totalNumberPending;
    }


    @Override
    public int getTotalNumberForVerification() {

        try {
            User doneBy = getCurrentUser();
            List<String> permissions = getPermissionCodes(doneBy.getRole());
            List<Verification> b = verificationRepo.findVerificationForUser(doneBy.getUserName(), doneBy.getUserType(), permissions);
            return b.size();
        } catch (Exception e) {
            logger.error("Error retrieving verification", e);
        }
        return 0;
    }


    public Page<Verification> getVerificationsForUser(Pageable pageable) {
        User doneBy = getCurrentUser();
        List<String> permissions = getPermissionCodes(doneBy.getRole());
        Page<Verification> verifications = verificationRepo.findVerificationForUsers(doneBy.getUserName(), doneBy.getUserType(), permissions, pageable);
        return verifications;

    }

    public List<String> getPermissionCodes(Role role) {
        Collection<Permission> permissions = role.getPermissions();
        List<String> permCodes = new ArrayList<>();
        for (Permission permission : permissions) {
            permCodes.add(permission.getCode());
        }
        return permCodes;
    }


    public Page<VerificationDTO> getPendingForUser(Pageable pageable) {
        User doneBy = getCurrentUser();
        Page<Verification> page = verificationRepo.findByInitiatedByAndUserTypeAndStatusOrderByInitiatedOnDesc(doneBy.getUserName(), doneBy.getUserType(), VerificationStatus.PENDING, pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageable, t);
        return pageImpl;

    }


    @Override
    public Page<VerificationDTO> getVerifiedOPerations(Pageable pageable) {
        User verifiedBy = getCurrentUser();
        Page<Verification> page = verificationRepo.findVerifiedOperationsForUser(verifiedBy.getUserName(), verifiedBy.getUserType(), pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<VerificationDTO> pageImpl = new PageImpl<VerificationDTO>(dtOs, pageable, t);
        return pageImpl;
    }

    private String getCurrentUserName() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return user.getUserName();
    }

    private User getCurrentUser(){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }
}


