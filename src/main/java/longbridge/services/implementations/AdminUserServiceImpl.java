package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.Role;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.RoleService;
import longbridge.services.SecurityService;
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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SYLVESTER on 3/30/2017.
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
        AdminUser adminUser =this.adminUserRepo.findFirstByUserName(name) ;
        return convertEntityToDTO(adminUser);
    }

    @Override
    public AdminUserDTO getAdminUser(Long userId) {
        AdminUser adminUser = adminUserRepo.findOne(userId);
        return convertEntityToDTO(adminUser);
    }

    @Override
    public Iterable<AdminUserDTO> getUsers() {
        Iterable<AdminUser> adminUsers =adminUserRepo.findAll();
        return convertEntitiesToDTOs(adminUsers);
    }

//    @Override
//    public Iterable<AdminUser> getAdminUsers() {
//
//        return this.adminUserRepo.findAll();
//     }

    @Override
    public boolean setPassword(AdminUser user, String hashedPassword) {
        boolean ok = false;
        if (user != null) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            adminUserRepo.save(user);
        } else {
            throw new RuntimeException("NO USER FOUND");
        }
        return ok;
    }

    @Override
    @Verifiable(operation="Add Admin",description="Adding a new User")
    public boolean addUser(AdminUserDTO user) {
        boolean ok = false;
        if (user != null) {
           // user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            AdminUser adminUser = new AdminUser();
            adminUser.setFirstName(user.getFirstName());
            adminUser.setLastName(user.getLastName());
            adminUser.setUserName(user.getUserName());
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            adminUser.setRole(role);
            this.adminUserRepo.save(adminUser);
            logger.info("New admin user: {} created", adminUser.getUserName());
            ok=true;
        } else {
            logger.error("USER NOT FOUND");
        }
        return ok;
    }



    @Override
    public void deleteUser(Long id) {
        adminUserRepo.delete(id);
    }

    @Override
    @Verifiable(operation="Updating an Existing User")
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
            logger.info("Admin user {} updated", adminUser.getUserName());
            ok=true;
        } else {
            logger.error("Null user provided");
        }
        return ok;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        boolean ok = false;
        AdminUser user = getUser(userId);

        if (user != null) {
            setPassword(user, newPassword);
            this.adminUserRepo.save(user);

            logger.info("PASSWORD RESET SUCCESSFULLY");
            ok=true;
        }
        return ok;
    }

    @Override
    public boolean changePassword(AdminUserDTO user, String oldPassword, String newPassword) {
        boolean ok=false;

        try {

            if (getUser(user.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return ok;
            }

            if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                AdminUser adminUser = convertDTOToEntity(user);
//                    adminUser.setRole(user.getRole());
                adminUser.setPassword(this.passwordEncoder.encode(newPassword));
                this.adminUserRepo.save(adminUser);
                logger.info("USER {}'s password has been updated", user.getId());
                ok = true;
            } else {
                logger.error("INVALID CURRENT PASSWORD FOR USER {}", user.getId());

            }
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
        return ok;
    }


    @Override
    public boolean generateAndSendPassword(AdminUser user) {
        boolean ok = false;

        try {
            String newPassword = securityService.generatePassword();
            setPassword(user, newPassword);
            sendPassword(user);
            logger.info("PASSWORD GENERATED AND SENT");
        } catch (Exception e) {
            logger.error("ERROR OCCURRED {}", e.getMessage());
        }


        return ok;
    }


    public boolean sendPassword(AdminUser user) {
        //TODO use an smtp server to send new password to user via mail
        return false;
    }
//
//    @Override
//    public boolean updateUser(AdminUserDTO user) {
//        boolean result = false;
//        try {
//            if (user != null) {
//                AdminUser adminUser = convertDTOToEntity(user);
//                this.adminUserRepo.save(adminUser);
//                logger.info("User Successfully Updated");
//            }
//        } catch (Exception e) {
//            logger.info("Could not update admin user",e);
//
//        }
//        return result;
//    }



    private List<AdminUserDTO> convertEntitiesToDTOs(Iterable<AdminUser> adminUsers){
        List<AdminUserDTO> adminUserDTOList = new ArrayList<>();
        for(AdminUser adminUser: adminUsers){
            AdminUserDTO userDTO = convertEntityToDTO(adminUser);
            userDTO.setRole(adminUser.getRole().getName());
            adminUserDTOList.add(userDTO);
        }
        return adminUserDTOList;
    }

    private AdminUserDTO convertEntityToDTO(AdminUser adminUser){
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setFirstName(adminUser.getFirstName());
        adminUserDTO.setLastName(adminUser.getLastName());
        adminUserDTO.setRoleId(adminUser.getRole().getId().toString());
        return  modelMapper.map(adminUser,AdminUserDTO.class);
    }

    private AdminUser convertDTOToEntity(AdminUserDTO adminUserDTO){
        return  modelMapper.map(adminUserDTO,AdminUser.class);
    }

	@Override
	public Page<AdminUserDTO> getUsers(Pageable pageDetails) {
        Page<AdminUser> page = adminUserRepo.findAll(pageDetails);
        List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}

  
}



