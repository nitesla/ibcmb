package longbridge.services.implementations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.models.Role;
import longbridge.repositories.OperationsUserRepo;
import longbridge.services.MailService;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordService;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */
@Service
public class OperationsUserServiceImpl implements OperationsUserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private OperationsUserRepo operationsUserRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordService passwordService;

    public OperationsUserServiceImpl() {

    }


    @Override
    public boolean isValidUsername(String username) {
        boolean isValid = false;
        OperationsUser opsUser = operationsUserRepo.findFirstByUserName(username);
        if (opsUser == null) {
            isValid = true;
        }
        return isValid;
    }


    @Autowired
    public OperationsUserServiceImpl(OperationsUserRepo operationsUserRepo, BCryptPasswordEncoder passwordEncoder) {
        this.operationsUserRepo = operationsUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OperationsUserDTO getUser(Long id) {
        OperationsUser user = operationsUserRepo.findOne(id);
        return convertEntityToDTO(user);
    }

    @Override
    public Iterable<OperationsUserDTO> getUsers() {
        Iterable<OperationsUser> operationsUsers = operationsUserRepo.findAll();
        return convertEntitiesToDTOs(operationsUsers);
    }

    @Override
    public void setPassword(OperationsUser user, String password) {

    }


    @Override
    public OperationsUserDTO getUserByName(String name) {
        OperationsUser opsUser = this.operationsUserRepo.findFirstByUserName(name);
        return convertEntityToDTO(opsUser);
    }

    @Override
    public boolean addUser(OperationsUserDTO user) {
        boolean ok = false;
        if (user != null) {
            OperationsUser opsUser = new OperationsUser();
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setEmail(user.getEmail());
            opsUser.setDateCreated(new Date());
            opsUser.setStatus("ACTIVE");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 2);
            opsUser.setExpiryDate(calendar.getTime());
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            opsUser.setRole(role);
            String password = passwordService.generatePassword();
            opsUser.setPassword(passwordEncoder.encode(password));
            this.operationsUserRepo.save(opsUser);
            logger.info("Your new password is {}",password);
            mailService.send(user.getEmail(), String.format("Your username is %s and password is %s", user.getUserName(), password));
            logger.info("New operations user: {} created", opsUser.getUserName());
            ok = true;
        } else {
            logger.error("USER NOT FOUND");
        }
        return ok;

    }

    @Override
    public boolean updateUser(OperationsUserDTO userDTO) {
        boolean ok = false;
        OperationsUser operationsUser = new OperationsUser();
        operationsUser.setId(userDTO.getId());
        operationsUser.setVersion(userDTO.getVersion());
        operationsUser.setFirstName(userDTO.getFirstName());
        operationsUser.setLastName(userDTO.getLastName());
        operationsUser.setUserName(userDTO.getUserName());
        Role role = new Role();
        role.setId(Long.parseLong(userDTO.getRoleId()));
        operationsUser.setRole(role);
        operationsUserRepo.save(operationsUser);
        ok = true;

        return ok;
    }

    @Override
    public void deleteUser(Long userId) {
        operationsUserRepo.delete(userId);
    }

    @Override
    public void resetPassword(OperationsUser user) {

    }

    @Override
    public boolean changePassword(OperationsUserDTO user, String oldPassword, String newPassword) {
        boolean ok = false;

        try {


            if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
                opsUser.setPassword(this.passwordEncoder.encode(newPassword));
                this.operationsUserRepo.save(opsUser);
                logger.info("User {}'s password has been updated", user.getId());
                ok = true;
            } else {
                logger.error("INVALID CURRENT PASSWORD FOR USER {}", user.getId());

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR OCCURRED {}", e.getMessage());
        }
        return ok;
    }

    @Override
    public void generateAndSendPassword() {
        //TODO

    }

    private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser) {
        return modelMapper.map(operationsUser, OperationsUserDTO.class);
    }

    private OperationsUser convertDTOToEntity(OperationsUserDTO operationsUserDTO) {
        return modelMapper.map(operationsUserDTO, OperationsUser.class);
    }

    private List<OperationsUserDTO> convertEntitiesToDTOs(Iterable<OperationsUser> operationsUsers) {
        List<OperationsUserDTO> operationsUserDTOList = new ArrayList<>();
        for (OperationsUser operationsUser : operationsUsers) {
            OperationsUserDTO userDTO = convertEntityToDTO(operationsUser);
            userDTO.setRole(operationsUser.getRole().getName());
            operationsUserDTOList.add(userDTO);
        }
        return operationsUserDTOList;
    }

    @Override
    public Page<OperationsUserDTO> getUsers(Pageable pageDetails) {
        Page<OperationsUser> page = operationsUserRepo.findAll(pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


}
