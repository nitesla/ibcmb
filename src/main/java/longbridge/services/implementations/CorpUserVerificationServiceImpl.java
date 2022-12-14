package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.dtos.CorpUserVerificationDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.EntrustException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.PrettySerializer;
import longbridge.utils.Verifiable;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;

/**
 * Created by Wunmi on 28/07/2017.
 */
@Service
public class CorpUserVerificationServiceImpl implements CorpUserVerificationService {

    private static final String PACKAGE_NAME = "longbridge.models.";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CorpUserVerificationRepo corpUserVerificationRepo;
    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private CorporateRepo corporateRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SettingsService configService;

    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VerificationService verificationService;

    private final Locale locale = LocaleContextHolder.getLocale();





    @Override
    @Verifiable(operation = "UPDATE_ACCOUNT_PERMISSION_FROM_CORPORATE_ADMIN", description = "Update corporate user account permission", type = UserType.CORPORATE)
    public String updateAccountPermissionsFromCorporateAdmin(CorporateUserDTO corporateUserDTO) {

        try {
            String message = verificationService.add(corporateUserDTO,"UPDATE_ACCOUNT_PERMISSION_FROM_CORPORATE_ADMIN","Update corporate user account permission", UserType.OPERATIONS);
            logger.info("User account permissions updated successfully");
            return message;
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            logger.error("Failed to update user account permissions",e);
            throw new InternetBankingException(messageSource.getMessage("accountpermission.update.failure", null, locale), e);
        }
    }


