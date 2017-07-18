package longbridge.services.implementations;


import longbridge.api.AccountInfo;
import longbridge.api.CustomerDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.*;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.FailedLoginService;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
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
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by SYLVESTER on 3/29/2017.
 */

@Service
public class RetailUserServiceImpl implements RetailUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private MailService mailService;

    @Autowired
    private FailedLoginService failedLoginService;

    private Locale locale = LocaleContextHolder.getLocale();

    private CodeService codeService;
    private AccountService accountService;
    private SecurityService securityService;
    private RoleService roleService;
    private IntegrationService integrationService;
    @Autowired
    private ConfigurationService configService;

    public RetailUserServiceImpl() {
    }

    @Autowired
    public RetailUserServiceImpl(CodeService codeService, AccountService accountService, SecurityService securityService, RoleService roleService, IntegrationService integrationService) {
        this.codeService = codeService;
        this.accountService = accountService;
        this.securityService = securityService;
        this.roleService = roleService;
        this.integrationService = integrationService;
    }

    @Override
    public RetailUserDTO getUser(Long id) {
        RetailUser retailUser = this.retailUserRepo.findOne(id);
        return convertEntityToDTO(retailUser);
    }

    @Override
    public String unlockUser(Long id) throws InternetBankingException {

        RetailUser user = retailUserRepo.findOne(id);
        if (!"L".equals(user.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("user.unlocked", null, locale));
        }
        try {
            failedLoginService.unLockUser(user);
            return messageSource.getMessage("unlock.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("unlock.failure", null, locale));

        }
    }

    @Override
    public RetailUser getUserByName(String name) {
        RetailUser retailUser = this.retailUserRepo.findFirstByUserNameIgnoreCase(name);
        return retailUser;
    }

    @Override
    public RetailUser getUserByEntrustId(String entrustId) {
        RetailUser retailUser = this.retailUserRepo.findFirstByEntrustIdIgnoreCase(entrustId);
        return retailUser;
    }

    @Override
    public RetailUser getUserByEmail(String email) {
        RetailUser retailUser = this.retailUserRepo.findFirstByEmailIgnoreCase(email);
        return retailUser;
    }

    @Override
    public RetailUserDTO getUserDTOByName(String name) {
        RetailUser retailUser = this.retailUserRepo.findFirstByUserNameIgnoreCase(name);
        return convertEntityToDTO(retailUser);
    }

    @Override
    public Iterable<RetailUserDTO> getUsers() {
        Iterable<RetailUser> retailUsers = retailUserRepo.findAll();
        return convertEntitiesToDTOs(retailUsers);
    }

    @Override
    public RetailUser getUserByCustomerId(String custId) {
        RetailUser retailUser = this.retailUserRepo.findFirstByCustomerId(custId);
        return retailUser;
    }


    @Override
    @Transactional
    public String addUser(RetailUserDTO user, CustomerDetails details) throws InternetBankingException {
        try {
            RetailUser retailUser = getUserByName(user.getUserName());
            if (retailUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.exist", null, locale));
            }
            RetailUser retUser = getUserByCustomerId(user.getCustomerId());
            if (retUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.exist", null, locale));
            }

            retailUser = new RetailUser();
            retailUser.setUserName(user.getUserName());
            retailUser.setCustomerId(details.getCifId());
            retailUser.setFirstName(details.getFirstName());
            retailUser.setLastName(details.getLastName());
            retailUser.setEmail(details.getEmail());
            retailUser.setPhoneNumber(details.getPhone());
            retailUser.setCreatedOnDate(new Date());
            retailUser.setBirthDate(user.getBirthDate());
            retailUser.setBvn(user.getBvn());
            SettingDTO settingDTO = configService.getSettingByName("DEFAULT_RETAIL_ROLE");
            Role role = roleService.getTheRole(settingDTO.getValue());
            retailUser.setRole(role);
            retailUser.setStatus("A");
            retailUser.setAlertPreference(codeService.getByTypeAndCode("ALERT_PREFERENCE", "BOTH"));
            String errorMsg = passwordPolicyService.validate(user.getPassword(), null);
            if (!"".equals(errorMsg)) {
                throw new PasswordPolicyViolationException(errorMsg);
            }

            String phoneNo = details.getPhone();
            String fullName = details.getFirstName() + " " + details.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {

                    retailUser.setEntrustId(user.getUserName());

                    String defaultGroup = configService.getSettingByName("DEF_ENTRUST_RET_GRP").getValue();

                    retailUser.setEntrustGroup(defaultGroup);

                    createEntrustUser(retailUser.getEntrustId(), retailUser.getEntrustGroup(), fullName, true);

                    addUserContact(retailUser.getEntrustId(), retailUser.getEntrustGroup(), phoneNo, user.getEmail());

                    setEntrustUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup(), user.getSecurityQuestion(), user.getSecurityAnswer());

                    setEntrustUserMutualAuth(retailUser.getEntrustId(), retailUser.getEntrustGroup(), user.getCaptionSec(), user.getPhishingSec());
                }
            }


            retailUser.setPassword(passwordEncoder.encode(user.getPassword()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveRetailPassword(retailUser);
            retailUserRepo.save(retailUser);

            Collection<AccountInfo> accounts = integrationService.fetchAccounts(details.getCifId());
            for (AccountInfo acct : accounts) {
                accountService.AddFIAccount(details.getCifId(), acct);
            }

            logger.info("Retail user {} created", user.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    private void createEntrustUser(String username, String group, String fullName, boolean enableOtp) {
        try {
            securityService.createEntrustUser(username, group, fullName, true);
        } catch (InternetBankingSecurityException e) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void addUserContact(String username, String group, String phone, String email) {
        try {
            securityService.addUserContacts(email, phone, true, username, group);
        } catch (InternetBankingSecurityException e) {
            securityService.deleteEntrustUser(username, group);
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void setEntrustUserQA(String username, String group, List<String> securityQuestion, List<String> securityAnswer) {
        try {
            securityService.setUserQA(username, group, securityQuestion, securityAnswer);
        } catch (InternetBankingSecurityException e) {
            securityService.deleteEntrustUser(username, group);
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void setEntrustUserMutualAuth(String username, String group, String captionSec, String phishingSec) {
        try {
            securityService.setMutualAuth(username, group, captionSec, phishingSec);

        } catch (InternetBankingSecurityException e) {
            securityService.deleteEntrustUser(username, group);
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    @Override
    public String deleteUser(Long userId) throws InternetBankingException {
        try {

            RetailUser retailUser = retailUserRepo.findOne(userId);
            retailUserRepo.delete(userId);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(retailUser.getEntrustId(), retailUser.getEntrustGroup());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_RETAIL_STATUS", description = "Change Retail User Activation Status")
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            RetailUser user = retailUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null) || ("I".equals(oldStatus)) && "A".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                passwordPolicyService.saveRetailPassword(user);
                retailUserRepo.save(user);
                sendActivationMessage(user, fullName, user.getUserName(), password);

            } else {
                user.setStatus(newStatus);
                retailUserRepo.save(user);

            }

            logger.info("Retail user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
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


    @Async
    public void sendPostPasswordResetMessage(User user, String... args) {
        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("customer.password.reset.message", null, locale), args))
                    .build();
            mailService.send(email);
        } catch (MailException me) {
            logger.error("Failed to send reactivation mail to {}", user.getEmail(), me);
        }

    }


    @Async
    public void sendPostActivateMessage(User user, String... args) {
        try {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.reactivation.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("customer.reactivation.message", null, locale), args))
                    .build();
            mailService.send(email);
        } catch (MailException me) {
            logger.error("Failed to send reactivation mail to {}", user.getEmail(), me);
        }

    }


    @Async
    private void sendActivationMessage(User user, String... args) {
        RetailUser corpUser = getUserByName(user.getUserName());
        if ("A".equals(corpUser.getStatus())) {
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.reactivation.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("customer.reactivation.message", null, locale), args))
                    .build();
            mailService.send(email);
        }
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) throws PasswordException {


            RetailUser user = retailUserRepo.findOne(userId);
            logger.info("this is the user status{}",user.getStatus());
            if("I".equals(user.getStatus()))
            {
                throw new InternetBankingException(messageSource.getMessage("users.deactivated", null, locale));
            }
        try {
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveRetailPassword(user);
            retailUserRepo.save(user);
            String fullName = user.getFirstName() + " " + user.getLastName();
            sendPostPasswordResetMessage(user, fullName, user.getUserName(), newPassword);
            logger.info("Retail user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
           }
        catch (MailException me)
        {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(RetailUser user, CustResetPassword custResetPassword) {

        String errorMessage = passwordPolicyService.validate(custResetPassword.getNewPassword(), user);
        if (!"".equals(errorMessage)) {
            logger.info("Password violation");
            throw new PasswordPolicyViolationException(errorMessage);
        }

        if (!custResetPassword.getNewPassword().equals(custResetPassword.getConfirmPassword())) {
            logger.info("Password mismatch");
            throw new PasswordMismatchException();
        }

        try {
            RetailUser retailUser = retailUserRepo.findOne(user.getId());
            retailUser.setPassword(this.passwordEncoder.encode(custResetPassword.getNewPassword()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveRetailPassword(retailUser);
            this.retailUserRepo.save(retailUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }

    @Override
    public String changePassword(RetailUser user, CustChangePassword changePassword) throws PasswordException {
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
            RetailUser retailUser = retailUserRepo.findOne(user.getId());
            retailUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            passwordPolicyService.saveRetailPassword(retailUser);
            this.retailUserRepo.save(retailUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }

    @Override
    public boolean updateUser(RetailUserDTO user) {
        boolean ok = false;
        try {
            if (user != null) {
                RetailUser retailUser = convertDTOToEntity(user);
                this.retailUserRepo.save(retailUser);
                logger.info("USER SUCCESSFULLY UPDATED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public boolean AddAccount(RetailUser user, AccountDTO accountDTO) {
        return accountService.AddAccount(user.getCustomerId(), accountDTO);
    }


    @Override
    public boolean changeAlertPreference(RetailUserDTO user, AlertPref alertPreference) {
        boolean ok = false;
        try {
            if (getUser(user.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return ok;
            }

            RetailUser retailUser = convertDTOToEntity(user);
            Code code = codeService.getByTypeAndCode("ALERT_PREFERENCE", alertPreference.getCode());
            retailUser.setAlertPreference(code);
            this.retailUserRepo.save(retailUser);
            logger.info("USER {}'s alert preference set", user.getId());
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR OCCURRED {}", e.getMessage());
        }
        return ok;
    }


    private RetailUserDTO convertEntityToDTO(RetailUser retailUser) {
        RetailUserDTO retailUserDTO = modelMapper.map(retailUser, RetailUserDTO.class);
        if (retailUser.getCreatedOnDate() != null) {
            retailUserDTO.setCreatedOn(DateFormatter.format(retailUser.getCreatedOnDate()));
        }
        if (retailUser.getLastLoginDate() != null) {
            retailUserDTO.setLastLogin(DateFormatter.format(retailUser.getLastLoginDate()));
        }
        return retailUserDTO;
    }

    private RetailUser convertDTOToEntity(RetailUserDTO retailUserDTO) {
        return modelMapper.map(retailUserDTO, RetailUser.class);
    }

    private List<RetailUserDTO> convertEntitiesToDTOs(Iterable<RetailUser> RetailUsers) {
        List<RetailUserDTO> retailUserDTOList = new ArrayList<>();
        for (RetailUser retailUser : RetailUsers) {
            RetailUserDTO userDTO = convertEntityToDTO(retailUser);
            retailUserDTOList.add(userDTO);
        }
        return retailUserDTOList;
    }

    @Override
    public Page<RetailUserDTO> getUsers(Pageable pageDetails) {
        Page<RetailUser> page = retailUserRepo.findAll(pageDetails);
        List<RetailUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<RetailUserDTO> pageImpl = new PageImpl<RetailUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    @Override
    public String retrieveUsername(String accountNumber, String securityQuestion, String securityAnswer) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            //invalid account number
            return "Invalid Account Number";
        }
        logger.info("Account: {}", account.toString());
        String customerId = account.getCustomerId();

        RetailUser retailUser = retailUserRepo.findFirstByCustomerId(customerId);
        if (retailUser == null) {
            //customer doesn't have a user
            return "User not found";
        }

        logger.info("Retail user: {}", retailUser);

        //TODO confirm security question
        if (!securityService.validateSecurityQuestion(retailUser, securityQuestion, securityAnswer)) {
            return "Invalid Security Question or Answer";
        }

        return retailUser.getUserName();
    }

    @Override
    public Page<RetailUserDTO> findUsers(String pattern, Pageable pageDetails) {
        Page<RetailUser> page = retailUserRepo.findUsingPattern(pattern, pageDetails);
        List<RetailUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<RetailUserDTO> pageImpl = new PageImpl<RetailUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

}
