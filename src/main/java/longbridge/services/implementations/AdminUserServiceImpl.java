package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.AdminUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.OperationCode;
import longbridge.models.Role;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.*;
//import longbridge.utils.Verifiable;
import java.util.ArrayList;
import java.util.List;

import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MailSessionDefinition;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import longbridge.dtos.AdminUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.Role;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.RoleService;
import longbridge.services.SecurityService;

import longbridge.dtos.AdminUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.Role;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
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

    @Override
    public boolean isValidUsername(String username) {
        boolean isValid = false;
        AdminUser adminUser = adminUserRepo.findFirstByUserName(username);
        if (adminUser == null) {
            isValid = true;
        }
        return isValid;
    }

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
    public AdminUserDTO getUserByName(String name) {
        AdminUser adminUser = this.adminUserRepo.findFirstByUserName(name);
        return convertEntityToDTO(adminUser);
    }

    @Override
    public AdminUserDTO getAdminUser(Long userId) {
        AdminUser adminUser = adminUserRepo.findOne(userId);
        return convertEntityToDTO(adminUser);
    }

    @Override
    public Iterable<AdminUserDTO> getUsers() {
        Iterable<AdminUser> adminUsers = adminUserRepo.findAll();
        return convertEntitiesToDTOs(adminUsers);
    }



    @Override
    @Transactional
//    @Verifiable(operation="Add Admin",description="Adding a new User")
    public boolean addUser(AdminUserDTO user) {
        boolean ok = false;
        if (user != null) {
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
            mailService.send(user.getEmail(), String.format("Your username is %s and password is %s", user.getUserName(), password));
            ok = true;
            logger.info("New admin user: {} created", adminUser.getUserName());

        } else {
            logger.error("Aborted Admin user creation. NULL user supplied");
        }
        return ok;
    }

    @Override
    @Transactional
    public void changeActivationStatus(Long userId) {
        AdminUser user = adminUserRepo.findOne(userId);
        String oldStatus = user.getStatus();
        String newStatus = "ACTIVE".equals(oldStatus) ? "INACTIVE" : "ACTIVE";
        user.setStatus(newStatus);
        adminUserRepo.save(user);
        if ("INACTIVE".equals(oldStatus) && "ACTIVE".equals(newStatus)) {
            String password = passwordService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            mailService.send(user.getEmail(), String.format("Your new password to Admin console is %s and your current username is %s", password, user.getUserName()));
        }
        logger.info("Admin user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);


    }


    @Override
    public void deleteUser(Long id) {
        adminUserRepo.delete(id);
        logger.warn("Admin user with Id {} deleted",id);
    }

    @Override
    @Transactional
//    @Verifiable(operation="Updating an Existing User")
    public boolean updateUser(AdminUserDTO user) {
        boolean ok = false;
        if (user != null) {
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
            ok = true;
            logger.info("Admin user {} updated", adminUser.getUserName());

        } else {
            logger.error("Aborted Admin user update. NULL user supplied");
        }
        return ok;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId) {
        boolean ok = false;
        AdminUser user = getUser(userId);

        if (user != null) {
            String newPassword = passwordService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            this.adminUserRepo.save(user);
            mailService.send(user.getEmail(), "Your new password to Internet banking is " + newPassword);
            ok = true;
            logger.info("Admin user {} password reset successfully", user.getUserName());

        }
        return ok;
    }

    @Override
    @Transactional
    public boolean changePassword(AdminUserDTO user, String oldPassword, String newPassword) {
        boolean ok = false;

        try {

            if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                AdminUser adminUser = adminUserRepo.findOne(user.getId());
                adminUser.setPassword(this.passwordEncoder.encode(newPassword));
                this.adminUserRepo.save(adminUser);
                ok = true;
                logger.info("User {}'s password has been updated", user.getId());

            } else {
                logger.error("Could not change password for admin user {} due to incorrect old password", user.getUserName());
            }
        } catch (Exception e) {
            logger.error("Aborted password change{}", e.toString());
        }
        return ok;
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
        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


}



