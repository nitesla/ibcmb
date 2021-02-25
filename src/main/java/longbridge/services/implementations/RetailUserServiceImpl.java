package longbridge.services.implementations;


import longbridge.api.AccountInfo;
import longbridge.api.CustomerDetails;
import longbridge.api.omnichannel.dto.CustomerInfo;
import longbridge.api.omnichannel.dto.RetailUserCredentials;
import longbridge.apilayer.models.WebhookResponse;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.apidtos.MobileRetailUserDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.*;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.FailedLoginService;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.RetailWebHook;
import longbridge.utils.Verifiable;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Created by SYLVESTER on 3/29/2017.
 */

@Service
public class RetailUserServiceImpl implements RetailUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();
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
    private CodeService codeService;
    private AccountService accountService;
    private SecurityService securityService;
    private RoleService roleService;
    private IntegrationService integrationService;

    @Autowired
    private SettingsService configService;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private RetailWebHook retailWebHook;

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
        RetailUser retailUser = this.retailUserRepo.findById(id).get();
        return convertEntityToDTO(retailUser);
    }

    @Override
    public Long countUser() {
        return retailUserRepo.count();
    }

    @Override
    public String unlockUser(Long id)  {

        RetailUser user = retailUserRepo.findById(id).get();
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
        return this.retailUserRepo.findFirstByUserNameIgnoreCase(name);
    }

    @Override
    public RetailUser getUserByEntrustId(String entrustId) {
        return this.retailUserRepo.findFirstByEntrustIdIgnoreCase(entrustId);
    }

    @Override
    public RetailUser getUserByEmail(String email) {
        return this.retailUserRepo.findFirstByEmailIgnoreCase(email);
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
        return this.retailUserRepo.findFirstByCustomerId(custId);
    }


    @Override
    @Transactional
    public String addUser(RetailUserDTO user, CustomerDetails details)  {
        try {
            RetailUser retailUser = getUserByName(user.getUserName());
            if (retailUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
            }
            RetailUser retUser = getUserByCustomerId(user.getCustomerId());
            if (retUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.reg.exists", null, locale));
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
            retailUser.setAlertPreference("BOTH");
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

                    createEntrustUser(retailUser.getEntrustId(), retailUser.getEntrustGroup(), fullName);

                    addUserContact(retailUser.getEntrustId(), retailUser.getEntrustGroup(), phoneNo, user.getEmail());

                    setEntrustUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup(), user.getSecurityQuestion(), user.getSecurityAnswer());

                    setEntrustUserMutualAuth(retailUser.getEntrustId(), retailUser.getEntrustGroup(), user.getCaptionSec(), user.getPhishingSec());
                }
            }


            retailUser.setPassword(passwordEncoder.encode(user.getPassword()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());


            List<AccountInfo> accounts = integrationService.fetchAccounts(details.getCifId());

//            List<AccountInfo> transactionalAccounts = accountService.getTransactionalAccounts(accounts);

            for (AccountInfo acct : accounts) {
                accountService.AddFIAccount(details.getCifId(), acct);
            }

            passwordPolicyService.saveRetailPassword(retailUser);
//            coverageService.addCoverageForNewRetail(retailUser);
            retailUserRepo.save(retailUser);

            logger.info("Retail user {} created", user.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    @Override
    public String addUser(RetailUserDTO user)  {
        try {
            RetailUser retailUser = getUserByName(user.getUserName());
            if (retailUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
            }
            RetailUser retUser = getUserByCustomerId(user.getCustomerId());
            if (retUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.reg.exists", null, locale));
            }

            retailUser = new RetailUser();
            retailUser.setUserName(user.getUserName());
            retailUser.setCustomerId(user.getCustomerId());
            retailUser.setFirstName(user.getFirstName());
            retailUser.setLastName(user.getLastName());
            retailUser.setEmail(user.getEmail());
            retailUser.setPhoneNumber(user.getPhoneNumber());
            retailUser.setCreatedOnDate(new Date());
            retailUser.setBirthDate(user.getBirthDate());
            retailUser.setBvn(user.getBvn());
            retailUser.setRole(new Role(user.getRoleId()));
            retailUser.setStatus("A");
            retailUser.setAlertPreference("BOTH");

            retailUserRepo.save(retailUser);

            logger.info("Retail user {} created", user.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    private void createEntrustUser(String username, String group, String fullName) {
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
    @Verifiable(operation = "DELETE_RETAIL_USER", description = "Deleting a Retail User")
    public String deleteUser(Long userId)  {
        try {

            RetailUser retailUser = retailUserRepo.findById(userId).get();
            retailUserRepo.delete(retailUser);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(retailUser.getEntrustId(), retailUser.getEntrustGroup());
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        } catch (VerificationInterruptedException ve) {
            return ve.getMessage();
        } catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_RETAIL_STATUS", description = "Change Retail User Activation Status")
    public String changeActivationStatus(Long userId)  {
        try {
            RetailUser user = retailUserRepo.findById(userId).get();
            entityManager.detach(user);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            retailUserRepo.save(user);
            if ("A".equals(user.getStatus())) {
                sendActivationMessage(user);
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
    public void sendPasswordResetMessage(RetailUser user) {

        final String url =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/retail";


        try {
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setExpiryDate(new Date());
            passwordPolicyService.saveRetailPassword(user);
            retailUserRepo.save(user);
            String fullName = user.getFirstName() + " " + user.getLastName();

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("username", user.getUserName());
            context.setVariable("password", newPassword);
            context.setVariable("url", url);


            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.password.reset.subject", null, locale))
                    .setTemplate("mail/retailpasswordreset")
                    .build();

            mailService.sendMail(email, context);
        } catch (Exception exception) {
            logger.error("Error resetting password", exception);
        }

    }


    @Async
    public void sendActivationMessage(RetailUser retailUser) {
        try {

            String url =
                    ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/retail";


            String fullName = retailUser.getFirstName() + " " + retailUser.getLastName();

            String password = passwordPolicyService.generatePassword();
            retailUser.setPassword(passwordEncoder.encode(password));
            retailUser.setExpiryDate(new Date());
            passwordPolicyService.saveRetailPassword(retailUser);
            retailUserRepo.save(retailUser);


            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("username", retailUser.getUserName());
            context.setVariable("password", password);
            context.setVariable("url", url);

            Email email = new Email.Builder()
                    .setRecipient(retailUser.getEmail())
                    .setSubject(messageSource.getMessage("customer.activation.subject", null, locale))
                    .setTemplate("mail/retailactivation")
                    .build();
            mailService.sendMail(email, context);
        } catch (MailException me) {
            logger.error("Failed to send reactivation mail to {}", retailUser.getEmail(), me);
        }

    }


    @Override
    public void sendActivationCredentials(RetailUser user, String password) {

        try {
            final String url =
                    ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/login/retail";

            String fullName = user.getFirstName() + " " + user.getLastName();

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("username", user.getUserName());
            context.setVariable("password", password);
            context.setVariable("url", url);


            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.activation.subject", null, locale))
                    .setTemplate("mail/retailactivation")
                    .build();
            mailService.sendMail(email, context);
        } catch (Exception e) {
            logger.error("Error occurred sending activation credentials", e);

        }
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) throws PasswordException {


        RetailUser user = retailUserRepo.findById(userId).get();
        logger.info("this is the user status{}", user.getStatus());
        if ("I".equals(user.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("users.deactivated", null, locale));
        }
        try {

            sendPasswordResetMessage(user);
            logger.info("Retail user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (MailException me) {
            throw new InternetBankingException(messageSource.getMessage("mail.failure", null, locale), me);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public String resetSecurityQuestion(Long userId) throws PasswordException {


        RetailUser user = retailUserRepo.findById(userId).get();
        logger.info("this is the user status{}", user.getStatus());
        if ("I".equals(user.getStatus())) {
            throw new InternetBankingException(messageSource.getMessage("users.deactivated", null, locale));
        }
        try {
            user.setResetSecurityQuestion("Y");
            retailUserRepo.save(user);
            logger.info("Retail user {} security question reset successfully", user.getUserName());
            return messageSource.getMessage("securityquestion.reset.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("securityquestion.reset.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    public void setSecurityQuestion(Long userId) throws PasswordException {


        RetailUser user = retailUserRepo.findById(userId).get();
        logger.info("this is the user status{}", user.getStatus());

        try {
            user.setResetSecurityQuestion("N");
            retailUserRepo.save(user);
            logger.info("Retail user {} security question set successfully", user.getUserName());
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("securityquestion.reset.failure", null, locale), e);
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
            RetailUser retailUser = retailUserRepo.findById(user.getId()).get();
            retailUser.setPassword(this.passwordEncoder.encode(custResetPassword.getNewPassword()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            retailUser.setTempPassword(null);
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


    /*
    Mobile Users password reset
     */
    @Transactional
    @Override
    public String resetPasswordMobileUser(RetailUser user, CustResetPassword custResetPassword) {
        String message;

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
            RetailUser retailUser = retailUserRepo.findById(user.getId()).get();
            String encodedPassword = this.passwordEncoder.encode(custResetPassword.getNewPassword());
            retailUser.setPassword(encodedPassword);
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            retailUser.setTempPassword(null);
            passwordPolicyService.saveRetailPassword(retailUser);
            this.retailUserRepo.save(retailUser);

            WebhookResponse webhookResponse = retailWebHook.pushToWebHook(retailUser);
            logger.info("Webhook Response {} ", webhookResponse);

            if (webhookResponse.getCode().equalsIgnoreCase("0")) {

                return messageSource.getMessage("password.change.success", null, locale);
            } else if ((webhookResponse.getCode().equalsIgnoreCase("20"))) {
                message = webhookResponse.getDescription();
                return message;

            } else if ((webhookResponse.getCode().equalsIgnoreCase("40"))) {
                message = webhookResponse.getDescription();
                return message;

            } else if ((webhookResponse.getCode().equalsIgnoreCase("99"))) {
                message = webhookResponse.getDescription();
                return message;

            } else if ((webhookResponse.getCode().equalsIgnoreCase("10"))) {
                message = webhookResponse.getDescription();
                return message;

            }
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.failure", null, locale);
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
            RetailUser retailUser = retailUserRepo.findById(user.getId()).get();
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
                ok = true;
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
            retailUser.setAlertPreference(code.getCode());
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
        if(retailUser != null && retailUser.getRole() != null)
            retailUserDTO.setRoleId(retailUser.getRole().getId());
        String coverage = retailUser.getCoverage();
        if (StringUtils.isNotBlank(coverage)) {
            String[] coverages = coverage.split(",");
            retailUserDTO.setCoverageCodes(Arrays.asList(coverages));
        } else {
            retailUserDTO.setCoverageCodes(Collections.emptyList());
        }
        return retailUserDTO;
    }

    private RetailUser convertDTOToEntity(RetailUserDTO retailUserDTO) {
        RetailUser map = modelMapper.map(retailUserDTO, RetailUser.class);
        map.setRole(new Role(retailUserDTO.getRoleId()));
        map.setCoverage(String.join(",", retailUserDTO.getCoverageCodes()));
        return map;
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
        return retailUserRepo.findAll(pageDetails).map(this::convertEntityToDTO);
    }

//TODO : Remove serves no clear purpose
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
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    @Override
    public void increaseNoOfTokenAttempt(RetailUser retailUser) {
        if (retailUser.getNoOfTokenAttempts() == null) {
            retailUser.setNoOfTokenAttempts(0);
        } else {
            retailUser.setNoOfTokenAttempts(retailUser.getNoOfTokenAttempts() + 1);
        }
        retailUserRepo.save(retailUser);
    }

    @Override
    public void resetNoOfTokenAttempt(RetailUser retailUser) {
        retailUser.setNoOfTokenAttempts(0);
        retailUserRepo.save(retailUser);
    }

    @Override
    public String validateAccount() {
        return null;
    }


    @Override
    public CustomerInfo getCustomerInfo(RetailUserCredentials userCredentials) {

        if (userCredentials.getUsername() == null || userCredentials.getPassword() == null) {
            logger.error("Missing credential {}", userCredentials);
            throw new InternetBankingException("Username or password not provided");
        }

        CustomerInfo customerInfo;
        RetailUser retailUser = retailUserRepo.findFirstByUserName(userCredentials.getUsername());

        if (retailUser != null) {
            String hashedPassword = retailUser.getPassword();
            if (passwordEncoder.matches(userCredentials.getPassword(), hashedPassword)) {
                customerInfo = new CustomerInfo();
                customerInfo.setCustomerName(retailUser.getFirstName() + " " + retailUser.getLastName());
                customerInfo.setPhoneNumber(retailUser.getPhoneNumber());
                customerInfo.setEmailAddress(retailUser.getEmail());
                return customerInfo;
            } else {
                logger.debug("Omni-channel: Incorrect password provided for username {}", retailUser.getUserName());
                throw new WrongPasswordException();
            }
        } else {
            logger.debug("Omni-channel: Retail user not found with username {}", userCredentials.getUsername());
            throw new UserNotFoundException();
        }

    }

    @Override
    public MobileRetailUserDTO convertEntitiesToDTO(RetailUser retailUser) {

        MobileRetailUserDTO mobileRetailUserDTO = modelMapper.map(retailUser, MobileRetailUserDTO.class);

        mobileRetailUserDTO.setBvn(retailUser.getBvn());
        mobileRetailUserDTO.setFirstName(retailUser.getFirstName());
        mobileRetailUserDTO.setLastName(retailUser.getLastName());

        if (retailUser.getLastLoginDate() != null) {
            mobileRetailUserDTO.setLastLoginDate(DateFormatter.format(retailUser.getLastLoginDate()));
        }
        mobileRetailUserDTO.setUserName(retailUser.getUserName());
        return mobileRetailUserDTO;
    }
  /*  @Override
    public Page<RetailUserDTO> findUsers(RetailUserDTO user, Pageable pageDetails) {
        RetailUser retailUser = modelMapper.map(user,RetailUser.class);
        logger.info("Retail user: " + retailUser.toString());
        Page<RetailUser> page = retailUserRepo.findAllUsers(retailUser.getUserName().toLowerCase(),
                retailUser.getLastName().toLowerCase(),retailUser.getFirstName().toLowerCase(),
                retailUser.getEmail().toLowerCase(), pageDetails);
        logger.info("retailusers {}",page.getTotalElements());
        List<RetailUserDTO> dtOs =new ArrayList<>();
        for (RetailUser retailUser1 : page) {
            RetailUserDTO retailDTO = modelMapper.map(retailUser1,RetailUserDTO.class);
            dtOs.add(retailDTO);
        }
        long t = page.getTotalElements();
        Page<RetailUserDTO> pageImpl = new PageImpl<RetailUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }
*/


    @Override
    public String changeFeedBackStatus(RetailUserDTO user)  {

        try {
            if (getUser(user.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return null;
            }
            retailUserRepo.updateFeedBackStatus(user.getFeedBackStatus(), user.getId());
            return messageSource.getMessage("feedback.update.success", null, locale);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FEEDBACK STATUS ERROR OCCURRED {}", e.getMessage());
        }
        return "Feedback status change not successful";
    }

    @Override
    public Page<RetailUserDTO> findUsers(RetailUserDTO user, Pageable pageDetails) {
        RetailUser retailUser = modelMapper.map(user, RetailUser.class);
        logger.info("Retail user: " + retailUser.toString());
        Page<RetailUser> page = retailUserRepo.findAllUsers(retailUser.getUserName().toLowerCase(),
                retailUser.getLastName().toLowerCase(), retailUser.getFirstName().toLowerCase(),
                retailUser.getEmail().toLowerCase(), pageDetails);
        logger.info("retailusers {}", page.getTotalElements());
        List<RetailUserDTO> dtOs = new ArrayList<>();
        for (RetailUser retailUser1 : page) {
            RetailUserDTO retailDTO = modelMapper.map(retailUser1, RetailUserDTO.class);
            dtOs.add(retailDTO);
        }
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }
}