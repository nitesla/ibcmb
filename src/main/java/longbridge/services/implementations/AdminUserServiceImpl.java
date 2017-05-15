package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AdminUser;
import longbridge.models.Role;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.*;
//import longbridge.utils.Verifiable;
import java.util.ArrayList;
import java.util.List;

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
import java.util.Calendar;
import java.util.Date;

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
    PasswordService passwordService;

    @Autowired
    MessageSource messageSource;


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
        return  this.adminUserRepo.findFirstByUserName(name);

    }

    @Override
    public AdminUserDTO getAdminUser(Long userId) {
        AdminUser adminUser = adminUserRepo.findOne(userId);
        return convertEntityToDTO(adminUser);
    }

    @Override
    public boolean isUsernameExist(String username) throws InternetBankingException {
        AdminUser adminUser = adminUserRepo.findFirstByUserName(username);
        return (adminUser == null)?true:false;
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
        AdminUser adminUser = new AdminUser();
        adminUser.setFirstName(user.getFirstName());
        adminUser.setLastName(user.getLastName());
        adminUser.setUserName(user.getUserName());
        adminUser.setEmail(user.getEmail());
        adminUser.setDateCreated(new Date());
        String password = passwordService.generatePassword();
        adminUser.setPassword(passwordEncoder.encode(password));
        Role role = new Role();
        role.setId(Long.parseLong(user.getRoleId()));
        adminUser.setRole(role);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 2);
        adminUser.setExpiryDate(calendar.getTime());
        this.adminUserRepo.save(adminUser);
        mailService.send(user.getEmail(), "Mail from Internet Banking",String.format("Your username is %s and password is %s", user.getUserName(), password));
        logger.info("New admin user: {} created", adminUser.getUserName());
        return messageSource.getMessage("user.admin.create.success", null, LocaleContextHolder.getLocale());
    }

    @Override
    @Transactional
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        AdminUser user = adminUserRepo.findOne(userId);
        String oldStatus = user.getStatus();
        String newStatus = "ACTIVE".equals(oldStatus) ? "INACTIVE" : "ACTIVE";
        user.setStatus(newStatus);
        adminUserRepo.save(user);
        if ("INACTIVE".equals(oldStatus) && "ACTIVE".equals(newStatus)) {
            String password = passwordService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            mailService.send(user.getEmail(),"Mail from Internet Banking", String.format("Your new password to Admin console is %s and your current username is %s", password, user.getUserName()));
        }
        logger.info("Admin user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
        return messageSource.getMessage("user.status.success",null,LocaleContextHolder.getLocale());

    }


    @Override
    public String deleteUser(Long id) throws InternetBankingException {
        adminUserRepo.delete(id);
        logger.warn("Admin user with Id {} deleted", id);
        return messageSource.getMessage("user.delete", null, LocaleContextHolder.getLocale());

    }

    @Override
    @Transactional
//    @Verifiable(operation="Updating an Existing User")
    public String updateUser(AdminUserDTO user) throws InternetBankingException {
        AdminUser adminUser = new AdminUser();
        adminUser.setId((user.getId()));
        adminUser.setVersion(user.getVersion());
        adminUser.setFirstName(user.getFirstName());
        adminUser.setLastName(user.getLastName());
        adminUser.setUserName(user.getUserName());
        Role role = new Role();
        role.setId(Long.parseLong(user.getRoleId()));
        adminUser.setRole(role);
        this.adminUserRepo.save(adminUser);
        logger.info("Admin user {} updated", adminUser.getUserName());
        return messageSource.getMessage("user.update.success", null, LocaleContextHolder.getLocale());
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) throws InternetBankingException {
        AdminUser user = getUser(userId);
        String newPassword = passwordService.generatePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setExpiryDate(new Date());
        this.adminUserRepo.save(user);
        mailService.send(user.getEmail(), "Mail from Internet Banking","Your new password to Internet banking is " + newPassword);
        logger.info("Admin user {} password reset successfully", user.getUserName());
        return messageSource.getMessage("password.reset.success", null, LocaleContextHolder.getLocale());
    }

    @Override
    @Transactional
    public String changePassword(AdminUser user, String oldPassword, String newPassword) throws InternetBankingException {
        if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
            AdminUser adminUser = adminUserRepo.findOne(user.getId());
            adminUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.adminUserRepo.save(adminUser);
            logger.info("User {}'s password has been updated", user.getId());

        } else {
            logger.error("Could not change password for admin user {} due to incorrect old password", user.getUserName());
        }
        return messageSource.getMessage("password.change.success",null,LocaleContextHolder.getLocale());
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
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setFirstName(adminUser.getFirstName());
        adminUserDTO.setLastName(adminUser.getLastName());
        adminUserDTO.setRoleId(adminUser.getRole().getId().toString());
        return modelMapper.map(adminUser, AdminUserDTO.class);
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



