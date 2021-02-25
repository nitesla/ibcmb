package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.models.*;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import longbridge.services.MailService;
import longbridge.services.VerificationService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.persistence.EntityManager;
import java.util.*;

@Service
@Transactional
public class VerificationServiceImpl implements VerificationService {

    private static final String PACKAGE_NAME = "longbridge.models.";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
    private final Locale locale = LocaleContextHolder.getLocale();


    @Override
    public String cancel(VerificationDTO dto) throws VerificationException {

        Verification verification = verificationRepo.findById(dto.getId()).get();
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
            logger.warn(verification.getOperation() + " cancelled by {}", cancelledBy);
            return messageSource.getMessage("verification.cancel.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("verification.cancel.failure", null, locale), e);
        }
    }


    @Override
    public String decline(VerificationDTO dto) throws VerificationException {

        Verification verification = verificationRepo.findById(dto.getId()).get();
        String verifiedBy = getCurrentUserName();

        if(!isAuthorised(dto.getOperation()+"_V")){
            throw new VerificationException("You do not have the authority to decline");
        }
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

        Verification verification = verificationRepo.findById(dto.getId()).get();
        String verifiedBy = getCurrentUserName();
        if (!isAuthorised(dto.getOperation() + "_V")) {
            throw new VerificationException("You do not have the authority to verify");
        }
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
                corporateService.addCorporate(requestDTO);
            } else if ("ADD_CORPORATE_ACCOUNT".equals(verification.getOperation())) {
                CorporateRequestDTO requestDTO = mapper.readValue(verification.getOriginalObject(), CorporateRequestDTO.class);
                corporateService.addAccounts(requestDTO);
            } else if ("DELETE_CORPORATE_ACCOUNT".equals(verification.getOperation())) {
                CorporateRequestDTO requestDTO = mapper.readValue(verification.getOriginalObject(), CorporateRequestDTO.class);
                corporateService.deleteCorporateAccount(requestDTO);
            } else if ("UPDATE_CORPORATE_ROLE".equals(verification.getOperation())) {
                CorporateRole role = mapper.readValue(verification.getOriginalObject(), CorporateRole.class);
                corporateService.updateCorporateRole(role);
            } else if ("ADD_AUTHORIZER_FROM_CORPORATE_ADMIN".equals(verification.getOperation())) {
                CorporateUserDTO corpUser = mapper.readValue(verification.getOriginalObject(), CorporateUserDTO.class);
                corporateUserService.addAuthorizer(corpUser);
            } else if ("UPDATE_USER_FROM_CORPORATE_ADMIN".equals(verification.getOperation())) {
                CorporateUserDTO corpUser = mapper.readValue(verification.getOriginalObject(), CorporateUserDTO.class);
                corporateUserService.updateUserFromCorpAdmin(corpUser);
            }
            else if ("UPDATE_USER_ACCOUNT_PERMISSION".equals(verification.getOperation())) {
                CorporateUserDTO corpUser = mapper.readValue(verification.getOriginalObject(), CorporateUserDTO.class);
                corporateUserService.updateAccountPermissions(corpUser);
            }

            else if ("UPDATE_ACCOUNT_PERMISSION_FROM_CORPORATE_ADMIN".equals(verification.getOperation())) {
                CorporateUserDTO corpUser = mapper.readValue(verification.getOriginalObject(), CorporateUserDTO.class);
                corporateUserService.updateAccountPermissions(corpUser);
            }
            else {
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
    public String add(AbstractDTO object, String operation, String description) throws JsonProcessingException {

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

        Long id = object.getId();
        verification.setAfterObject(prettyMapper.writeValueAsString(object));

        if (id != null) {

            Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName, id, VerificationStatus.PENDING);
            if (pendingVerification != null) {
                logger.info("Found entity with pending verification");
                throw new InternetBankingException(entityName + " has changes pending for verification. Approve or " +
                        "decline the changes before making another one.");

            }
            verification.setEntityId(id);
            Object beforeObject = getBeforeObject(operation, object);
            if (beforeObject != null) {
                verification.setBeforeObject(prettyMapper.writeValueAsString(beforeObject));
            }

        }

        verification.setStatus(VerificationStatus.PENDING);
        verificationRepo.save(verification);
        logger.info(entityName + " has been saved for verification");

        return description + " has gone for verification";

    }

    @Override
    public String add(AbstractDTO object, String operation, String description, UserType userTypeForApproval) throws JsonProcessingException {

        User doneBy = getCurrentUser();
        String entityName = object.getClass().getSimpleName();

        Verification verification = new Verification();
        verification.setEntityName(entityName);
        verification.setInitiatedOn(new Date());
        verification.setInitiatedBy(doneBy.getUserName());
        verification.setUserType(userTypeForApproval);
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

        Long id = object.getId();
        verification.setAfterObject(prettyMapper.writeValueAsString(object));

        if (id != null) {

            Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName, id, VerificationStatus.PENDING);
            if (pendingVerification != null) {
                logger.info("Found entity with pending verification");
                throw new InternetBankingException(entityName + " has changes pending for verification. Approve or " +
                        "decline the changes before making another one.");

            }
            verification.setEntityId(id);
            Object beforeObject = getBeforeObject(operation, object);
            if (beforeObject != null) {
                verification.setBeforeObject(prettyMapper.writeValueAsString(beforeObject));
            }

        }

