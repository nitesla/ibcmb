package longbridge.services.implementations;

import longbridge.dtos.CorpCorporateUserDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.*;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateRoleRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.RoleRepo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/4/2017.
 */
@Service
public class CorporateUserServiceImpl implements CorporateUserService {

    private CorporateUserRepo corporateUserRepo;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityService securityService;

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
        return corporateUserRepo.findFirstByUserNameIgnoreCase(username);
    }

    @Override
    public CorporateUser getUserByNameAndCorpCif(String username, String cif) {
        return corporateUserRepo.findFirstByUserNameIgnoreCaseAndCorporate_CustomerIdIgnoreCase(username, cif);
    }

    @Override
    public CorporateUser getUserByNameAndCorporateId(String username, String corporateId) {
        return corporateUserRepo.findFirstByUserNameIgnoreCaseAndCorporate_CorporateIdIgnoreCase(username, corporateId);
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


        CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());

        Corporate corporate = corporateUser.getCorporate();
        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }

        if (!user.getEmail().equals(corporateUser.getEmail())) {
            corporateUser = corporateUserRepo.findFirstByCorporateAndEmailIgnoreCase(corporate, user.getEmail());
            if (corporateUser != null && !user.getId().equals(corporateUser.getId())) {
                throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
            }
        }

        corporateUser = corporateUserRepo.findOne(user.getId());

        try {
            entityManager.detach(corporateUser);
            corporateUser.setEmail(user.getEmail());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            if(user.isAdmin()) {
                corporateUser.setCorpUserType(CorpUserType.ADMIN);
            }


            if (user.getRoleId() != null) {
                Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
                corporateUser.setRole(role);
            }

            corporateUserRepo.save(corporateUser);
            return messageSource.getMessage("user.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
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
        Corporate corporate = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
        corporateUser = corporateUserRepo.findFirstByCorporateAndEmailIgnoreCase(corporate, user.getEmail());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
        }
        try {
            corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            if(user.isAdmin()) {
                corporateUser.setCorpUserType(CorpUserType.ADMIN);
            }
            Corporate corp = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);
            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw e;
            }
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }



    public void createUserOnEntrustAndSendCredentials(CorporateUser corporateUser) {
        CorporateUser user = corporateUserRepo.findFirstByUserName(corporateUser.getUserName());
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
                    Corporate corporate = user.getCorporate();
                    String password = passwordPolicyService.generatePassword();
                    user.setPassword(passwordEncoder.encode(password));
                    user.setExpiryDate(new Date());
                    passwordPolicyService.saveCorporatePassword(user);
                    corporateUserRepo.save(user);


                    try {
                        Email email = new Email.Builder()
                                .setRecipient(user.getEmail())
                                .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                                .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), fullName, user.getUserName(), password, corporate.getCorporateId()))
                                .build();
                        mailService.send(email);
                    } catch (MailException me) {
                        logger.error("Failed to send creation mail to {}", user.getEmail(), me);
                    }
                }
            }
        }
    }


    @Override
    public void addCorporateUserToAuthorizerRole(CorporateUser corporateUser, Long corpRoleId) {

        CorporateRole corporateRole = corporateRoleRepo.findOne(corpRoleId);
        corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
        corporateRole.getUsers().add(corporateUser);
        corporateRoleRepo.save(corporateRole);
    }

    @Override
    public void changeCorporateUserAuthorizerRole(CorporateUser corporateUser, CorporateRole role, Long newRoleId) {
        CorporateRole oldRole = corporateRoleRepo.findOne(role.getId());
        CorporateRole newRole = corporateRoleRepo.findOne(newRoleId);
        corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
        oldRole.getUsers().remove(corporateUser);
        corporateRoleRepo.save(oldRole);
        newRole.getUsers().add(corporateUser);
        corporateRoleRepo.save(newRole);
    }

    @Override
    @Transactional
    public String addCorpUserFromCorporateAdmin(CorpCorporateUserDTO user) throws InternetBankingException {

        CorporateUser corporateUser = corporateUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }

        Corporate corporate = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
        corporateUser = corporateUserRepo.findFirstByCorporateAndEmailIgnoreCase(corporate, user.getEmail());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
        }

        try {
            corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            corporateUser.setStatus("A");
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corp);
            corporateUserRepo.save(corporateUser);
            createUserOnEntrustAndSendCredentials(corporateUser);

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
    @Verifiable(operation = "UPDATE_CORP_USER_STATUS", description = "Change corporate user activation status")
    public String changeActivationStatus(Long userId) throws InternetBankingException {

        CorporateUser user = corporateUserRepo.findOne(userId);
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }
        try {
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

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }


    public void sendPostCreationMessage(User user, String fullName, String username, String password, String corporateId) {
        CorporateUser corporateUser = corporateUserRepo.findFirstByUserName(user.getUserName());
        if (corporateUser != null) {

            Corporate corporate = corporateUser.getCorporate();
            corporateUser.setPassword(passwordEncoder.encode(password));
            corporateUser.setExpiryDate(new Date());
            passwordPolicyService.saveCorporatePassword(corporateUser);
            try {
                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), fullName, username, password, corporate.getCustomerId()))
                        .build();
                mailService.send(email);
            } catch (MailException me) {
                logger.error("Failed to send creation mail to {}", user.getEmail(), me);
            }
            corporateUserRepo.save(corporateUser);
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
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("corp.customer.password.reset.subject", null, locale))
                .setBody(String.format(messageSource.getMessage("corp.customer.password.reset.message", null, locale), args))
                .build();
        mailService.send(email);
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

        CorporateUser user = corporateUserRepo.findOne(userId);
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }
        try {
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ("I".equals(oldStatus) && "A".equals(newStatus)) {
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

        CorporateUser user = corporateUserRepo.findOne(userId);
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }
        try {
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
    public String resetCorpPassword(Long userId) throws PasswordException {

        CorporateUser user = corporateUserRepo.findOne(userId);
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }

        try {
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
                    securityService.deleteEntrustUser(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
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
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }

        try {
            failedLoginService.unLockUser(user);
            return messageSource.getMessage("unlock.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("unlock.failure", null, locale), e);

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
            corporateUserRepo.save(corporateUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    @Override
    public boolean userExists(String username) {
        return corporateUserRepo.existsByUserNameIgnoreCase(username);
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
            corporateUserRepo.save(corporateUser);
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

    private String getUserDesignation(CorporateUser corporateUser) {

        List<CorporateRole> roles = corporateRoleRepo.findByCorporate(corporateUser.getCorporate());
        for (CorporateRole corporateRole : roles) {
            if (corporateRoleRepo.countInRole(corporateRole, corporateUser) > 0) {
                if (corporateRole.getRank() != null) {
                    return corporateRole.getName() + " " + corporateRole.getRank();
                } else {
                    return corporateRole.getName();
                }
            }
        }
        return null;
    }

    public CorporateRole getCorporateUserAuthorizerRole(CorporateUser user){

        Corporate corporate = user.getCorporate();
        List<CorporateRole> roles = corporateRoleRepo.findByCorporate(corporate);

        for (CorporateRole corporateRole : roles) {
            if (corporateRoleRepo.countInRole(corporateRole, user) > 0) {
                return corporateRole;
            }
        }
        return  null;
    }

    @Override
    public CorporateUserDTO convertEntityToDTO(CorporateUser corporateUser) {

        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        corporateUserDTO.setCorporateId(corporateUser.getCorporate().getId().toString());
        corporateUserDTO.setCorporateName(corporateUser.getCorporate().getName());
        corporateUserDTO.setCorporateType(corporateUser.getCorporate().getCorporateType());
        corporateUserDTO.setId(corporateUser.getId());
        corporateUserDTO.setUserName(corporateUser.getUserName());
        corporateUserDTO.setFirstName(corporateUser.getFirstName());
        corporateUserDTO.setLastName(corporateUser.getLastName());
        corporateUserDTO.setPhoneNumber(corporateUser.getPhoneNumber());
        corporateUserDTO.setStatus(corporateUser.getStatus());
        corporateUserDTO.setEmail(corporateUser.getEmail());
        corporateUserDTO.setEntrustId(corporateUser.getEntrustId());
        corporateUserDTO.setEntrustGroup(corporateUser.getEntrustGroup());
        corporateUserDTO.setIsFirstTimeLogon(corporateUser.getIsFirstTimeLogon());
        corporateUserDTO.setRoleId(corporateUser.getRole().getId().toString());
        corporateUserDTO.setRole(corporateUser.getRole().getName());
        corporateUserDTO.setCorpUserType(corporateUser.getCorpUserType());
        if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())){
            corporateUserDTO.isAuthorizer();
        }
        if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())){
            CorporateRole corporateRole = getCorporateUserAuthorizerRole(corporateUser);
            if (corporateRole != null) {
                corporateUserDTO.setCorporateRoleId(corporateRole.getId());
                corporateUserDTO.setCorporateRole(corporateRole.getName() + " " + corporateRole.getRank());
            }
        }

        if (corporateUser.getCreatedOnDate() != null) {
            corporateUserDTO.setCreatedOnDate(DateFormatter.format(corporateUser.getCreatedOnDate()));
        }
        if (corporateUser.getLastLoginDate() != null) {
            corporateUserDTO.setLastLoginDate(DateFormatter.format(corporateUser.getLastLoginDate()));
        }

        return corporateUserDTO;
    }

    @Override
    public CorporateUser convertDTOToEntity(CorporateUserDTO corporateUserDTO) {
        //CorporateUser corporateUser = modelMapper.map(corporateUserDTO, CorporateUser.class);
        CorporateUser corporateUser = new CorporateUser();
        corporateUser.setId(corporateUserDTO.getId());
        corporateUser.setUserName(corporateUserDTO.getUserName());
        corporateUser.setFirstName(corporateUserDTO.getFirstName());
        corporateUser.setLastName(corporateUserDTO.getLastName());
        corporateUser.setPhoneNumber(corporateUserDTO.getPhoneNumber());
        corporateUser.setStatus(corporateUserDTO.getStatus());
        corporateUser.setEmail(corporateUserDTO.getEmail());
        corporateUser.setEntrustId(corporateUserDTO.getEntrustId());
        corporateUser.setEntrustGroup(corporateUserDTO.getEntrustGroup());
        corporateUser.setUserType(UserType.CORPORATE);
        corporateUser.setIsFirstTimeLogon(corporateUserDTO.getIsFirstTimeLogon());
        Role role = roleRepo.findOne(Long.parseLong(corporateUserDTO.getRoleId()));
        corporateUser.setRole(role);
        corporateUser.setCorpUserType(corporateUserDTO.getCorpUserType());
        if (corporateUserDTO.getCreatedOnDate() == null) {
            corporateUser.setCreatedOnDate(new Date());
        }
        Corporate corporate = corporateRepo.getOne(Long.parseLong(corporateUserDTO.getCorporateId()));
        corporateUser.setCorporate(corporate);
//        if (corporateUserDTO.getCorporateRoleId() != null){
//            corporateUser.set
//        }
        return corporateUser;
    }

    @Override
    public Page<CorporateUserDTO> getUsers(Long corpId, Pageable pageDetails) {

        Page<CorporateUser> page = corporateUserRepo.findByCorporateId(corpId, pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    @Override
    public Page<CorporateUserDTO> getUsers(Pageable pageDetails) {

        Page<CorporateUser> page = corporateUserRepo.findAll(pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }
    @Override
    public void increaseNoOfTokenAttempt(CorporateUser corporateUser) {
        if (corporateUser.getNoOfTokenAttempts() == null) {
            corporateUser.setNoOfTokenAttempts(0);
        } else {
            corporateUser.setNoOfTokenAttempts(corporateUser.getNoOfTokenAttempts() + 1);
        }
        corporateUserRepo.save(corporateUser);
    }
    @Override
    public void resetNoOfTokenAttempt(CorporateUser corporateUser) {
        corporateUser.setNoOfTokenAttempts(0);
        corporateUserRepo.save(corporateUser);
    }

    @Override
    public CorporateUser getUserByCifAndEmailIgnoreCase(Corporate corporate, String email) {
        CorporateUser corporateUser = corporateUserRepo.findFirstByCorporateAndEmailIgnoreCase(corporate, email);
        return corporateUser;
    }

    @Override
    @Transactional
    public String addAuthorizer(CorporateUserDTO user) throws InternetBankingException {
        try {
            CorporateUser corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);
            if (user.isAuthorizer()){
                if (user.getCorporateRoleId() != null) {
                    addCorporateUserToAuthorizerRole(corporateUser, user.getCorporateRoleId());
                }
            }
            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        }catch (InternetBankingException ibe){
            throw ibe;
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new InternetBankingException(e.getMessage());
        }
    }




    @Override
    public String addInitiator(CorporateUserDTO user) throws InternetBankingException {
        try {
            CorporateUser corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            corporateUser.setCorpUserType(CorpUserType.INITIATOR);
            corporateUser.setEntrustId(user.getUserName());
            SettingDTO settingDTO = configService.getSettingByName("DEF_ENTRUST_CORP_GRP");
            corporateUser.setEntrustGroup(settingDTO.getValue());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);

            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        }catch (InternetBankingException ibe){
            throw ibe;
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new InternetBankingException(e.getMessage());
        }
    }

    @Override
    public String updateUserFromCorpAdmin(CorporateUserDTO user) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);

            if (user.isAuthorizer()){
                if (user.getCorporateRoleId() != null) {
                    CorporateRole corporateRole = getCorporateUserAuthorizerRole(corporateUser);
                    if(!user.getCorporateRoleId().equals(corporateRole.getId())){
                        changeCorporateUserAuthorizerRole(corporateUser, corporateRole, user.getCorporateRoleId());
                        addCorporateUserToAuthorizerRole(corporateUser, user.getCorporateRoleId());
                    }
                }
            }

            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw e;
            }
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    @Override
    public String changeActivationStatusFromCorpAdmin(Long id) throws InternetBankingException {
        CorporateUser corporateUser = corporateUserRepo.findOne(id);

        if ("I".equals(corporateUser.getCorporate().getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }

        try {
            CorporateUserDTO corporateUserDTO = convertEntityToDTO(corporateUser);
            String oldStatus = corporateUserDTO.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            corporateUser.setStatus(newStatus);
            corporateUserRepo.save(corporateUser);
            return messageSource.getMessage("user.update.success", null, locale);

        }catch (InternetBankingException ibe){
            throw ibe;
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new InternetBankingException(messageSource.getMessage("corporate.update.failure", null, locale), e);
        }
    }
}
