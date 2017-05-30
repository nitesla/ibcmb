package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import longbridge.models.Email;
import longbridge.models.Role;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.*;
//import longbridge.utils.Verifiable;
import java.util.*;

import longbridge.utils.DateFormatter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import longbridge.services.AdminUserService;
import longbridge.services.RoleService;
import longbridge.services.SecurityService;


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
    private VerificationRepo verificationRepo;

    @Autowired
    private SecurityService securityService;

    @Autowired
    RoleService roleService;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CodeService codeService;

    @Autowired
    ConfigurationService configService;

    Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    public AdminUserServiceImpl(AdminUserRepo adminUserRepo, BCryptPasswordEncoder passwordEncoder) {

        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUserServiceImpl() {
    }

    @Override
    public AdminUser getUser(Long id) {
        return this.adminUserRepo.findOne(id);
    }

    @Override
    public AdminUser getUserByName(String name) {
        return this.adminUserRepo.findFirstByUserName(name);

    }

    @Override
    public AdminUserDTO getAdminUser(Long userId) {
        AdminUser adminUser = adminUserRepo.findOne(userId);
        return convertEntityToDTO(adminUser);
    }

    @Override
    public boolean userExists(String username) throws InternetBankingException {
        AdminUser adminUser = adminUserRepo.findFirstByUserName(username);
        return (adminUser != null) ? true : false;
    }


    @Override
    public Iterable<AdminUserDTO> getUsers() {
        Iterable<AdminUser> adminUsers = adminUserRepo.findAll();
        return convertEntitiesToDTOs(adminUsers);
    }


    @Override
    @Transactional
//    @Verifiable(operation="Add Admin",description="Adding a new User")
    public String addUser(AdminUserDTO user) throws InternetBankingException {
        AdminUser adminUser = adminUserRepo.findFirstByUserName(user.getUserName());
        if (adminUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }
        try {
            adminUser = new AdminUser();
            adminUser.setFirstName(user.getFirstName());
            adminUser.setLastName(user.getLastName());
            adminUser.setUserName(user.getUserName());
            adminUser.setEmail(user.getEmail());
            adminUser.setPhoneNumber(user.getPhoneNumber());
            adminUser.setCreatedOnDate(new Date());
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            adminUser.setRole(role);
            adminUserRepo.save(adminUser);
            String fullName = adminUser.getFirstName() + " " + adminUser.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.createEntrustUser(adminUser.getUserName(), fullName, true);
                }
            }
            logger.info("New admin user {} created", adminUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            AdminUser user = adminUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            adminUserRepo.save(user);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null)) {//User was just created
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                adminUserRepo.save(user);

                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("admin.create.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("admin.create.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);


            } else if (("I".equals(oldStatus)) && "A".equals(newStatus)) {//User is being reactivated
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                adminUserRepo.save(user);
                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("admin.reactivation.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("admin.reactivation.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);

            }

            logger.info("Admin user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }


    @Override
    @Transactional
    public String deleteUser(Long id) throws InternetBankingException {
        try {
            AdminUser user = adminUserRepo.findOne(id);
            adminUserRepo.delete(id);
            String fullName = user.getFirstName() + " " + user.getLastName();

            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(user.getUserName(), fullName);
                }
            }
            logger.warn("Admin user {} deleted", user.getUserName());
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
//    @Verifiable(operation="Updating an Existing User")
    public String updateUser(AdminUserDTO user) throws InternetBankingException {

        try {
            AdminUser adminUser = adminUserRepo.findOne(user.getId());
            adminUser.setVersion(user.getVersion());
            adminUser.setFirstName(user.getFirstName());
            adminUser.setLastName(user.getLastName());
            adminUser.setUserName(user.getUserName());
            adminUser.setEmail(user.getEmail());
            adminUser.setPhoneNumber(user.getPhoneNumber());
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            adminUser.setRole(role);
            this.adminUserRepo.save(adminUser);
            logger.info("Admin user {} updated", adminUser.getUserName());
            return messageSource.getMessage("user.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.update.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(Long userId) throws PasswordException {

        try {
            AdminUser user = adminUserRepo.findOne(userId);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            String fullName = user.getFirstName() + " " + user.getLastName();
            this.adminUserRepo.save(user);
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("admin.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("admin.password.reset.message", null, locale), fullName, newPassword))
                    .build();
            mailService.send(email);
            logger.info("Admin user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(String username) throws PasswordException {

        try {
            AdminUser user = adminUserRepo.findFirstByUserName(username);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            String fullName = user.getFirstName() + " " + user.getLastName();
            this.adminUserRepo.save(user);
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("admin.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("admin.password.reset.message", null, locale), fullName, newPassword))
                    .build();
            mailService.send(email);
            logger.info("Admin user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public String changePassword(AdminUser user, ChangePassword changePassword) throws PasswordException {

        if (!this.passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user.getUsedPasswords());
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            AdminUser adminUser = adminUserRepo.findOne(user.getId());
            adminUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            adminUser.setUsedPasswords(getUsedPasswords(changePassword.getNewPassword(), adminUser.getUsedPasswords()));
            adminUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            this.adminUserRepo.save(adminUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }

    private String getUsedPasswords(String newPassword, String oldPasswords) {
        StringBuilder builder = new StringBuilder();
        if (oldPasswords != null) {
            builder.append(oldPasswords);
        }
        builder.append(passwordEncoder.encode(newPassword) + ",");
        return builder.toString();
    }


    private void sendUserCredentials(AdminUser user, String password) throws InternetBankingException {
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject("Creation on Internet Banking Admin Console")
                .setBody(String.format("You have been created on the Internet Banking Administration console.\nYour username is %s and your password is %s. \nThank you.", user.getUserName(), password))
                .build();
        mailService.send(email);
    }

    @Override
    @Transactional
    public String changeDefaultPassword(AdminUser user, ChangeDefaultPassword changePassword) throws PasswordException {

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user.getUsedPasswords());
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            AdminUser adminUser = adminUserRepo.findOne(user.getId());
            adminUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            adminUser.setUsedPasswords(getUsedPasswords(changePassword.getNewPassword(), adminUser.getUsedPasswords()));
            adminUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            this.adminUserRepo.save(adminUser);
            logger.info("User {}'s password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }

    @Override
    public boolean generateAndSendPassword(AdminUser user) {
        return false;// TODO
    }


    public boolean sendPassword(AdminUser user) {
        //TODO use an smtp server to send new password to user via mail
        return false;
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
            adminUserDTO.setCreatedOn(DateFormatter.format(adminUser.getCreatedOnDate()));
        }
        if (adminUser.getLastLoginDate() != null) {
            adminUserDTO.setLastLogin(DateFormatter.format(adminUser.getLastLoginDate()));
        }
        Code code = codeService.getByTypeAndCode("USER_STATUS", adminUser.getStatus());
        if (code != null) {
            adminUserDTO.setStatus(code.getDescription());
        }
        return adminUserDTO;
    }

    private AdminUser convertDTOToEntity(AdminUserDTO adminUserDTO) {
        return modelMapper.map(adminUserDTO, AdminUser.class);
    }

    @Override
    public Page<AdminUserDTO> getUsers(Pageable pageDetails) {
        Page<AdminUser> page = adminUserRepo.findAll(pageDetails);
        List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


}
