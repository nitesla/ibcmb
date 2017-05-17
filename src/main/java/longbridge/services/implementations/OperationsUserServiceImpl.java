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
import longbridge.services.*;
import longbridge.utils.ReflectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    MailService mailService;

    @Autowired
    ConfigurationService configService;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CodeService codeService;

    Locale locale = LocaleContextHolder.getLocale();

    public OperationsUserServiceImpl() {

    }


    @Override
    public boolean isValidUsername(String username) {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserName(username);
        return (opsUser == null) ? true : false;

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
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            operationsUserRepo.save(user);
            if ((oldStatus == null) || ("I".equals(oldStatus)) && "A".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                Email email = new Email.Builder().setSender("admin@ibanking.coronationmb.com")
                        .setRecipient(user.getEmail())
                        .setSubject("Internet Banking Operations Console Activation")
                        .setBody(String.format("Your new password to Operations console is %s and your username is %s", password, user.getUserName()))
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
    public String addUser(OperationsUserDTO user) throws InternetBankingException {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserName(user.getUserName());
        if (opsUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }
        try {
            opsUser = new OperationsUser();
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            opsUser.setEmail(user.getEmail());
            opsUser.setCreatedOnDate(new Date());
            String password = passwordPolicyService.generatePassword();
            opsUser.setPassword(passwordEncoder.encode(password));
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            opsUser.setRole(role);
            opsUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            this.operationsUserRepo.save(opsUser);
            logger.info("New Operation user  {} created", opsUser.getUserName());
            return messageSource.getMessage("user.add.success", null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }



    @Override
    @Transactional
    public String updateUser(OperationsUserDTO user) throws InternetBankingException {
        try {
            OperationsUser opsUser = new OperationsUser();
            opsUser.setId((user.getId()));
            opsUser.setVersion(user.getVersion());
            opsUser.setFirstName(user.getFirstName());
            opsUser.setLastName(user.getLastName());
            opsUser.setUserName(user.getUserName());
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
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
        operationsUserRepo.delete(userId);
        logger.warn("Operations user with Id {} deleted", userId);
        return messageSource.getMessage("user.delete.success", null, locale);


    }

    @Override
    @Transactional
    public String resetPassword(Long id) throws InternetBankingException {
        try {
            OperationsUser user = operationsUserRepo.findOne(id);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            this.operationsUserRepo.save(user);
            Email email = new Email.Builder().setSender("admin@ibanking.coronationmb.com")
                    .setRecipient(user.getEmail())
                    .setSubject("Internet Banking Admin Console Password Reset")
                    .setBody(String.format("Your new password to Operations console is %s and your username is %s", newPassword, user.getUserName()))
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

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(),user.getUsedPasswords());
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


        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(),user.getUsedPasswords());
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
            opsUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            operationsUserRepo.save(opsUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }


    @Override
    public String generateAndSendPassword(OperationsUser user) throws InternetBankingException {
        return null;//TODO
    }

    private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser) {
        OperationsUserDTO operationsUserDTO =  modelMapper.map(operationsUser, OperationsUserDTO.class);
        operationsUserDTO.setRole(operationsUser.getRole().getName());
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


}
