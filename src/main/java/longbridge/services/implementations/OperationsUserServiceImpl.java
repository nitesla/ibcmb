package longbridge.services.implementations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.*;
import longbridge.repositories.OperationsUserRepo;
import longbridge.repositories.RoleRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.ReflectionUtils;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 * Modified by Fortune
 */
@Service
public class OperationsUserServiceImpl implements OperationsUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private OperationsUserRepo operationsUserRepo;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private EntityManager entityManager;

    @Value("${host.url}")
    private String hostUrl;

    private Locale locale = LocaleContextHolder.getLocale();

    public OperationsUserServiceImpl() {

    }


    @Override
    public boolean userExists(String username) {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserName(username);
        return (opsUser != null) ? true : false;

    }


    @Autowired
    public OperationsUserServiceImpl(OperationsUserRepo operationsUserRepo, BCryptPasswordEncoder passwordEncoder) {
        this.operationsUserRepo = operationsUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long countOps() {
        return operationsUserRepo.count();
    }

    @Override
    public OperationsUserDTO getUser(Long id) {
        OperationsUser user = operationsUserRepo.findOne(id);
        return convertEntityToDTO(user);
    }

    @Override
    public Page<OperationsUserDTO> findUsers(OperationsUserDTO example, Pageable pageDetails) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase().withIgnorePaths("version", "noOfLoginAttempts").withIgnoreNullValues();
        OperationsUser entity = convertDTOToEntity(example);
        logger.info("Ops user: " + entity.toString());
        ReflectionUtils.nullifyStrings(entity, 1);
        Page<OperationsUser> page = operationsUserRepo.findAll(Example.of(entity, matcher), pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Iterable<OperationsUserDTO> getUsers() {
        Iterable<OperationsUser> operationsUsers = operationsUserRepo.findAll();
        return convertEntitiesToDTOs(operationsUsers);
    }

    @Override
    public String setPassword(OperationsUser user, String password) throws InternetBankingException {
        return null;//TODO
    }

    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_OPS_STATUS", description = "Change Operations User Activation Status")
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findOne(userId);
            entityManager.detach(user);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            operationsUserRepo.save(user);
            sendCredentialNotification(user);

            logger.info("Operations user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
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


    @Override
    public void sendActivationMessage(User user, String... args) {


        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("ops.activation.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("ops.activation.message", null, locale), args))
                    .build();
            mailService.send(email);
        } catch (MailException me) {
            logger.error("Error sending mail to {}", user.getEmail(), me);
        }
    }

    @Override
    public OperationsUser getUserByName(String name) {
        OperationsUser opsUser = this.operationsUserRepo.findFirstByUserNameIgnoreCase(name);
        return opsUser;
    }

    @Override
    @Transactional
    @Verifiable(operation = "ADD_OPS_USER", description = "Adding an Operations User")
    public String addUser(OperationsUserDTO user) throws InternetBankingException {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (opsUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }


        opsUser = operationsUserRepo.findFirstByEmailIgnoreCase(user.getEmail());
        if (opsUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
        }

        try {
            opsUser = new OperationsUser();
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setEmail(user.getEmail());
            opsUser.setPhoneNumber(user.getPhoneNumber());
            opsUser.setStatus("A");
            opsUser.setCreatedOnDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            opsUser.setRole(role);
            OperationsUser newUser = operationsUserRepo.save(opsUser);
            createUserOnEntrustAndSendCredentials(newUser);

            logger.info("New Operation user  {} created", opsUser.getUserName());
            return messageSource.getMessage("user.add.success", null, LocaleContextHolder.getLocale());
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), se);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw e;
            }
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }


    }


    public OperationsUser createUserOnEntrustAndSendCredentials(OperationsUser opsUser) {
        OperationsUser user = operationsUserRepo.findFirstByUserName(opsUser.getUserName());
        if (user != null) {
            if ("".equals(user.getEntrustId()) || user.getEntrustId() == null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
                String entrustId = user.getUserName();
                String group = configService.getSettingByName("DEF_ENTRUST_OPS_GRP").getValue();

                if (setting != null && setting.isEnabled() && group != null) {
                    if ("YES".equalsIgnoreCase(setting.getValue())) {
                        boolean creatResult = securityService.createEntrustUser(entrustId, group, fullName, true);
                        if (!creatResult) {
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
                    OperationsUser ops = operationsUserRepo.save(user);
                    sendCredentialNotification(ops);
                }
            }
        }
        return user;
    }

    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_OPS_USER", description = "Updating an Operations User")
    public String updateUser(OperationsUserDTO user) throws InternetBankingException {

        OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
        if ("I".equals(opsUser.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("user.deactivated", null, locale));
        }

        if (!user.getEmail().equals(opsUser.getEmail())) {

            opsUser = operationsUserRepo.findFirstByEmailIgnoreCase(user.getEmail());
            if (opsUser != null && !user.getId().equals(opsUser.getId())) {
                throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
            }
        }

        opsUser = operationsUserRepo.findOne(user.getId());


        try {
            entityManager.detach(opsUser);
            opsUser.setVersion(user.getVersion());
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setPhoneNumber(user.getPhoneNumber());
            opsUser.setEmail(user.getEmail());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            opsUser.setRole(role);
            operationsUserRepo.save(opsUser);

            logger.info("Operations user {} updated", opsUser.getUserName());
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
    @Verifiable(operation = "DELETE_OPS_USER", description = "Deleting an Ops User")
    public String deleteUser(Long userId) throws InternetBankingException {
        try {
            OperationsUser opsUser = operationsUserRepo.findOne(userId);
            operationsUserRepo.delete(opsUser);
            logger.warn("Operations user with Id {} deleted", userId);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(opsUser.getEntrustId(), opsUser.getEntrustGroup());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        }
        catch (VerificationInterruptedException ve){
            return ve.getMessage();
        }
        catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale));
        }
    }

    @Override
    public String resetPassword(Long id) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findOne(id);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            String fullName = user.getFirstName() + " " + user.getLastName();
            user.setExpiryDate(new Date());
            passwordPolicyService.saveOpsPassword(user);
            this.operationsUserRepo.save(user);
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("ops.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("ops.password.reset.message", null, locale), fullName, newPassword))
                    .build();
            mailService.send(email);
            logger.info("Operations user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (MailException me) {
            throw new PasswordException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(String username) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findFirstByUserName(username);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            String fullName = user.getFirstName() + " " + user.getLastName();
            user.setExpiryDate(new Date());
            passwordPolicyService.saveOpsPassword(user);
            this.operationsUserRepo.save(user);
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("ops.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("ops.password.reset.message", null, locale), fullName, newPassword))
                    .build();
            mailService.send(email);
            logger.info("Operations user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (MailException me) {
            throw new PasswordException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public String changePassword(OperationsUser user, ChangePassword changePassword) throws InternetBankingException, PasswordException {

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
            OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
            opsUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            opsUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveOpsPassword(user);
            operationsUserRepo.save(opsUser);
            logger.info("User {}'s password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public String changeDefaultPassword(OperationsUser user, ChangeDefaultPassword changePassword) throws PasswordException {


        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
            opsUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            opsUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveOpsPassword(user);
            operationsUserRepo.save(opsUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser) {
        OperationsUserDTO operationsUserDTO = modelMapper.map(operationsUser, OperationsUserDTO.class);
        operationsUserDTO.setRole(operationsUser.getRole().getName());
        if (operationsUser.getCreatedOnDate() != null) {
            operationsUserDTO.setCreatedOnDate(DateFormatter.format(operationsUser.getCreatedOnDate()));
        }
        if (operationsUser.getLastLoginDate() != null) {
            operationsUserDTO.setLastLoginDate(DateFormatter.format(operationsUser.getLastLoginDate()));
        }
        return operationsUserDTO;
    }

    private OperationsUser convertDTOToEntity(OperationsUserDTO operationsUserDTO) {
        return modelMapper.map(operationsUserDTO, OperationsUser.class);
    }

    private List<OperationsUserDTO> convertEntitiesToDTOs(Iterable<OperationsUser> operationsUsers) {
        List<OperationsUserDTO> operationsUserDTOList = new ArrayList<>();
        for (OperationsUser operationsUser : operationsUsers) {
            OperationsUserDTO userDTO = convertEntityToDTO(operationsUser);
            operationsUserDTOList.add(userDTO);
        }
        return operationsUserDTOList;
    }

    @Override
    public Page<OperationsUserDTO> getUsers(Pageable pageDetails) {
        Page<OperationsUser> page = operationsUserRepo.findAll(pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    @Override
    public Page<OperationsUserDTO> findUsers(String pattern, Pageable pageDetails) {
        Page<OperationsUser> page = operationsUserRepo.findUsingPattern(pattern, pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    public void sendCredentialNotification(OperationsUser user) {

        String opsUrl = (hostUrl != null)? hostUrl+"/ops":"";

        if ("A".equals(user.getStatus())) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            String password = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveOpsPassword(user);
            OperationsUser opsUser = operationsUserRepo.save(user);
            sendActivationMessage(opsUser, fullName, user.getUserName(), password,opsUrl);
        }

    }
}