    @Override
    public String changeStatusFromCorporateAdmin(Long id)  {
        CorporateUser corporateUser = corporateUserRepo.findById(id).get();

        if ("I".equals(corporateUser.getCorporate().getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }

        try {
            CorporateUserDTO corporateUserDTO = corporateUserService.convertEntityToDTO(corporateUser);
            //entityManager.detach(corporateUser);

            String oldStatus = corporateUserDTO.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            corporateUserDTO.setStatus(newStatus);
            saveInit(corporateUserDTO, "UPDATE_CORP_USER_STATUS", "Change corporate user activation status" );
            return messageSource.getMessage("success.user.status", null, locale);
        }catch (VerificationInterruptedException ib){
            return ib.getMessage();
        }catch (VerificationException e){
            logger.error(e.getMessage(),e);
            throw new InternetBankingException(messageSource.getMessage("failed.user.status", null, locale));
        }catch (InternetBankingException ibe){
            logger.error(ibe.getMessage());
            throw ibe;
        }
    }

    @Override
    @Verifiable(operation = "ADD_INITIATOR_FROM_CORPORATE_ADMIN", description = "Add an initiator by corporate Admin", type = UserType.CORPORATE)
    public void addInitiator(CorporateUserDTO userDTO, String operation, String description) throws VerificationException {
        saveInit(userDTO, operation, description);
    }

    @Override
    @Verifiable(operation = "UPDATE_USER_FROM_CORPORATE_ADMIN", description = "Edit an initiator by corporate Admin", type = UserType.CORPORATE)
    public void saveInitiator(CorporateUserDTO userDTO, String operation, String description) throws VerificationException {
        saveInit(userDTO, operation, description);
    }
    @Transactional
    void saveInit(CorporateUserDTO userDTO, String operation, String description)throws VerificationException{



        try {
            if (userDTO.getStatus() == null){
                userDTO.setStatus("A");
            }

            if(userDTO.getRoleId() != null){
                Role role = roleRepo.findById(Long.parseLong(userDTO.getRoleId())).get();
                userDTO.setRole(role.getName());
            }

            if(userDTO.getCorporateId() != null){
                Corporate corporate = corporateRepo.findById(Long.parseLong(userDTO.getCorporateId())).get();
                userDTO.setCorporateName(corporate.getName());
            }






            CorporateUser user = corporateUserService.convertDTOToEntity(userDTO);


            String entityName = userDTO.getClass().getSimpleName();
            if (user.getId() != null) {
                Long id = user.getId();

                CorpUserVerification pendingVerification = corpUserVerificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    throw new InternetBankingException("User has changes pending for verification. Approve or " +
                            "decline the changes before making another one.");
                }
            }


            if(CorpUserType.AUTHORIZER.equals(userDTO.getCorpUserType()) && userDTO.getCorporateRoleId()!=null){
                saveAuth(userDTO, operation, description);
            }

            CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            CorporateUser doneBy = corporateUserRepo.findByUserName(principal.getUsername());


            CorpUserVerification corpUserVerification = new CorpUserVerification();
            corpUserVerification.setEntityName(entityName);
            corpUserVerification.setInitiatedOn(new Date());
            corpUserVerification.setInitiatedBy(doneBy.getUserName());
//            corpUserVerification.setCorpUserType(CorpUserType.AUTHORIZER);
            corpUserVerification.setCorpId(Long.parseLong(userDTO.getCorporateId()));
            corpUserVerification.setOperation(operation);
            corpUserVerification.setDescription(description);
            ObjectMapper mapper = new ObjectMapper();
            corpUserVerification.setOriginalObject(mapper.writeValueAsString(userDTO));


            ObjectMapper prettyMapper = new ObjectMapper();

            if (userDTO instanceof PrettySerializer) {
                JsonSerializer<Object> serializer = ((PrettySerializer) (userDTO)).getSerializer();

                SimpleModule module = new SimpleModule();
                module.addSerializer(userDTO.getClass(), serializer);
                prettyMapper.registerModule(module);
                logger.debug("Registering Pretty serializer for " + user.getClass().getName());
            }

            if (user.getId() != null) {
                Long id = user.getId();

                Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    // found pending verification
                    throw new InternetBankingException("User has pending action waiting for verification");
                }

                CorporateUser originalEntity = entityManager.find(CorporateUser.class, id);
                CorporateUserDTO cpUserDTO = corporateUserService.convertEntityToDTO(originalEntity);



                corpUserVerification.setBeforeObject(prettyMapper.writeValueAsString(cpUserDTO));
                corpUserVerification.setEntityId(user.getId());
            }

            corpUserVerification.setAfterObject(prettyMapper.writeValueAsString(userDTO));


            corpUserVerification.setStatus(VerificationStatus.PENDING);

            logger.info("verification >>>>  " + corpUserVerification);
            corpUserVerificationRepo.save(corpUserVerification);

            logger.info(entityName + " has been saved for verification");

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            throw new VerificationInterruptedException(description + " has gone for verification");

        }catch (JsonProcessingException e){
            logger.error(e.getMessage(), e);
            throw new VerificationException(e.getMessage(),e);
        }
    }

    @Override
    @Verifiable(operation = "ADD_AUTHORIZER_FROM_CORPORATE_ADMIN", description = "Add an authorizer by corporate Admin", type = UserType.CORPORATE)
    public void addAuthorizer(CorporateUserDTO userDTO, String operation, String description) throws VerificationException {
        saveAuth(userDTO, operation, description);
    }

    @Override
    @Verifiable(operation = "UPDATE_USER_FROM_CORPORATE_ADMIN", description = "Edit an authorizer by corporate Admin", type = UserType.CORPORATE)
    public void saveAuthorizer(CorporateUserDTO userDTO, String operation, String description) throws VerificationException {
        saveAuth(userDTO, operation, description);
    }


    @Transactional
    void saveAuth(CorporateUserDTO userDTO, String operation, String description) throws VerificationException {
        try {

            if (userDTO.getStatus() == null){
                userDTO.setStatus("A");
            }

            if(userDTO.getRoleId() != null){
                Role role = roleRepo.findById(Long.parseLong(userDTO.getRoleId())).get();
                userDTO.setRole(role.getName());
            }

            if(userDTO.getCorporateId() != null){
                Corporate corporate = corporateRepo.findById(Long.parseLong(userDTO.getCorporateId())).get();
                userDTO.setCorporateName(corporate.getName());
            }


            CorporateUser user = corporateUserService.convertDTOToEntity(userDTO);


            String entityName = userDTO.getClass().getSimpleName();
            if (user.getId() != null) {
                Long id = user.getId();

                Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    logger.info("Found entity with pending verification");
                    throw new InternetBankingException("User has changes pending for verification. Approve or " +
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
            verification.setOriginalObject(mapper.writeValueAsString(userDTO));


            ObjectMapper prettyMapper = new ObjectMapper();

            if (userDTO instanceof PrettySerializer) {
                JsonSerializer<Object> serializer = ((PrettySerializer) (userDTO)).getSerializer();

                SimpleModule module = new SimpleModule();
                module.addSerializer(userDTO.getClass(), serializer);
                prettyMapper.registerModule(module);
                logger.debug("Registering Pretty serializer for " + user.getClass().getName());
            }

            if (user.getId() != null) {
                Long id = user.getId();

                Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                        id, VerificationStatus.PENDING);
                if (pendingVerification != null) {
                    // found pending verification
                    throw new InternetBankingException(entityName + " has pending verification");
                }

                CorporateUser originalEntity = entityManager.find(CorporateUser.class, id);
                CorporateUserDTO cpUserDTO = corporateUserService.convertEntityToDTO(originalEntity);



                verification.setBeforeObject(prettyMapper.writeValueAsString(cpUserDTO));
                verification.setEntityId(user.getId());
            }

            verification.setAfterObject(prettyMapper.writeValueAsString(userDTO));


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
    @Transactional
    public String decline(CorpUserVerificationDTO dto) throws VerificationException {

        CorpUserVerification corpUserVerification = corpUserVerificationRepo.findById(dto.getId()).get();
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
    public boolean isPendingVerification(Long entityId, String entityName) {

        Verification pendingVerification = verificationRepo.findFirstByEntityNameAndEntityIdAndStatus(entityName,
                entityId, VerificationStatus.PENDING);
        return pendingVerification != null;
    }

    @Override
    @Transactional
    public String verify(CorpUserVerificationDTO dto) throws VerificationException {

        logger.info(">>>>>>" + dto);
        CorpUserVerification corpUserVerification = corpUserVerificationRepo.findById(dto.getId()).get();
        String verifiedBy = getCurrentUserName();
        if(verifiedBy.equals(corpUserVerification.getInitiatedBy())){
            throw new VerificationException("You cannot verify what you initiated");
        }

        if (!VerificationStatus.PENDING.equals(corpUserVerification.getStatus())) {
            throw new VerificationException("Verification is not pending for the operation");
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            Class<?> clazz  = Class.forName(CorporateUserDTO.class.getName());
            Object object = mapper.readValue(corpUserVerification.getOriginalObject(), clazz);
            logger.info("Trying to merge..{}",object.toString());
            CorporateUser corporateUser = corporateUserService.convertDTOToEntity((CorporateUserDTO) object);
            entityManager.merge(corporateUser);
            corpUserVerification.setId(dto.getId());
            corpUserVerification.setVersion(dto.getVersion());
            corpUserVerification.setVerifiedBy(getCurrentUserName());
            corpUserVerification.setVerifiedOn(new Date());
            corpUserVerification.setComments(dto.getComments());
            corpUserVerification.setStatus(VerificationStatus.APPROVED);
            corpUserVerificationRepo.save(corpUserVerification);
            postCorporateUserActivation(dto);
            notifyInitiator(corpUserVerification);
        }
        catch (Exception e) {
            logger.error("Error verifying operation");
            throw new InternetBankingException("Failed to verify the operation",e);
        }
        return messageSource.getMessage("verification.verify", null, locale);
    }

    @Async
    void notifyInitiator(CorpUserVerification corpUserVerification){

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User verifiedBy = principal.getUser();

        String initiator = corpUserVerification.getInitiatedBy();

        User initiatedBy = corporateUserRepo.findFirstByUserNameIgnoreCase(initiator);

        if(initiatedBy!=null) {
            if (initiatedBy.getEmail() != null) {
                String initiatorName = initiatedBy.getFirstName()+" "+initiatedBy.getLastName();
                String verifierName = verifiedBy.getFirstName()+" "+verifiedBy.getLastName();
                Date date = corpUserVerification.getVerifiedOn();
                String operation = corpUserVerification.getDescription();
                String comments = corpUserVerification.getComments();
                String status = corpUserVerification.getStatus().name();


                Context context = new Context();
                context.setVariable("fullName",initiatorName);
                context.setVariable("verifier",verifierName);
                context.setVariable("operation",operation);
                context.setVariable("date",date);
                context.setVariable("comments",comments);
                context.setVariable("status",status);


                Email email = new Email.Builder()
                        .setRecipient(initiatedBy.getEmail())
                        .setSubject(messageSource.getMessage("verification.subject", null, locale))
                        .setTemplate("mail/verification")
                        .build();

                mailService.sendMail(email,context);
            }
        }
    }

    @Override
    public CorpUserVerificationDTO getVerification(Long id) {
        return convertEntityToDTO(corpUserVerificationRepo.findById(id).get());
    }


    @Override
    public CorpUserVerificationDTO convertEntityToDTO(CorpUserVerification corpUserVerification) {
        return modelMapper.map(corpUserVerification, CorpUserVerificationDTO.class);
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
    public int getTotalNumberPending() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CorporateUser doneBy = (CorporateUser) principal.getUser();
        return corpUserVerificationRepo.countByCorpIdAndStatus(doneBy.getCorporate().getId(), VerificationStatus.PENDING);
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
        return new PageImpl<>(dtOs, pageable, t);
    }

    @Override
    public Page<CorpUserVerificationDTO> getRequestsByCorpId(Long corpId, Pageable pageable) {
        Page<CorpUserVerification> page = corpUserVerificationRepo.findByCorpIdOrderByStatusDesc(corpId, pageable);
        List<CorpUserVerificationDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t =page.getTotalElements();
        return new PageImpl<>(dtOs, pageable, t);
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

    private void postCorporateUserActivation(CorpUserVerificationDTO corpUserVerificationDTO) throws IOException {

        CorpUserVerification corpUserVerification  =corpUserVerificationRepo.findById(corpUserVerificationDTO.getId()).get();
        logger.info(">>>>>>>>>>" + corpUserVerification.getOperation());
        if(corpUserVerification.getOperation().equals("UPDATE_CORP_USER_STATUS")){

            logger.info("Inside Advisor for Post Corporate user activation...");

            CorporateUser user = corporateUserRepo.findById(corpUserVerification.getEntityId()).get();
            entityManager.detach(user);
            ObjectMapper objectMapper = new ObjectMapper();
            CorporateUserDTO corpUserDTO = objectMapper.readValue(corpUserVerification.getOriginalObject(),CorporateUserDTO.class);
            CorporateUser cUser = corporateUserService.convertDTOToEntity(corpUserDTO);
            if("A".equals(cUser.getStatus())){
                logger.info("Corp user status is A");
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveCorporatePassword(user);
                corporateUserRepo.save(user);
                sendPostActivateMessage(cUser, password);
            }
            else{
                corporateUserRepo.save(user);
            }
        }

        if(corpUserVerification.getOperation().equals("ADD_INITIATOR_FROM_CORPORATE_ADMIN")) {
            logger.info("Adding Corporate User");
            ObjectMapper objectMapper = new ObjectMapper();
            CorporateUserDTO corpUserDTO = objectMapper.readValue(corpUserVerification.getOriginalObject(),CorporateUserDTO.class);
            CorporateUser cUser = corporateUserService.convertDTOToEntity(corpUserDTO);
            logger.info("Corporate User >>"+ cUser);
            createUserOnEntrustAndSendCredentials(cUser);
        }

        if(corpUserVerification.getOperation().equals("UPDATE_USER_FROM_CORPORATE_ADMIN")) {
            logger.info("Updating Corporate User");
            ObjectMapper objectMapper = new ObjectMapper();
            CorporateUserDTO corpUserDTO = objectMapper.readValue(corpUserVerification.getOriginalObject(),CorporateUserDTO.class);
            CorporateUser cpUser = corporateUserService.convertDTOToEntity(corpUserDTO);
            corporateUserRepo.save(cpUser);
        }

    }

    public void createUserOnEntrustAndSendCredentials(CorporateUser corporateUser) {
        CorporateUser user = corporateUserRepo.findFirstByUserNameIgnoreCase(corporateUser.getUserName());
        logger.info("Corporate User >>"+ user);
        if (user != null) {

            if ("".equals(user.getEntrustId()) || user.getEntrustId() == null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
                String entrustId = user.getUserName();
                String group = configService.getSettingByName("DEF_ENTRUST_CORP_GRP").getValue();

                if (setting != null && setting.isEnabled()) {
                    if ("YES".equalsIgnoreCase(setting.getValue())) {
                        boolean result = securityService.createEntrustUser(entrustId, group, fullName, true);
                        if (!result) {
                            throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));

                        }
                        boolean contactResult = securityService.addUserContacts(user.getEmail(), user.getPhoneNumber(), true, entrustId, group);
                        if (!contactResult) {
                            logger.error("Failed to add user contacts on Entrust");
                            securityService.deleteEntrustUser(entrustId, group);
                            throw new EntrustException(messageSource.getMessage("entrust.contact.failure", null, locale));
                        }
                    }

                    user.setEntrustId(entrustId);
                    user.setEntrustGroup(group);
                    String password = passwordPolicyService.generatePassword();
                    user.setPassword(passwordEncoder.encode(password));
                    user.setExpiryDate(new Date());
                    user.setIsFirstTimeLogon("Y");
                    passwordPolicyService.saveCorporatePassword(user);
                    corporateUserRepo.save(user);
                    sendCreationCredentials(user,password);



                }
            }
        }
    }

    private void sendCreationCredentials(CorporateUser user, String password) {

        final String url =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/corporate";

        String fullName = user.getFirstName() + " " + user.getLastName();
        Corporate corporate = user.getCorporate();

        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("username", user.getUserName());
        context.setVariable("password", password);
        context.setVariable("corporateId", corporate.getCorporateId());
        context.setVariable("url", url);


        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                .setTemplate("mail/corpcreation")
                .build();
        mailService.sendMail(email, context);
    }

    @Async
    public void sendPostActivateMessage(CorporateUser user, String password) {

        final String url =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/corporate";
        String fullName = user.getFirstName() + " " + user.getLastName();
        Corporate corporate = user.getCorporate();

        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("username", user.getUserName());
        context.setVariable("password", password);
        context.setVariable("corporateId", corporate.getCorporateId());
        context.setVariable("url", url);

        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("corporate.customer.activation.subject", null, locale))
                .setTemplate("mail/corpactivation")
                .build();

        mailService.sendMail(email, context);

    }

}