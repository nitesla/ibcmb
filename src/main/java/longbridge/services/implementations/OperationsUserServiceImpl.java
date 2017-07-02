package longbridge.services.implementations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.Code;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EmbeddableType;
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
    private CodeService codeService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    EntityManager entityManager;

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
    public OperationsUserDTO getUser(Long id) {
        OperationsUser user = operationsUserRepo.findOne(id);
        return convertEntityToDTO(user);
    }

    @Override
    public Page<OperationsUserDTO> findUsers(OperationsUserDTO example, Pageable pageDetails) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase().withIgnorePaths("version", "noOfAttempts").withIgnoreNullValues();
        OperationsUser entity = convertDTOToEntity(example);
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
    @Verifiable(operation="UPDATE_OPS_STATUS",description="Change Operations User Activation Status")
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            operationsUserRepo.save(user);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null)) {//User was just created
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveOpsPassword(user);
                operationsUserRepo.save(user);

                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("ops.create.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("ops.create.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);
            } else if (("I".equals(oldStatus)) && "A".equals(newStatus)) {//User is being reactivated
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveOpsPassword(user);
                operationsUserRepo.save(user);
                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("ops.reactivation.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("ops.reactivation.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);
            }


            logger.info("Operations user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }


    @Override
    public OperationsUser getUserByName(String name) {
        OperationsUser opsUser = this.operationsUserRepo.findFirstByUserName(name);
        return opsUser;
    }

    @Override
    @Transactional
    @Verifiable(operation="ADD_OPS_USER",description="Adding an Operations User")
    public String addUser(OperationsUserDTO user) throws InternetBankingException {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (opsUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exist", null, locale));
        }
        try {
            opsUser = new OperationsUser();
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setEmail(user.getEmail());
            opsUser.setPhoneNumber(user.getPhoneNumber());
            opsUser.setCreatedOnDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            opsUser.setRole(role);
            creatUserOnEntrust(opsUser);
            operationsUserRepo.save(opsUser);
            logger.info("New Operation user  {} created", opsUser.getUserName());
            return messageSource.getMessage("user.add.success", null, LocaleContextHolder.getLocale());
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), se);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));
            } else {
                throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
            }
        }


    }

    private void creatUserOnEntrust(OperationsUser opsUser) throws EntrustException{
        String fullName = opsUser.getFirstName() + " " + opsUser.getLastName();
        SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");

        if (setting != null && setting.isEnabled()) {
            if ("YES".equalsIgnoreCase(setting.getValue())) {
                boolean result = securityService.createEntrustUser(opsUser.getUserName(), fullName, true);
                if (!result) {
                    throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));
                }

                boolean contactResult = securityService.addUserContacts(opsUser.getEmail(),opsUser.getPhoneNumber(),true,opsUser.getUserName());
                if(!contactResult){
                    logger.error("Failed to add user contacts on Entrust");
                }
            }
        }
    }

    @Override
    @Transactional
    @Verifiable(operation="UPDATE_OPS_USER",description="Updating an Operations User")
    public String updateUser(OperationsUserDTO user) throws InternetBankingException {
        try {
            OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
            entityManager.detach(opsUser);
            opsUser.setVersion(user.getVersion());
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setPhoneNumber(user.getPhoneNumber());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            opsUser.setRole(role);
            this.operationsUserRepo.save(opsUser);
            logger.info("Operations user {} updated", opsUser.getUserName());
            return messageSource.getMessage("user.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.update.failure", null, locale), e);
        }
    }


    @Override

    public String deleteUser(Long userId) throws InternetBankingException {
        try {
            OperationsUser opsUser = operationsUserRepo.findOne(userId);
            operationsUserRepo.delete(userId);
            logger.warn("Operations user with Id {} deleted", userId);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(opsUser.getUserName());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (InternetBankingSecurityException se) {
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
            this.operationsUserRepo.save(opsUser);
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
            operationsUserDTO.setCreatedOn(DateFormatter.format(operationsUser.getCreatedOnDate()));
        }
        if (operationsUser.getLastLoginDate() != null) {
            operationsUserDTO.setLastLogin(DateFormatter.format(operationsUser.getLastLoginDate()));
        }
        Code code = codeService.getByTypeAndCode("USER_STATUS", operationsUser.getStatus());
        if (code != null) {
            operationsUserDTO.setStatus(code.getDescription());
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
	 	Page<OperationsUser> page = operationsUserRepo.findUsingPattern(pattern,pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
	}


}
