
package longbridge.services.implementations;

import longbridge.apiLayer.models.WebhookResponse;
import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.FailedLoginService;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.CorporateWebhook;
import longbridge.utils.DateFormatter;
import longbridge.utils.Verifiable;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;

import javax.persistence.EntityManager;
import java.util.*;

import static longbridge.dtos.AccountPermissionDTO.Permission.VIEW_ONLY;
import static longbridge.models.UserAccountRestriction.RestrictionType.*;

/**
 * Created by Fortune on 4/4/2017.
 */
@Service
public class CorporateUserServiceImpl implements CorporateUserService {

    private final CorporateUserRepo corporateUserRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private SettingsService configService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private CorporateRepo corporateRepo;

    @Autowired
    private FailedLoginService failedLoginService;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CorporateRoleRepo corporateRoleRepo;


    @Autowired
    private CorporateWebhook corporateWebhook;



    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserAccountRestrictionRepo userAccountRestrictionRepo;

    @Autowired
    public CorporateUserServiceImpl(CorporateUserRepo corporateUserRepo, BCryptPasswordEncoder passwordEncoder, SecurityService securityService) {
        this.corporateUserRepo = corporateUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public CorporateUserDTO getUser(Long id) {
        CorporateUser corporateUser = corporateUserRepo.findById(id).get();
        return convertEntityToDTO(corporateUser);
    }

    @Override
    public CorporateUserDTO getUserDTOByName(String name) {
        CorporateUser corporateUser = this.corporateUserRepo.findFirstByUserNameIgnoreCase(name);
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
    public CorporateUser getUserByEmail(String email) {
        return corporateUserRepo.findByEmailIgnoreCase(email);
    }



    @Override
    public Iterable<CorporateUser> getUsers() {
        return corporateUserRepo.findAll();
    }

    @Override
    public Iterable<CorporateUserDTO> getUsers(Long corpId) {
        Corporate corporate = corporateRepo.findById(corpId).get();
        List<CorporateUser> users = corporate.getUsers();
        return convertEntitiesToDTOs(users);
    }

    @Override
    @Verifiable(operation = "UPDATE_CORPORATE_USER", description = "Updating Corporate User")
    public String updateUser(CorporateUserDTO user) throws InternetBankingException {


        CorporateUser corporateUser = corporateUserRepo.findById(user.getId()).get();

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

        corporateUser = corporateUserRepo.findById(user.getId()).get();

        try {
            entityManager.detach(corporateUser);
            corporateUser.setEmail(user.getEmail());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setAdmin(user.isAdmin());
            if (user.isAdmin()) {
                corporateUser.setCorpUserType(CorpUserType.ADMIN);

            } else if (!CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())) {
                corporateUser.setCorpUserType(CorpUserType.INITIATOR);
            }

            if (user.getRoleId() != null) {
                Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
                corporateUser.setRole(role);
            }

            corporateUserRepo.save(corporateUser);
            if (corporateUser.isAdmin()) {
                removeUserFromAuthorizerRole(corporateUser);
            }

            return messageSource.getMessage("user.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.update.failure", null, locale), e);

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
        Corporate corporate = corporateRepo.findById(Long.parseLong(user.getCorporateId())).get();
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
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
            corporateUser.setRole(role);
            corporateUser.setAdmin(user.isAdmin());
            if (user.isAdmin()) {
                corporateUser.setCorpUserType(CorpUserType.ADMIN);
            } else {
                corporateUser.setCorpUserType(CorpUserType.INITIATOR);
            }
            Corporate corp = corporateRepo.findById(Long.parseLong(user.getCorporateId())).get();
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);
            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception exception) {
            if (exception instanceof EntrustException) {
                throw exception;
            }
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), exception);
        }
    }


    public void createUserOnEntrustAndSendCredentials(CorporateUser corporateUser) {
        CorporateUser user = corporateUserRepo.findFirstByUserNameIgnoreCase(corporateUser.getUserName());
        if (user != null) {
            if ("".equals(user.getEntrustId()) || user.getEntrustId() == null) {
                SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
                String entrustId = user.getUserName();
                String group = configService.getSettingByName("DEF_ENTRUST_CORP_GRP").getValue();

                if (setting != null && setting.isEnabled()) {
                    String fullName = user.getFirstName() + " " + user.getLastName();

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
                        user.setEntrustId(entrustId);
                        user.setEntrustGroup(group);
                        corporateUserRepo.save(user);
                    }
                }

                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveCorporatePassword(user);
                corporateUserRepo.save(user);
                sendCreationCredentials(user, password);


            }
        }
    }


    private void sendCreationCredentials(CorporateUser user, String password) {

        try {
            String url =
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
        } catch (Exception e) {
            logger.error("Error occurred sending creation credentials", e);
        }
    }

    public void sendActivationCredentials(CorporateUser user, String password) {

        try {
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
        } catch (Exception e) {
            logger.error("Error occurred sending activation credentials", e);
        }
    }

    @Override
    public void addCorporateUserToAuthorizerRole(CorporateUser corporateUser, Long corpRoleId) {

        CorporateRole corporateRole = corporateRoleRepo.findById(corpRoleId).get();
        corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
        corporateUser.setAdmin(false);
        corporateRole.getUsers().add(corporateUser);
        corporateRoleRepo.save(corporateRole);
    }

    @Override
    public void changeCorporateUserAuthorizerRole(CorporateUser corporateUser, CorporateRole role, Long newRoleId) {
        CorporateRole oldRole = corporateRoleRepo.findById(role.getId()).get();
        CorporateRole newRole = corporateRoleRepo.findById(newRoleId).get();
        corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
        oldRole.getUsers().remove(corporateUser);
        corporateRoleRepo.save(oldRole);
        newRole.getUsers().add(corporateUser);
        corporateRoleRepo.save(newRole);
    }

    @Override
    public void removeUserFromAuthorizerRole(CorporateUser corporateUser) {
        CorporateRole corporateRole = getCorporateUserAuthorizerRole(corporateUser);

        if (corporateRole != null) {
            CorporateRole role = corporateRoleRepo.findById(corporateRole.getId()).get();
            role.getUsers().remove(corporateUser);
            corporateRoleRepo.save(role);
        }
    }

    @Override
    @Transactional
    public String addCorpUserFromCorporateAdmin(CorpCorporateUserDTO user) throws InternetBankingException {

        CorporateUser corporateUser = corporateUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }

        Corporate corporate = corporateRepo.findById(Long.parseLong(user.getCorporateId())).get();
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
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findById(Long.parseLong(user.getCorporateId())).get();
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

        CorporateUser user = corporateUserRepo.findById(userId).get();
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }
        try {
            entityManager.detach(user);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            if (("I".equals(oldStatus)) && "A".equals(newStatus)) {

                CorporateUser corpUser = corporateUserRepo.save(user);
                sendActivationMessage(corpUser);

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


    private void sendActivationMessage(User user) {

        try {
            CorporateUser corpUser = getUserByName(user.getUserName());
            Corporate corporate = corpUser.getCorporate();

            if ("A".equals(corpUser.getStatus())) {

                String fullName = corpUser.getFirstName() + " " + corpUser.getLastName();
                String password = passwordPolicyService.generatePassword();
                corpUser.setPassword(passwordEncoder.encode(password));
                corpUser.setExpiryDate(new Date());
                passwordPolicyService.saveCorporatePassword(corpUser);
                corporateUserRepo.save(corpUser);
                String url =
                        ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/corporate";


                Context context = new Context();
                context.setVariable("fullName", fullName);
                context.setVariable("username", corpUser.getUserName());
                context.setVariable("password", password);
                context.setVariable("corporateId", corporate.getCorporateId());
                context.setVariable("url", url);

                Email email = new Email.Builder()
                        .setRecipient(corpUser.getEmail())
                        .setSubject(messageSource.getMessage("corporate.customer.activation.subject", null, locale))
                        .setTemplate("mail/corpactivation")
                        .build();

                mailService.sendMail(email, context);
            }

        } catch (Exception e) {
            logger.error("Error occurred sending activation credentials", e);
        }
    }

    @Async
    public void sendPasswordResetMessage(CorporateUser user) {

        try {
            CorporateUser corpUser = corporateUserRepo.findById(user.getId()).get();
            Corporate corporate = corpUser.getCorporate();

            String fullName = corpUser.getFirstName() + " " + corpUser.getLastName();
            String password = passwordPolicyService.generatePassword();
            corpUser.setPassword(passwordEncoder.encode(password));
            corpUser.setExpiryDate(new Date());
            passwordPolicyService.saveCorporatePassword(corpUser);
            corporateUserRepo.save(corpUser);
            String url =
                    ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/corporate";


            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("username", corpUser.getUserName());
            context.setVariable("password", password);
            context.setVariable("corporateId", corporate.getCorporateId());
            context.setVariable("url", url);

            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("corp.customer.password.reset.subject", null, locale))
                    .setTemplate("mail/corppasswordreset")
                    .build();
            mailService.sendMail(email, context);
        } catch (Exception e) {
            logger.error("Error occurred sending reset credentials", e);
        }
    }


    @Override
    public String changeCorpActivationStatus(Long userId) throws InternetBankingException {

        CorporateUser user = corporateUserRepo.findById(userId).get();
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }
        try {
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            if ("I".equals(oldStatus) && "A".equals(newStatus)) {
                CorporateUser corpUser = corporateUserRepo.save(user);
                sendActivationMessage(corpUser);
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

        CorporateUser user = corporateUserRepo.findById(userId).get();
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }
        try {
            sendPasswordResetMessage(user);
            logger.info("Corporate user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }


    @Override
    public String resetCorpPassword(Long userId) throws PasswordException {

        CorporateUser user = corporateUserRepo.findById(userId).get();
        Corporate corporate = user.getCorporate();

        if ("I".equals(corporate.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("corporate.deactivated", null, locale));
        }

        try {

            sendPasswordResetMessage(user);
            logger.info("Corporate user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }


    @Override
    @Verifiable(operation = "DELETE_CORPORATE_USER", description = "Deleting a Corporate User")
    public String deleteUser(Long userId) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findById(userId).get();
            corporateUserRepo.delete(corporateUser);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (VerificationInterruptedException ve) {
            return ve.getMessage();
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale), e);
        }
    }


    @Override
    public String unlockUser(Long id) throws InternetBankingException {

        CorporateUser user = corporateUserRepo.findById(id).get();
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
            CorporateUser corporateUser = corporateUserRepo.findById(user.getId()).get();
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
            CorporateUser corporateUser = corporateUserRepo.findById(user.getId()).get();
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


    @Transactional
    @Override
    public String resetPasswordForMobileUsers(CorporateUser user, CustResetPassword changePassword) {
        String message;

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        try {
            CorporateUser corporateUser = corporateUserRepo.getOne(user.getId());
            corporateUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            corporateUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveCorporatePassword(corporateUser);
            corporateUserRepo.save(corporateUser);

            //push request to webhook
            WebhookResponse webhookResponse = corporateWebhook.pushToWebHook(corporateUser);
            logger.info("Webhook Response {} ", webhookResponse);
            if(webhookResponse.getCode().equalsIgnoreCase("0")){

                return messageSource.getMessage("password.change.success", null, locale);
            }
            else if ((webhookResponse.getCode().equalsIgnoreCase("20"))){
                message =webhookResponse.getDescription();
                return message;

            }
            else if ((webhookResponse.getCode().equalsIgnoreCase("40"))){
                message =webhookResponse.getDescription();
                return message;

            }
            else if ((webhookResponse.getCode().equalsIgnoreCase("99"))){
                message =webhookResponse.getDescription();
                return message;

            }
            else if ((webhookResponse.getCode().equalsIgnoreCase("10"))){
                message =webhookResponse.getDescription();
                return message;

            }

            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.failure", null, locale);
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
        Corporate corporate = corporateRepo.findById(corpId).get();
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

        String designation = "";
        List<CorporateRole> roles = corporateRoleRepo.findByCorporate(corporateUser.getCorporate());
        for (CorporateRole corporateRole : roles) {
            if (corporateRoleRepo.countInRole(corporateRole, corporateUser) > 0) {
                designation = corporateRole.getName() + " " + corporateRole.getRank();
                break;
            }

        }
        if (designation.isEmpty() && corporateUser.getCorpUserType() != null) {
            designation = StringUtils.capitalize(corporateUser.getCorpUserType().toString().toLowerCase());
        }

        return designation;
    }

    public CorporateRole getCorporateUserAuthorizerRole(CorporateUser user) {

        Corporate corporate = user.getCorporate();
        List<CorporateRole> roles = corporateRoleRepo.findByCorporate(corporate);

        for (CorporateRole corporateRole : roles) {
            if (corporateRoleRepo.countInRole(corporateRole, user) > 0) {
                return corporateRole;
            }
        }
        return null;
    }

    @Override
    public CorporateUserDTO convertEntityToDTO(CorporateUser corporateUser) {

        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        corporateUserDTO.setCorporateId(corporateUser.getCorporate().getId().toString());
        corporateUserDTO.setCorporateName(corporateUser.getCorporate().getName());
        corporateUserDTO.setCorporateType(corporateUser.getCorporate().getCorporateType());
        corporateUserDTO.setId(corporateUser.getId());
        corporateUserDTO.setVersion(corporateUser.getVersion());
        corporateUserDTO.setUserName(corporateUser.getUserName());
        corporateUserDTO.setFirstName(corporateUser.getFirstName());
        corporateUserDTO.setLastName(corporateUser.getLastName());
        corporateUserDTO.setPhoneNumber(corporateUser.getPhoneNumber());
        corporateUserDTO.setAdmin(corporateUser.isAdmin());
        corporateUserDTO.setStatus(corporateUser.getStatus());
        corporateUserDTO.setEmail(corporateUser.getEmail());
        corporateUserDTO.setEntrustId(corporateUser.getEntrustId());
        corporateUserDTO.setEntrustGroup(corporateUser.getEntrustGroup());
        corporateUserDTO.setIsFirstTimeLogon(corporateUser.getIsFirstTimeLogon());
        corporateUserDTO.setRoleId(corporateUser.getRole().getId().toString());
        corporateUserDTO.setRole(corporateUser.getRole().getName());
        corporateUserDTO.setCorpUserType(corporateUser.getCorpUserType());
        if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())) {
            corporateUserDTO.setAuthorizer(true);
        }
        if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())) {
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
        CorporateUser corporateUser = new CorporateUser();
        corporateUser.setId(corporateUserDTO.getId());
        corporateUser.setVersion(corporateUserDTO.getVersion());
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
        Role role = roleRepo.findById(Long.parseLong(corporateUserDTO.getRoleId())).get();
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
        return new PageImpl<>(dtOs, pageDetails, t);
    }


    @Override
    public Page<CorporateUserDTO> getUsers(Pageable pageDetails) {

        Page<CorporateUser> page = corporateUserRepo.findAll(pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        return new PageImpl<>(dtOs, pageDetails, t);
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
        return corporateUserRepo.findFirstByCorporateAndEmailIgnoreCase(corporate, email);
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
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findById(Long.parseLong(user.getCorporateId())).get();
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);
            if (user.isAuthorizer()) {
                if (user.getCorporateRoleId() != null) {
                    addCorporateUserToAuthorizerRole(corporateUser, user.getCorporateRoleId());
                }
            }
            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            logger.error("Error creating corporate user", e);
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
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
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
            corporateUser.setRole(role);
            Corporate corp = corporateRepo.findById(Long.parseLong(user.getCorporateId())).get();
            corporateUser.setCorporate(corp);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);
            createUserOnEntrustAndSendCredentials(corpUser);

            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            logger.error("Error creating corporate user", e);
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    @Override
    public String updateUserFromCorpAdmin(CorporateUserDTO user) throws InternetBankingException {

        try {
            CorporateUser corporateUser = corporateUserRepo.findById(user.getId()).get();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
            corporateUser.setRole(role);

            if (CorpUserType.AUTHORIZER.equals(user.getCorpUserType())) {
                if (user.getCorporateRoleId() != null) {
                    CorporateRole corporateRole = getCorporateUserAuthorizerRole(corporateUser);

                    if (corporateRole != null) {
                        if (!user.getCorporateRoleId().equals(corporateRole.getId())) {
                            changeCorporateUserAuthorizerRole(corporateUser, corporateRole, user.getCorporateRoleId());
                        }
                    } else {
                        addCorporateUserToAuthorizerRole(corporateUser, user.getCorporateRoleId());
                    }
                }
            } else {
                corporateUser.setCorpUserType(CorpUserType.INITIATOR);
                removeUserFromAuthorizerRole(corporateUser);
            }

            corporateUserRepo.save(corporateUser);

            logger.info("Corporate user {} updated", corporateUser.getUserName());
            return messageSource.getMessage("user.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw e;
            }
            throw new InternetBankingException(messageSource.getMessage("user.update.failure", null, locale), e);
        }
    }

    @Override
    public String changeActivationStatusFromCorpAdmin(Long id) throws InternetBankingException {
        CorporateUser corporateUser = corporateUserRepo.findById(id).get();

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

        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternetBankingException(messageSource.getMessage("corporate.update.failure", null, locale), e);
        }
    }

    @Override
    public String resetSecurityQuestion(Long id) {
        CorporateUser user = corporateUserRepo.findById(id).get();
        logger.info("this is the user status{}", user.getStatus());
        if ("I".equals(user.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("users.deactivated", null, locale));
        }
        try {
            user.setResetSecurityQuestion("Y");
            corporateUserRepo.save(user);
            logger.info("Retail user {} Security question reset successfully", user.getUserName());
            return messageSource.getMessage("securityquestion.reset.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("securityquestion.reset.failure", null, locale), e);
        }
    }

    @Override
    public void setSecurityQuestion(Long id) {
        CorporateUser user = corporateUserRepo.findById(id).get();

        try {
            user.setResetSecurityQuestion("N");
            corporateUserRepo.save(user);
            logger.info("Retail user {} Security question set successfully", user.getUserName());
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("securityquestion.reset.failure", null, locale), e);
        }
    }


    @Override
    @Verifiable(operation = "UPDATE_USER_ACCOUNT_PERMISSION", description = "Update corporate user account permission")
    public String updateAccountPermissions(CorporateUserDTO corporateUserDTO) {

        try {
            String message = updateAccountRestrictionsBasedOnPermissions(corporateUserDTO);
            logger.info("User account permissions updated successfully");
            return message;
        } catch (InternetBankingException ibe) {
            throw ibe;
        }
    }



    @Override
    public String updateAccountRestrictionsBasedOnPermissions(CorporateUserDTO userDTO) {

        try {
            List<UserAccountRestriction> accountRestrictions = new ArrayList<>();
            CorporateUser user = corporateUserRepo.findById(userDTO.getId()).get();
            List<Account> accounts = user.getCorporate().getAccounts();

            logger.debug("Corporate accounts: {}",accounts);
            for (Account account : accounts) {
                AccountPermissionDTO accountPermission = getAccountPermission(account, userDTO.getAccountPermissions());

                UserAccountRestriction accountRestriction = userAccountRestrictionRepo.findByCorporateUserIdAndAccountId(user.getId(), account.getId());

                if(accountPermission!=null) {
                    switch (accountPermission.getPermission()) {
                        case VIEW_ONLY:
                            if (accountRestriction != null) {
                                accountRestriction.setRestrictionType(TRANSACTION);

                            } else {
                                accountRestriction = new UserAccountRestriction(user.getId(), account.getId(), TRANSACTION);
                                accountRestrictions.add(accountRestriction);
                            }
                            break;
                        case VIEW_AND_TRANSACT:
                            if (accountRestriction != null) {
                                accountRestriction.setRestrictionType(NONE);

                            } else {
                                accountRestriction = new UserAccountRestriction(user.getId(), account.getId(), NONE);
                                accountRestrictions.add(accountRestriction);
                            }
                            break;
                        case NONE:
                            if (accountRestriction != null) {
                                accountRestriction.setRestrictionType(VIEW);
                            } else {
                                accountRestriction = new UserAccountRestriction(user.getId(), account.getId(), VIEW);
                                accountRestrictions.add(accountRestriction);
                            }
                            break;

                        default:
                            break;
                    }
                    userAccountRestrictionRepo.save(accountRestriction);
                }
            }
//            userAccountRestrictionRepo.save(accountRestrictions);
            return messageSource.getMessage("accountpermission.update.success", null, locale);
        } catch (Exception e) {
            logger.error("Failed to update user account permissions", e);
            throw new InternetBankingException(messageSource.getMessage("accountpermission.update.failure", null, locale), e);
        }
    }

    private AccountPermissionDTO getAccountPermission(Account account, List<AccountPermissionDTO> accountPermissions) {

        for (AccountPermissionDTO permission : accountPermissions) {
            if (permission.getAccountNumber().equals(account.getAccountNumber())) {
                return permission;
            }
        }
        return null;
    }


    @Override
    public List<AccountPermissionDTO> getAccountPermissionsForAdminManagement(Long userId) {

        //Corporate Admin cannot view and manage accounts permission that he does not have
        //permission to view

        CorporateUser adminUser = getCurrentUser();
        //Get admin user account permissions
        List<AccountPermissionDTO> adminAccountPermissions = getAccountPermissions(adminUser.getId());
        logger.debug("Admin user permissions {}",adminAccountPermissions);

        //Get the target corporate user permissions
        List<AccountPermissionDTO> accountPermissions = getAccountPermissions(userId);
        logger.debug("Corporate user account permissions {}",accountPermissions);

        Iterator<AccountPermissionDTO> accountPermissionIterator = accountPermissions.iterator();

        while (accountPermissionIterator.hasNext()){
            AccountPermissionDTO accountPermission = accountPermissionIterator.next();
            for(AccountPermissionDTO adminAccountPermission: adminAccountPermissions ){
                if(accountPermission.equals(adminAccountPermission) && adminAccountPermission.getPermission().equals(AccountPermissionDTO.Permission.NONE)){
                    accountPermissionIterator.remove();
                    logger.debug("Removed account permission {} as admin has no VIEW access permission",accountPermission);
                }
            }

        }

        return accountPermissions;
    }

    @Override
    public List<AccountPermissionDTO> getAccountPermissions(Long userId) {

        CorporateUser corporateUser = corporateUserRepo.findById(userId).get();
        Corporate corporate = corporateUser.getCorporate();
        List<Account> accounts = corporate.getAccounts();

        List<UserAccountRestriction> accountRestrictions = userAccountRestrictionRepo.findByCorporateUserId(userId);

        List<AccountPermissionDTO> accountPermissions = new ArrayList<>();

        for (Account account : accounts) {

            if (accountRestricted(account, accountRestrictions)) {
                UserAccountRestriction restrictedAccount = userAccountRestrictionRepo.findByCorporateUserIdAndAccountId(corporateUser.getId(), account.getId());
                AccountPermissionDTO accountPermission;
                switch (restrictedAccount.getRestrictionType()) {
                    case VIEW:
                        accountPermission = new AccountPermissionDTO(account, AccountPermissionDTO.Permission.NONE);
                        accountPermissions.add(accountPermission);
                        break;
                    case TRANSACTION:
                        accountPermission = new AccountPermissionDTO(account, VIEW_ONLY);
                        accountPermissions.add(accountPermission);
                        break;

                    case NONE:
                        accountPermission = new AccountPermissionDTO(account, AccountPermissionDTO.Permission.VIEW_AND_TRANSACT);
                        accountPermissions.add(accountPermission);
                        break;
                    default:
                        break;


                }                    logger.info("not restricted {}",accountPermissions);

            } else {
                AccountPermissionDTO permission = new AccountPermissionDTO(account, AccountPermissionDTO.Permission.VIEW_AND_TRANSACT);
                accountPermissions.add(permission);
                logger.info("restricted {}",accountPermissions);
            }

        }
        return accountPermissions;
    }


    private boolean accountRestricted(Account account, List<UserAccountRestriction> accountRestrictions) {

        for (UserAccountRestriction accountRestriction : accountRestrictions) {
            if (accountRestriction.getAccountId().equals(account.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String changeFeedBackStatus(CorporateUser user) throws InternetBankingException{

        try {
            if (getUser(user.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return null;
            }
            corporateUserRepo.updateFeedBackStatus(user.getFeedBackStatus(), user.getId());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FEEDBACK STATUS ERROR OCCURRED {}", e.getMessage());
        }
        return "Feedback status change not successful";
    }

    @Override
    public Page<CorporateUserDTO> getUsers(CorporateDTO user, Pageable pageDetails) {
        logger.info("Corp user: {} {} ",user.getName(),user.getCorporateId() );
        Page<CorporateUser> page = corporateUserRepo.findCorporateUser(user.getCorporateId().toLowerCase(),
                user.getName().toLowerCase(), pageDetails);
        logger.info("query result count is: {} ",page.getTotalElements());
        if(page.getContent().size()>0){
            logger.info("my corp id is: {}",page.getContent().get(0).getCorporate().getCorporateId());
        }

        List<CorporateUserDTO> dtOs =new ArrayList<>();
        for (CorporateUser corporateUser : page.getContent()) {
            CorporateUserDTO corporateUserDTO = convertEntityToDTO(corporateUser);
            logger.info("have got record for username  : {} ",corporateUserDTO.getCorporateName());
            dtOs.add(corporateUserDTO);
        }
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    private CorporateUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return (CorporateUser) user;
    }
}

