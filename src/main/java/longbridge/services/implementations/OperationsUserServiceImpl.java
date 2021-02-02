package longbridge.services.implementations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.Email;
import longbridge.models.OperationsUser;
import longbridge.models.Role;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 * Modified by Fortune
 */
@Service
public class OperationsUserServiceImpl implements OperationsUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private SettingsService configService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private EntityManager entityManager;


    private final Locale locale = LocaleContextHolder.getLocale();

    public OperationsUserServiceImpl() {

    }


    @Override
    public boolean userExists(String username) {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserNameIgnoreCase(username);
        return opsUser != null;

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
        OperationsUser user = operationsUserRepo.findById(id).get();

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
        return new PageImpl<>(dtOs, pageDetails, t);
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
            OperationsUser user = operationsUserRepo.findById(userId).get();
            entityManager.detach(user);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            operationsUserRepo.save(user);
            sendActivationMessage(user);

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
    public OperationsUser getUserByName(String name) {
        return this.operationsUserRepo.findFirstByUserNameIgnoreCase(name);
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
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
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
        OperationsUser user = operationsUserRepo.findFirstByUserNameIgnoreCase(opsUser.getUserName());
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

                        user.setEntrustId(entrustId);
                        user.setEntrustGroup(group);
                        user = operationsUserRepo.save(user);

                    }
                }
                sendCreationMessage(user);
            }
        }
        return user;
    }

    private void sendCreationMessage(OperationsUser user) {
       try {
           Email email = new Email.Builder()
                   .setRecipient(user.getEmail())
                   .setSubject(messageSource.getMessage("ops.creation.subject", null, locale))
                   .setTemplate("mail/opscreation")
                   .build();
           generateAndSendCredentials(user, email);
       }
       catch (Exception e){
           logger.error("Error occurred sending creation credentials",e);

       }
    }

    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_OPS_USER", description = "Updating an Operations User")
    public String updateUser(OperationsUserDTO user) throws InternetBankingException {

        OperationsUser opsUser = operationsUserRepo.findById(user.getId()).get();
        if ("I".equals(opsUser.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("user.deactivated", null, locale));
        }

        if (!user.getEmail().equals(opsUser.getEmail())) {

            opsUser = operationsUserRepo.findFirstByEmailIgnoreCase(user.getEmail());
            if (opsUser != null && !user.getId().equals(opsUser.getId())) {
                throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
            }
        }

        opsUser = operationsUserRepo.findById(user.getId()).get();


        try {
            entityManager.detach(opsUser);
            opsUser.setVersion(user.getVersion());
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setPhoneNumber(user.getPhoneNumber());
            opsUser.setEmail(user.getEmail());
            Role role = roleRepo.findById(Long.parseLong(user.getRoleId())).get();
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
            OperationsUser opsUser = operationsUserRepo.findById(userId).get();
            operationsUserRepo.delete(opsUser);
            logger.warn("Operations user with Id {} deleted", userId);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(opsUser.getEntrustId(), opsUser.getEntrustGroup());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (VerificationInterruptedException ve) {
            return ve.getMessage();
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale));
        }
    }

    @Override
    public String resetPassword(Long id) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findById(id).get();
            sendResetMessage(user);
            logger.info("Operations user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(String username) throws InternetBankingException {
        try {
            logger.debug("About to reset ops user {} password",username);
            OperationsUser user = operationsUserRepo.findFirstByUserNameIgnoreCase(username);
            sendResetMessage(user);
            logger.info("Operations user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (MailException me) {
            throw new PasswordException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    private void sendResetMessage(OperationsUser user) {
        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("ops.password.reset.subject", null, locale))
                    .setTemplate("mail/opspasswordreset")
                    .build();
            generateAndSendCredentials(user, email);
        }
        catch (Exception e){
            logger.error("Error occurred sending reset credentials",e);

        }
    }

    @Override
    @Transactional
    public String changePassword(OperationsUser user, ChangePassword changePassword) throws InternetBankingException {

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
            OperationsUser opsUser = operationsUserRepo.findById(user.getId()).get();
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
            OperationsUser opsUser = operationsUserRepo.findById(user.getId()).get();
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


    public void generateAndSendCredentials(OperationsUser user, Email email) {

        String opsUrl =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/ops";


        if ("A".equals(user.getStatus())) {
            logger.debug("Ops user {} is ACTIVE and should receive mail",user.getUserName());
            String fullName = user.getFirstName() + " " + user.getLastName();
            String password = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveOpsPassword(user);
            operationsUserRepo.save(user);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("username", user.getUserName());
            context.setVariable("password", password);
            context.setVariable("opsUrl", opsUrl);

            logger.debug("About to send new credentials to user {} ",user.getUserName());
            mailService.sendMail(email, context);

        }else {
            logger.debug("Ops user {} is INACTIVE and will not receive mail, the user should be reactivated instead",user.getUserName());

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

        return new PageImpl<>(dtOs, pageDetails, t);
    }


    @Override
    public Page<OperationsUserDTO> findUsers(String pattern, Pageable pageDetails) {
        Page<OperationsUser> page = operationsUserRepo.findUsingPattern(pattern, pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        return new PageImpl<>(dtOs, pageDetails, t);
    }



    @Override
    public void sendActivationCredentials(OperationsUser user, String password) {


        String opsUrl =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/ops";


        String fullName = user.getFirstName() + " " + user.getLastName();
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("username", user.getUserName());
        context.setVariable("password", password);
        context.setVariable("opsUrl", opsUrl);

        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("ops.activation.subject", null, locale))
                .setTemplate("mail/opsactivation")
                .build();

        mailService.sendMail(email, context);
    }

    private void sendActivationMessage(OperationsUser user) {


        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("ops.activation.subject", null, locale))
                .setTemplate("mail/opsactivation")
                .build();

        generateAndSendCredentials(user, email);

    }
}