        verification.setStatus(VerificationStatus.PENDING);
        verificationRepo.save(verification);
        logger.info(entityName + " has been saved for verification");

        return description + " has gone for verification";

    }

    private Object getBeforeObject(String operation, AbstractDTO dto) {

        Object beforeObject = null;

        if ("UPDATE_USER_ACCOUNT_PERMISSION".equals(operation) || "UPDATE_ACCOUNT_PERMISSION_FROM_CORPORATE_ADMIN".equals(operation) ) {
            CorporateUserDTO corporateUserDTO = (CorporateUserDTO) dto;
            List<AccountPermissionDTO> accountPermissions = corporateUserService.getAccountPermissions(corporateUserDTO.getId());
            ModelMapper modelMapper = new ModelMapper();
            CorporateUserDTO existingUserDTO = modelMapper.map(corporateUserDTO, CorporateUserDTO.class);
            existingUserDTO.setAccountPermissions(accountPermissions);
            beforeObject = existingUserDTO;
        }

        return beforeObject;
    }

    private void notifyInitiator(Verification verification) {

        User verifiedBy = getCurrentUser();
        String initiator = verification.getInitiatedBy();

        User initiatedBy = null;

        switch (verification.getUserType()) {
            case ADMIN:
                initiatedBy = adminUserRepo.findFirstByUserNameIgnoreCase(initiator);
                break;
            case OPERATIONS:
                initiatedBy = operationsUserRepo.findFirstByUserNameIgnoreCase(initiator);
                break;
        }
        if (initiatedBy != null && initiatedBy.getEmail() != null) {
            String initiatorName = initiatedBy.getFirstName() + " " + initiatedBy.getLastName();
            String verifierName = verifiedBy.getFirstName() + " " + verifiedBy.getLastName();
            Date date = verification.getVerifiedOn();
            String operation = verification.getDescription();
            String comments = verification.getComments();
            String status = verification.getStatus().name();

            Context context = new Context();
            context.setVariable("fullName", initiatorName);
            context.setVariable("verifier", verifierName);
            context.setVariable("operation", operation);
            context.setVariable("date", date);
            context.setVariable("comments", comments);
            context.setVariable("status", status);


            Email email = new Email.Builder()
                    .setRecipient(initiatedBy.getEmail())
                    .setSubject(messageSource.getMessage("verification.subject", null, locale))
                    .setTemplate("mail/verification")
                    .build();

            mailService.sendMail(email, context);

        }

    }

    @Override
    public boolean isPendingVerification(Long entityId, String entityName) {

        Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                entityId, VerificationStatus.PENDING);
        return pendingVerification != null;
    }

    @Override
    public VerificationDTO getVerification(Long id) {
        return convertEntityToDTO(verificationRepo.findById(id).get());
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
        return new PageImpl<>(dtOs, pageDetails, t);

    }

    @Override
    public Page<VerificationDTO> getPendingOperations(String operation, Pageable pageable) {
        User doneBy = getCurrentUser();
        Page<Verification> page = verificationRepo.findByOperationAndInitiatedByAndUserTypeAndStatusOrderByInitiatedOnDesc(operation, doneBy.getUserName(), doneBy.getUserType(), VerificationStatus.PENDING, pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageable, t);

    }

    @Override
    public long getTotalNumberPending() {
        User doneBy = getCurrentUser();
        return verificationRepo.countByInitiatedByAndUserTypeAndStatus(doneBy.getUserName(), doneBy.getUserType(), VerificationStatus.PENDING);
    }


    @Override
    public int getTotalNumberForVerification() {

        try {
            User doneBy = getCurrentUser();
            List<String> permissions = getPermissionCodes(doneBy.getRole());
            //List<Verification> b = verificationRepo.findVerificationForUser(doneBy.getUserName(), doneBy.getUserType(), permissions);
           // return b.size();

            return verificationRepo.countVerificationForUser(doneBy.getUserName(), doneBy.getUserType(), permissions);

        } catch (Exception e) {
            logger.error("Error retrieving verification count", e);
        }

        return 0;
    }


    public Page<Verification> getVerificationsForUser(Pageable pageable) {
        User doneBy = getCurrentUser();
        List<String> permissions = getPermissionCodes(doneBy.getRole());
        logger.info("UserType =========== {} " , doneBy.getUserType());
        logger.info("PERMISSIONS =========== {} " , permissions);
        return verificationRepo.findVerificationForUsers(doneBy.getUserName(), doneBy.getUserType(), permissions, pageable);

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
        return new PageImpl<>(dtOs, pageable, t);

    }


    @Override
    public Page<VerificationDTO> getVerifiedOperations(Pageable pageable) {
        User verifiedBy = getCurrentUser();
        Page<Verification> page = verificationRepo.findVerifiedOperationsForUser(verifiedBy.getUserName(), verifiedBy.getUserType(), pageable);
        List<VerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageable, t);
    }

    private String getCurrentUserName() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return user.getUserName();
    }

    private User getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }

    private boolean isAuthorised(String authority) {
       return SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }
}


