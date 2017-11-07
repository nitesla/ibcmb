package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.models.Email;
import longbridge.models.Role;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.RoleRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.thymeleaf.context.Context;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by SYLVESTER on 3/30/2017.
 * Modified by Fortune
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AdminUserRepo adminUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private EntityManager entityManager;

    @Value("${host.url}")
    private String hostUrl;

    private Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    public AdminUserServiceImpl(AdminUserRepo adminUserRepo, BCryptPasswordEncoder passwordEncoder) {

        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUserServiceImpl() {
    }

    @Override
    public Long countAdm() {
        return adminUserRepo.count();
    }

    @Override
    public AdminUser getUser(Long id) {
        return this.adminUserRepo.findOne(id);
    }

    @Override
    public AdminUser getUserByName(String name) {
        return this.adminUserRepo.findFirstByUserNameIgnoreCase(name);
    }

    @Override
    public AdminUserDTO getAdminUser(Long userId) {
        AdminUser adminUser = adminUserRepo.findOne(userId);
        return convertEntityToDTO(adminUser);
    }

    @Override
    public boolean userExists(String username) throws InternetBankingException {
        AdminUser adminUser = adminUserRepo.findFirstByUserNameIgnoreCase(username);
        return (adminUser != null) ? true : false;
    }


    @Override
    public Iterable<AdminUserDTO> getUsers() {
        Iterable<AdminUser> adminUsers = adminUserRepo.findAll();
        return convertEntitiesToDTOs(adminUsers);
    }


    @Override
    @Transactional
    @Verifiable(operation = "ADD_ADMIN_USER", description = "Adding an Admin User")
    public String addUser(AdminUserDTO user) throws InternetBankingException {
        AdminUser adminUser = adminUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (adminUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }

        adminUser = adminUserRepo.findFirstByEmailIgnoreCase(user.getEmail());
        if (adminUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
        }

        try {
            adminUser = new AdminUser();
            adminUser.setFirstName(user.getFirstName());
            adminUser.setLastName(user.getLastName());
            adminUser.setUserName(user.getUserName());
            adminUser.setEmail(user.getEmail());
            adminUser.setPhoneNumber(user.getPhoneNumber());
            adminUser.setStatus("A");
            adminUser.setCreatedOnDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            adminUser.setRole(role);
            AdminUser newUser = adminUserRepo.save(adminUser);
            createUserOnEntrustAndSendCredentials(newUser);

            logger.info("New admin user {} created", adminUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), se);
        } catch (EntrustException entrustExc) {
            throw entrustExc;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);

        }
    }


    public AdminUser createUserOnEntrustAndSendCredentials(AdminUser adminUser) throws EntrustException {
        AdminUser user = adminUserRepo.findFirstByUserNameIgnoreCase(adminUser.getUserName());
        if (user != null) {
            if ("".equals(user.getEntrustId()) || user.getEntrustId() == null) {
                String fullName = adminUser.getFirstName() + " " + adminUser.getLastName();
                String entrustId = user.getUserName();
                String group = configService.getSettingByName("DEF_ENTRUST_ADM_GRP").getValue();

                SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
                if (setting != null && setting.isEnabled()) {
                    if ("YES".equalsIgnoreCase(setting.getValue())) {
                        boolean creatResult = securityService.createEntrustUser(entrustId, group, fullName, true);
                        if (!creatResult) {
                            throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));
                        }

                        boolean contactResult = securityService.addUserContacts(adminUser.getEmail(), adminUser.getPhoneNumber(), true, entrustId, group);
                        if (!contactResult) {
                            logger.error("Failed to add user contacts on Entrust");

                            throw new EntrustException(messageSource.getMessage("entrust.contact.failure", null, locale));

                        }

                        user.setEntrustId(entrustId);
                        user.setEntrustGroup(group);
                        user = adminUserRepo.save(user);
                    }
                }
                sendCreationMessage(user);
            }
        }
        return user;
    }

    private void sendCreationMessage(AdminUser user){
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("admin.creation.subject", null, locale))
                .setTemplate("mail/admincreation")
                .build();
        generateAndSendCredentials(user,email);
    }

    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_ADMIN_STATUS", description = "Change Admin Activation Status")
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            AdminUser user = adminUserRepo.findOne(userId);
            entityManager.detach(user);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            adminUserRepo.save(user);
            sendActivationMessage(user);

            logger.info("Admin user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
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


    private void sendActivationMessage(AdminUser user) {

            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("admin.activation.subject", null, locale))
                    .setTemplate("mail/adminactivation")
                    .build();

            generateAndSendCredentials(user,email);

    }

    @Override
    public void sendActivationCredentials(AdminUser user, String password) {


        String adminUrl = (hostUrl != null) ? hostUrl + "/admin" : "";

            String fullName = user.getFirstName() + " " + user.getLastName();
            Context context = new Context();
            context.setVariable("fullName",fullName);
            context.setVariable("username", user.getUserName());
            context.setVariable("password",password);
            context.setVariable("adminUrl",adminUrl);

        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("admin.activation.subject", null, locale))
                .setTemplate("mail/adminactivation")
                .build();

            mailService.sendMail(email,context);
    }

    @Override
    @Transactional
    @Verifiable(operation = "DELETE_ADMIN_USER", description = "Deleting an Admin User")
    public String deleteUser(Long id) throws InternetBankingException {
        try {
            AdminUser user = adminUserRepo.findOne(id);
            adminUserRepo.delete(user);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(user.getEntrustId(), user.getEntrustGroup());
                }
            }
            logger.warn("Admin user {} deleted", user.getUserName());
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
    @Transactional
    @Verifiable(operation = "UPDATE_ADMIN_USER", description = "Updating an Admin User")
    public String updateUser(AdminUserDTO user) throws InternetBankingException {

        AdminUser adminUser = adminUserRepo.findById(user.getId());

        if ("I".equals(adminUser.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("user.deactivated", null, locale));
        }

        if (!user.getEmail().equals(adminUser.getEmail())) {

            adminUser = adminUserRepo.findFirstByEmailIgnoreCase(user.getEmail());
            if (adminUser != null && !user.getId().equals(adminUser.getId())) {
                throw new DuplicateObjectException(messageSource.getMessage("email.exists", null, locale));
            }
        }

        adminUser = adminUserRepo.findById(user.getId());

        try {
            entityManager.detach(adminUser);
            adminUser.setId(user.getId());
            adminUser.setVersion(user.getVersion());
            adminUser.setFirstName(user.getFirstName());
            adminUser.setLastName(user.getLastName());
            adminUser.setUserName(user.getUserName());
            adminUser.setEmail(user.getEmail());
            adminUser.setPhoneNumber(user.getPhoneNumber());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            adminUser.setRole(role);
            adminUserRepo.save(adminUser);
            logger.info("Admin user {} updated", adminUser.getUserName());
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
    public String resetPassword(Long userId) throws PasswordException {


        AdminUser user = adminUserRepo.findOne(userId);
        if ("I".equals(user.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("users.deactivated", null, locale));
        }
        try {
            sendResetMessage(user);
            logger.info("Admin user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        }  catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(String username) throws PasswordException {

        try {
            AdminUser user = adminUserRepo.findFirstByUserNameIgnoreCase(username);
            sendResetMessage(user);
            logger.info("Admin user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (MailException me) {
            throw new PasswordException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }


    private void sendResetMessage(AdminUser user){
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("admin.password.reset.subject", null, locale))
                .setTemplate("mail/adminpasswordreset")
                .build();
        generateAndSendCredentials(user,email);
    }

    @Override
    @Transactional
    public String changePassword(AdminUser user, ChangePassword changePassword) throws PasswordException {

        if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!errorMessage.isEmpty()) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            AdminUser adminUser = adminUserRepo.findOne(user.getId());
            adminUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            adminUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveAdminPassword(user);
            adminUserRepo.save(adminUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    public String changeDefaultPassword(AdminUser user, ChangeDefaultPassword changePassword) throws PasswordException {

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!errorMessage.isEmpty()) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            AdminUser adminUser = adminUserRepo.findOne(user.getId());
            adminUser.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            adminUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveAdminPassword(user);
            adminUserRepo.save(adminUser);
            logger.info("User {}'s password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    private List<AdminUserDTO> convertEntitiesToDTOs(Iterable<AdminUser> adminUsers) {
        List<AdminUserDTO> adminUserDTOList = new ArrayList<>();


        for (AdminUser adminUser : adminUsers) {
            AdminUserDTO userDTO = convertEntityToDTO(adminUser);
            userDTO.setRole(adminUser.getRole().getName());
            adminUserDTOList.add(userDTO);
        }
        return adminUserDTOList;
    }

    private AdminUserDTO convertEntityToDTO(AdminUser adminUser) {
        AdminUserDTO adminUserDTO = modelMapper.map(adminUser, AdminUserDTO.class);
        adminUserDTO.setRoleId(adminUser.getRole().getId().toString());
        if (adminUser.getCreatedOnDate() != null) {
            adminUserDTO.setCreatedOnDate(DateFormatter.format(adminUser.getCreatedOnDate()));
        }
        if (adminUser.getLastLoginDate() != null) {
            adminUserDTO.setLastLoginDate(DateFormatter.format(adminUser.getLastLoginDate()));
        }

        return adminUserDTO;
    }

    private AdminUser convertDTOToEntity(AdminUserDTO adminUserDTO) {
        return modelMapper.map(adminUserDTO, AdminUser.class);
    }


    public Page<AdminUserDTO> getUsers(Pageable pageDetails) {
        Page<AdminUser> page = adminUserRepo.findAll(pageDetails);
        List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Page<AdminUserDTO> findUsers(String pattern, Pageable pageDetails) {
        Page<AdminUser> page = adminUserRepo.findUsingPattern(pattern, pageDetails);
        List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public void generateAndSendCredentials(AdminUser user, Email email) {

        String adminUrl = (hostUrl != null) ? hostUrl + "/admin" : "";

        if ("A".equals(user.getStatus())) {

            String fullName = user.getFirstName() + " " + user.getLastName();
            String password = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveAdminPassword(user);
            adminUserRepo.save(user);

            Context context = new Context();
            context.setVariable("fullName",fullName);
            context.setVariable("username", user.getUserName());
            context.setVariable("password",password);
            context.setVariable("adminUrl",adminUrl);

            mailService.sendMail(email,context);
        }

    }

}
