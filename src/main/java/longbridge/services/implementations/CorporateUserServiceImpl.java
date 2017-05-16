package longbridge.services.implementations;

import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.Email;
import longbridge.models.Role;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/4/2017.
 */
@Service
public class CorporateUserServiceImpl implements CorporateUserService {

    private CorporateUserRepo corporateUserRepo;

    private CorpLimitRepo corpLimitRepo;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityService securityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    private AccountService accountService;

    Locale locale = LocaleContextHolder.getLocale();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorporateUserServiceImpl(CorporateUserRepo corporateUserRepo, BCryptPasswordEncoder passwordEncoder, SecurityService securityService) {
        this.corporateUserRepo = corporateUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public CorporateUserDTO getUser(Long id) {
        CorporateUser corporateUser = corporateUserRepo.findOne(id);
        return convertEntityToDTO(corporateUser);
    }

    @Override
    public CorporateUser getUserByName(String username) {
        return corporateUserRepo.findByUserName(username);
    }

    @Override
    public Iterable<CorporateUserDTO> getUsers(Corporate corporate) {

        Iterable<CorporateUser> corporateUserDTOList = corporateUserRepo.findAll();
        return convertEntitiesToDTOs(corporateUserDTOList);
    }

    @Override
    public Iterable<CorporateUser> getUsers() {
        return corporateUserRepo.findAll();
    }

    @Override
    public void setPassword(CorporateUser user, String hashedPassword) {

    }

    @Override
    public String updateUser(CorporateUserDTO user) throws InternetBankingException{
        corporateUserRepo.save(convertDTOToEntity(user));
        return messageSource.getMessage("user.update.success",null,locale);
    }

    @Override
    public String addUser(CorporateUserDTO user) throws InternetBankingException {

        CorporateUser corporateUser = corporateUserRepo.findFirstByUserName(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }
        try {
            corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setCreatedOnDate(new Date());
            String password = passwordPolicyService.generatePassword();
            corporateUser.setPassword(passwordEncoder.encode(password));
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            corporateUser.setExpiryDate(new Date());
            corporateUser.setStatus("INACTIVE");
            Corporate corporate = new Corporate();
            corporate.setId(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corporate);
            corporateUserRepo.save(corporateUser);
            logger.info("password {}", password);
            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            CorporateUser user = corporateUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "ACTIVE".equals(oldStatus) ? "INACTIVE" : "ACTIVE";
            user.setStatus(newStatus);
            corporateUserRepo.save(user);
            if ((oldStatus == null) || ("INACTIVE".equals(oldStatus)) && "ACTIVE".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                Email email = new Email.Builder().setSender("admin@ibanking.coronationmb.com")
                        .setRecipient(user.getEmail())
                        .setSubject("Internet Banking Corporate User Activation")
                        .setBody(String.format("Your new password to corporate console is %s and your username is %s", password, user.getUserName()))
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
    public String resetPassword(CorporateUser user) {
        return null;
    }

    @Override
    public String deleteUser(Long userId) throws InternetBankingException{
        corporateUserRepo.delete(userId);
        return messageSource.getMessage("user.delete.success",null,locale);
    }

    @Override
    public void lockUser(CorporateUser user, Date unlockat) {
        //todo
    }

    @Override
    public void changePassword(CorporateUser user, String oldPassword, String newPassword) {
        try {
            if (user == null) {
                logger.error("User does not exist");
                return;
            }

            if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(newPassword));
                corporateUserRepo.save(user);
                logger.info("User {}'s password has been updated", user.getUserName());
                return;
            } else {
                logger.error("Invalid current password supplied for {}", user.getUserName());
                return;
            }
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
        }
    }

    @Override
    public void generateAndSendPassword(CorporateUser user) {
        String newPassword = securityService.generatePassword();
        try {
            if (user == null) {
                logger.error("User does not exist");
                return;
            }
            user.setPassword(this.passwordEncoder.encode(newPassword));
            corporateUserRepo.save(user);
            logger.info("USER {}'s password has been updated", user.getUserName());
            //todo send the email.. messagingService.sendEmail(EmailDetail);
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
        }
    }

    private CorporateUser convertDTOToEntity(CorporateUserDTO CorporateUserDTO){
        return  modelMapper.map(CorporateUserDTO,CorporateUser.class);
    }


    private List<CorporateUserDTO> convertEntitiesToDTOs(Iterable<CorporateUser> corporateUsers) {
        List<CorporateUserDTO> corporateUserDTOList = new ArrayList<>();
        for (CorporateUser corporateUser : corporateUsers) {
            CorporateUserDTO userDTO = convertEntityToDTO(corporateUser);
            userDTO.setRole(corporateUser.getRole().getName());
            corporateUserDTOList.add(userDTO);
        }
        return corporateUserDTOList;
    }

    private CorporateUserDTO convertEntityToDTO(CorporateUser corporateUser) {
        CorporateUserDTO corporateUserDTO = new CorporateUserDTO();
        corporateUserDTO.setFirstName(corporateUser.getFirstName());
        corporateUserDTO.setLastName(corporateUser.getLastName());
        corporateUserDTO.setRoleId(corporateUser.getRole().getId().toString());
        return modelMapper.map(corporateUser, CorporateUserDTO.class);
    }

	@Override
	public Page<CorporateUserDTO> getUsers(Long corpId, Pageable pageDetails) {

        Page<CorporateUser> page = corporateUserRepo.findByCorporateId(corpId, pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
		// TODO Auto-gene
	}



    @Override
	public Page<CorporateUserDTO> getUsers(Pageable pageDetails) {
		// TODO Auto-generated method stub

        Page<CorporateUser> page = corporateUserRepo.findAll(pageDetails);
        List<CorporateUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}


}
