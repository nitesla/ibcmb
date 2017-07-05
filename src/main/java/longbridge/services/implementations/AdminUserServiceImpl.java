package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.*;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.RoleRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.*;
//import longbridge.utils.Verifiable;
import java.util.*;

import longbridge.utils.DateFormatter;
import longbridge.utils.HostMaster;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import longbridge.services.AdminUserService;
import longbridge.services.RoleService;
import longbridge.services.SecurityService;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


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
    private RoleService roleService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    HostMaster hostMaster;

    @Autowired
    EntityManager entityManager;

    private Locale locale = LocaleContextHolder.getLocale();


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
    @Verifiable(operation = "ADD_ADMIN_USER", description = "Adding an Admin User")
    public String addUser(AdminUserDTO user) throws InternetBankingException {
        AdminUser adminUser = adminUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (adminUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exist", null, locale));
        }
        try {
            adminUser = new AdminUser();
            adminUser.setFirstName(user.getFirstName());
            adminUser.setLastName(user.getLastName());
            adminUser.setUserName(user.getUserName());
            adminUser.setEmail(user.getEmail());
            adminUser.setPhoneNumber(user.getPhoneNumber());
            adminUser.setCreatedOnDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            adminUser.setRole(role);
            AdminUser newUser = adminUserRepo.save(adminUser);
            createUserOnEntrust(newUser);

            logger.info("New admin user {} created", adminUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);

        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), se);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw new EntrustException(e.getMessage());
            } else {
                throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
            }
        }
    }


    public void createUserOnEntrust(AdminUser adminUser) {
        AdminUser user = adminUserRepo.findFirstByUserName(adminUser.getUserName());
        if(user!=null) {
            if ("".equals(user.getEntrustId())||user.getEntrustId()==null){
            String fullName = adminUser.getFirstName() + " " + adminUser.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    boolean creatResult = securityService.createEntrustUser(adminUser.getUserName(), fullName, true);
                    if (!creatResult) {
                        throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));
                    }

                    boolean contactResult = securityService.addUserContacts(adminUser.getEmail(), adminUser.getPhoneNumber(), true, adminUser.getUserName());
                    if (!contactResult) {
                        logger.error("Failed to add user contacts on Entrust");
                    }
                }
                user.setEntrustId(user.getUserName());
                adminUserRepo.save(user);
            }
            }
        }
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
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null) || ("I".equals(oldStatus)) && "A".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveAdminPassword(user);
                adminUserRepo.save(user);
                sendPostActivateMessage(user, fullName,user.getUserName(),password);
            } else{
            	 user.setStatus(newStatus);
                 adminUserRepo.save(user);
            }

            logger.info("Admin user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }


    @Async
    public void sendPostActivateMessage(User user,String ... args ){
    	//check if records exist send else return
        if("A".equals(user.getStatus())) {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("admin.activation.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("admin.activation.message", null, locale), args))
                    .build();
            mailService.send(email);
        }
    }
    
    
    @Override
    @Transactional
    public String deleteUser(Long id) throws InternetBankingException {
        try {
            AdminUser user = adminUserRepo.findOne(id);
            adminUserRepo.delete(id);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(user.getUserName());
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
    @Verifiable(operation = "UPDATE_ADMIN_USER", description = "Updating an Admin User")
    public String updateUser(AdminUserDTO user) throws InternetBankingException {

        try {
            AdminUser adminUser = adminUserRepo.findById(user.getId());
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
        } catch (DuplicateObjectException e) {
            throw new DuplicateObjectException(e.getMessage());
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.update.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    public String resetPassword(Long userId) throws PasswordException {

        try {
            AdminUser user = adminUserRepo.findOne(userId);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            passwordPolicyService.saveAdminPassword(user);
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
            passwordPolicyService.saveAdminPassword(user);
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

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user);
        if (!"".equals(errorMessage)) {
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
            this.adminUserRepo.save(adminUser);
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
        if (!"".equals(errorMessage)) {
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


    private List<AdminUserDTO> convertEntitiesToDTOs(Iterable<AdminUser> adminUsers)
    {
        List<AdminUserDTO> adminUserDTOList = new ArrayList<>();
        for (AdminUser adminUser : adminUsers) {
            AdminUserDTO userDTO = convertEntityToDTO(adminUser);
            userDTO.setRole(adminUser.getRole().getName());
            adminUserDTOList.add(userDTO);
        }
        return adminUserDTOList;
    }

    private AdminUserDTO convertEntityToDTO(AdminUser adminUser)
    {
        AdminUserDTO adminUserDTO = modelMapper.map(adminUser, AdminUserDTO.class);
        adminUserDTO.setRoleId(adminUser.getRole().getId().toString());
        if (adminUser.getCreatedOnDate() != null) {
            adminUserDTO.setCreatedOn(DateFormatter.format(adminUser.getCreatedOnDate()));
        }
        if (adminUser.getLastLoginDate() != null) {
            adminUserDTO.setLastLogin(DateFormatter.format(adminUser.getLastLoginDate()));
        }

        return adminUserDTO;
    }

    private AdminUser convertDTOToEntity(AdminUserDTO adminUserDTO)
    {
        return modelMapper.map(adminUserDTO, AdminUser.class);
    }

    @Override
    public Page<AdminUserDTO> getUsers(Pageable pageDetails)
    {
        Page<AdminUser> page = adminUserRepo.findAll(pageDetails);
        List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

	@Override
	public Page<AdminUserDTO> findUsers(String pattern, Pageable pageDetails)
    {
		Page<AdminUser> page = adminUserRepo.findUsingPattern(pattern, pageDetails);
		List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}


}
