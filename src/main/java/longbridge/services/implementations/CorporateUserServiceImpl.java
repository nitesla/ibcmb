package longbridge.services.implementations;

import longbridge.dtos.CorpCorporateUserDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.FailedLoginService;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.Verifiable;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Created by Fortune on 4/4/2017.
 */
@Service
public class CorporateUserServiceImpl implements CorporateUserService {

    private CorporateUserRepo corporateUserRepo;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityService securityService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private ConfigurationService configService;


    @Autowired
    private CodeService codeService;

    @Autowired
    private CorporateRepo corporateRepo;

    @Autowired
    private FailedLoginService failedLoginService;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    CorporateRoleRepo corporateRoleRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorporateUserServiceImpl(CorporateUserRepo corporateUserRepo, BCryptPasswordEncoder passwordEncoder, SecurityService securityService) {
        this.corporateUserRepo = corporateUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public CorporateUserDTO getUser(Long id) {
        CorporateUser corporateUser = corporateUserRepo.findOne(id);
        return convertEntityToDTO(corporateUser);
    }

    @Override
    public CorporateUserDTO getUserDTOByName(String name) {
        CorporateUser corporateUser = this.corporateUserRepo.findFirstByUserName(name);
        return convertEntityToDTO(corporateUser);
    }

    @Override
    public CorporateUser getUserByName(String username) {
        return corporateUserRepo.findByUserName(username);
    }


    @Override
    public Iterable<CorporateUserDTO> getUsers(Corporate corporate) {

        Iterable<CorporateUser> corporateUserDTOList = corporateUserRepo.findAll();
        return convertEntitiesToDTOs(corporateUserDTOList);
    }

    @Override
    public Iterable<CorporateUser> getUsers() {
        return corporateUserRepo.findAll();
    }

    @Override
    public Iterable<CorporateUserDTO> getUsers(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        List<CorporateUser> users = corporate.getUsers();
        return convertEntitiesToDTOs(users);
    }

    @Override
    @Verifiable(operation = "UPDATE_CORPORATE_USER", description = "Updating Corporate User")
    public String updateUser(CorporateUserDTO user) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
            entityManager.detach(corporateUser);
            corporateUser.setEmail(user.getEmail());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            if (user.getRoleId() != null) {
                Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
                corporateUser.setRole(role);
            }
            corporateUserRepo.save(corporateUser);
            return messageSource.getMessage("user.update.success", null, locale);
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.update.failure", null, locale), e);

        }
    }

    @Override
    @Transactional
    @Verifiable(operation = "ADD_CORPORATE_USER", description = "Adding Corporate User")
    public String addUser(CorporateUserDTO user) throws InternetBankingException {

        CorporateUser corporateUser = corporateUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }
        try {
            corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setAdmin(user.isAdmin());
            corporateUser.setCreatedOnDate(new Date());
            String password = passwordPolicyService.generatePassword();
            corporateUser.setPassword(passwordEncoder.encode(password));
            corporateUser.setExpiryDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corporate = new Corporate();
            corporate.setId(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corporate);
            passwordPolicyService.saveCorporatePassword(corporateUser);
            corporateUserRepo.save(corporateUser);
            createUserOnEntrust(corporateUser);
            String fullName = corporateUser.getFirstName() + " " + corporateUser.getLastName();
            sendPostCreationMessage(corporateUser, fullName, user.getUserName(), password, corporateUser.getCorporate().getCustomerId());
            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw e;
            }
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    private void createUserOnEntrust(CorporateUser user) {

        if ("".equals(user.getEntrustId()) || user.getEntrustId() == null) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            String entrustId = user.getUserType().toString() + "_" + user.getUserName();

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    boolean result = securityService.createEntrustUser(entrustId, fullName, true);
                    if (!result) {
                        throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));

                    }
                    boolean contactResult = securityService.addUserContacts(user.getEmail(), user.getPhoneNumber(), true, entrustId);
                    if (!contactResult) {
                        logger.error("Failed to add user contacts on Entrust");
                        securityService.deleteEntrustUser(entrustId);
                        throw new EntrustException(messageSource.getMessage("entrust.contact.failure", null, locale));


                    }
                }
                user.setEntrustId(entrustId);
                corporateUserRepo.save(user);

            }
        }
    }


    @Override
    @Transactional
    public String addUserFromCorporateAdmin(CorpCorporateUserDTO user) throws InternetBankingException {

        CorporateUser corporateUser = corporateUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }
        try {
            corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            String password = passwordPolicyService.generatePassword();
            corporateUser.setPassword(passwordEncoder.encode(password));
            corporateUser.setExpiryDate(new Date());
            corporateUser.setStatus("A");
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corporate = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corporate);
            passwordPolicyService.saveCorporatePassword(corporateUser);
            corporateUserRepo.save(corporateUser);
            String fullName = user.getFirstName() + " " + user.getLastName();
            createUserOnEntrust(corporateUser);
            sendPostCreationMessage(corporateUser, fullName, user.getUserName(), password, corporateUser.getCorporate().getCustomerId());

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    @Verifiable(operation = "CORP_USER_STATUS", description = "Change corporate user activation status")
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            CorporateUser user = corporateUserRepo.findOne(userId);
            entityManager.detach(user);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null) || ("I".equals(oldStatus)) && "A".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveCorporatePassword(user);
                CorporateUser corpUser = corporateUserRepo.save(user);
                sendActivationMessage(corpUser, fullName, user.getUserName(), password, user.getCorporate().getCustomerId());
            } else {
                user.setStatus(newStatus);
                corporateUserRepo.save(user);
            }

            logger.info("Corporate user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }

    @Async
    private void sendPostCreationMessage(User user, String... args) {

        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), args))
                    .build();
            mailService.send(email);
        } catch (MailException me) {
            logger.error("Failed to send creation mail to {}", user.getEmail(), me);
        }

    }


    @Async
    public void sendPostActivateMessage(User user, String... args) {
        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("corporate.customer.reactivation.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("corporate.customer.reactivation.message", null, locale), args))
                    .build();
            mailService.send(email);
        } catch (MailException me) {
            logger.error("Failed to send activation mail to {}", user.getEmail(), me);
        }
    }

    @Async
    public void sendPostPasswordResetMessage(User user, String... args) {
        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("corp.customer.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("corp.customer.password.reset.message", null, locale), args))
                    .build();
            mailService.send(email);
        } catch (MailException me) {
            logger.error("Failed to send activation mail to {}", user.getEmail(), me);
        }
    }

    @Async
    private void sendActivationMessage(User user, String... args) {
        CorporateUser corpUser = getUserByName(user.getUserName());
        try {
            if ("A".equals(corpUser.getStatus())) {

                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("corporate.customer.reactivation.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("corporate.customer.reactivation.message", null, locale), args))
                        .build();
                mailService.send(email);
            }
        } catch (MailException me) {
            logger.error("Failed to send activation mail to {}", user.getEmail(), me);
        }
    }

    @Override
    public String changeCorpActivationStatus(Long userId) throws InternetBankingException {
        try {
            CorporateUser user = corporateUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null) || ("I".equals(oldStatus)) && "A".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveCorporatePassword(user);
                CorporateUser corpUser = corporateUserRepo.save(user);
                sendActivationMessage(corpUser, fullName, user.getUserName(), password, user.getCorporate().getCustomerId());
            } else {
                user.setStatus(newStatus);
                corporateUserRepo.save(user);
            }

            logger.info("Corporate user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);


        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }

    @Override
    public String resetPassword(Long userId) throws PasswordException {

        try {
            CorporateUser user = corporateUserRepo.findOne(userId);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            String fullName = user.getFirstName() + " " + user.getLastName();
            passwordPolicyService.saveCorporatePassword(user);
            corporateUserRepo.save(user);
            sendPostPasswordResetMessage(user, fullName, user.getUserName(), newPassword, user.getCorporate().getCustomerId());
            logger.info("Corporate user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }


    @Override
    public String deleteUser(Long userId) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(userId);
            corporateUserRepo.delete(userId);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(corporateUser.getUserName());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception ibe) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale));
        }
    }


    @Override
    public String unlockUser(Long id) throws InternetBankingException {

        CorporateUser user = corporateUserRepo.findOne(id);
        if (!"L".equals(user.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("user.unlocked", null, locale));
        }
        try {
            failedLoginService.unLockUser(user);
            return messageSource.getMessage("unlock.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("unlock.failure", null, locale));

        }
    }

    @Override
    public String changePassword(CorporateUser user, CustChangePassword changePassword) {

        if (!this.passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
            corporateUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            corporateUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveCorporatePassword(corporateUser);
            this.corporateUserRepo.save(corporateUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    @Override
    public String resetPassword(CorporateUser user, CustResetPassword changePassword) {

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
            corporateUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            corporateUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveCorporatePassword(corporateUser);
            this.corporateUserRepo.save(corporateUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    @Override
    public List<CorporateUserDTO> getUsersWithoutRole(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        List<CorporateUser> userNotInRole = corporateUserRepo.findUsersWithoutRole(corporate);
        return convertEntitiesToDTOs(userNotInRole);
    }


    @Override
    public boolean changeAlertPreference(CorporateUser corporateUser, AlertPref alertPreference) {
        boolean ok = false;
        try {
            if (getUser(corporateUser.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return ok;
            }
            Code code = codeService.getByTypeAndCode("ALERT_PREFERENCE", alertPreference.getCode());
            corporateUser.setAlertPreference(code);
            this.corporateUserRepo.save(corporateUser);
            logger.info("User {}'s alert preference set", corporateUser.getId());
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR OCCURRED", e);
        }
        return ok;
    }


    private List<CorporateUserDTO> convertEntitiesToDTOs(Iterable<CorporateUser> corporateUsers) {
        List<CorporateUserDTO> corporateUserDTOList = new ArrayList<>();
        for (CorporateUser corporateUser : corporateUsers) {
            CorporateUserDTO userDTO = convertEntityToDTO(corporateUser);
            userDTO.setRole(corporateUser.getRole().getName());
            userDTO.setDesignation(getUserDesignation(corporateUser));
            corporateUserDTOList.add(userDTO);
        }
        return corporateUserDTOList;
    }

    private String getUserDesignation(CorporateUser corporateUser){

        List<CorporateRole> roles = corporateRoleRepo.findByCorporate(corporateUser.getCorporate());
        for (CorporateRole corporateRole : roles) {
            if (corporateRoleRepo.countInRole(corporateRole, corporateUser)>0) {
                return corporateRole.getName()+" "+corporateRole.getRank();
            }
        }
        return null;
    }

    private CorporateUserDTO convertEntityToDTO(CorporateUser corporateUser) {

        CorporateUserDTO corporateUserDTO = modelMapper.map(corporateUser, CorporateUserDTO.class);
        corporateUserDTO.setRoleId(corporateUser.getRole().getId().toString());
        corporateUserDTO.setRole(corporateUser.getRole().getName());
        corporateUserDTO.setCorporateType(corporateUser.getCorporate().getCorporateType());
        corporateUserDTO.setCorporateName(corporateUser.getCorporate().getName());
        corporateUserDTO.setCorporateId(corporateUser.getCorporate().getId().toString());


        if (corporateUser.getCreatedOnDate() != null) {
            corporateUserDTO.setCreatedOn(DateFormatter.format(corporateUser.getCreatedOnDate()));
        }
        if (corporateUser.getLastLoginDate() != null) {
            corporateUserDTO.setLastLogin(DateFormatter.format(corporateUser.getLastLoginDate()));
        }

        return corporateUserDTO;
    }

    @Override
    public Page<CorporateUserDTO> getUsers(Long corpId, Pageable pageDetails) {

        Page<CorporateUser> page = corporateUserRepo.findByCorporateId(corpId, pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
        // TODO Auto-gene
    }


    @Override
    public Page<CorporateUserDTO> getUsers(Pageable pageDetails) {
        // TODO Auto-generated method stub

        Page<CorporateUser> page = corporateUserRepo.findAll(pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }
}
