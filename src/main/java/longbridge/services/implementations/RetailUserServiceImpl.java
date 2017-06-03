package longbridge.services.implementations;


import longbridge.api.AccountInfo;
import longbridge.api.CustomerDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.models.Account;
import longbridge.models.Code;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
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
    MessageSource messageSource;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    MailService mailService;

    Locale locale = LocaleContextHolder.getLocale();

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
    public RetailUser getUserByName(String name) {
        RetailUser retailUser = this.retailUserRepo.findFirstByUserName(name);
        return retailUser;
    }

    @Override
    public RetailUserDTO getUserDTOByName(String name) {
        RetailUser retailUser = this.retailUserRepo.findFirstByUserName(name);
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

//    @Override
//    public String setPassword(RetailUser user, String password) throws PasswordException {
//        boolean ok = false;
//        if (user != null) {
//            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
//        } else {
//            throw new RuntimeException("Null user provided");
//        }
//        return null;
//
//    }

    @Override
    @Transactional
    public String addUser(RetailUserDTO user, CustomerDetails details) throws InternetBankingException {
        try {
            RetailUser retailUser = getUserByName(user.getUserName());
            if (retailUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.add.exists", null, locale));
            }
            RetailUser retUser = getUserByCustomerId(user.getCustomerId());
            if (retUser != null) {
                throw new DuplicateObjectException(messageSource.getMessage("user.add.exist", null, locale));
            }

            retailUser = new RetailUser();
            retailUser.setUserName(user.getUserName());
            retailUser.setCustomerId(details.getCifId());
            retailUser.setFirstName(details.getCustomerName());
            retailUser.setEmail(details.getEmail());
            retailUser.setCreatedOnDate(new Date());
            retailUser.setBirthDate(user.getBirthDate());
            retailUser.setRole(roleService.getTheRole("RETAIL"));
            retailUser.setStatus("A");
            retailUser.setAlertPreference(codeService.getByTypeAndCode("ALERT_PREFERENCE", "BOTH"));
            String errorMsg = passwordPolicyService.validate(user.getPassword(),null);
            if(!"".equals(errorMsg)){
                throw new PasswordPolicyViolationException(errorMsg);
            }

            String fullName = retailUser.getFirstName()+" "+retailUser.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    createEntrustUser(user.getUserName(), fullName, true);

                    setEntrustUserQA(user.getUserName(), user.getSecurityQuestion(), user.getSecurityAnswer(), fullName);

//                    setEntrustUserMutualAuth(user.getUserName(), user.getCaptionSec(), user.getPhishingSec(), fullName);
                }
            }


            retailUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            retailUserRepo.save(retailUser);

            Collection<AccountInfo> accounts = integrationService.fetchAccounts(details.getCifId());
            for (AccountInfo acct : accounts) {
                accountService.AddFIAccount(details.getCifId(), acct);
            }

            logger.info("Retail user {} created", user.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }

    private void createEntrustUser(String username, String fullName, boolean enableOtp){
        try{
            securityService.createEntrustUser(username, fullName, true);
        }catch (InternetBankingSecurityException e){
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void setEntrustUserQA(String username, List<String> securityQuestion, List<String> securityAnswer, String fullName){
        try{
            securityService.setUserQA(username, securityQuestion, securityAnswer);
        }catch (InternetBankingSecurityException e){
            securityService.deleteEntrustUser(username, fullName);
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    private void setEntrustUserMutualAuth(String username, List<String> captionSec, List<String> phishingSec, String fullName){
        try{
            securityService.setMutualAuth(username, captionSec, phishingSec);
        }catch (InternetBankingSecurityException e){
            securityService.deleteEntrustUser(username, fullName);
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale), e);
        }
    }

    @Override
    public String deleteUser(Long userId) throws InternetBankingException {
        try {
            RetailUser user = retailUserRepo.findOne(userId);
            user.setDeletedOn(new Date());
            retailUserRepo.save(user);
            retailUserRepo.delete(userId);
            return messageSource.getMessage("user.delete.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure",null,locale),e);
        }
    }


    @Override
    @Transactional
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        try {
            RetailUser user = retailUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            retailUserRepo.save(user);
            if ((oldStatus == null) || ("I".equals(oldStatus)) && "A".equals(newStatus)) {
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setUsedPasswords(getUsedPasswords(password,user.getUsedPasswords()));
                String fullName = user.getFirstName()+" "+user.getLastName();
                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("customer.reactivation.subject",null,locale))
                        .setBody(String.format(messageSource.getMessage("customer.reactivation.message",null,locale), fullName, user.getUserName(),password))
                        .build();
                mailService.send(email);
            }

            logger.info("Retail user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }

    private String getUsedPasswords(String newPassword, String oldPasswords) {
        StringBuilder builder = new StringBuilder();
        if (oldPasswords != null) {
            builder.append(oldPasswords);
        }
        builder.append(passwordEncoder.encode(newPassword) + ",");
        return builder.toString();
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) throws PasswordException {

        try {
            RetailUser user = retailUserRepo.findOne(userId);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUsedPasswords(getUsedPasswords(newPassword,user.getUsedPasswords()));
            user.setExpiryDate(new Date());
            retailUserRepo.save(user);
            String fullName = user.getFirstName()+" "+user.getLastName();
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.password.reset.subject",null,locale))
                    .setBody(String.format(messageSource.getMessage("customer.password.reset.message",null,locale),fullName, user.getUserName(),newPassword))
                    .build();
            mailService.send(email);
            logger.info("Retail user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }

    @Override
    public String resetPassword(RetailUser user, String password) {

        String errorMessage = passwordPolicyService.validate(password, user.getUsedPasswords());
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }

        try {
            RetailUser retailUser = retailUserRepo.findOne(user.getId());
            retailUser.setPassword(this.passwordEncoder.encode(password));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            user.setUsedPasswords(getUsedPasswords(password,user.getUsedPasswords()));
            this.retailUserRepo.save(retailUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
        }
    }

    @Override
    public String changePassword(RetailUser user, CustChangePassword changePassword) throws PasswordException {
        if (!this.passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        String errorMessage = passwordPolicyService.validate(changePassword.getNewPassword(), user.getUsedPasswords());
        if (!"".equals(errorMessage)) {
            throw new PasswordPolicyViolationException(errorMessage);
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        try {
            RetailUser retailUser = retailUserRepo.findOne(user.getId());
            retailUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            user.setUsedPasswords(getUsedPasswords(changePassword.getNewPassword(),user.getUsedPasswords()));
            retailUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
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

//    @Override
//    public boolean generateAndSendPassword(RetailUser user) {
//        boolean ok = false;
//
//        try {
//            String newPassword = generatePassword();
//            setPassword(user, newPassword);
//            retailUserRepo.save(user);
//
//            sendPassword(user);
//            logger.info("PASSWORD GENERATED AND SENT");
//        } catch (Exception e) {
//            logger.error("ERROR OCCURRED {}", e.getMessage());
//        }
//
//
//        return ok;
//
//    }

    @Override
    public boolean changeAlertPreference(RetailUserDTO user, AlertPref alertPreference) {
        boolean ok = false;
        try {
            if (getUser(user.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return ok;
            }

            RetailUser retailUser = convertDTOToEntity(user);
            Code code = codeService.getByTypeAndCode("ALERT_PREFERENCE", alertPreference.getPreference());
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

    public String generatePassword() {
        /*String password=   RandomStringUtils.randomNumeric(10);
        return password!=null? password: "";*/
        return null;
    }


    public boolean sendPassword(RetailUser user) {
        //TODO use an smtp server to send new password to user via mail
        return false;
    }

    private RetailUserDTO convertEntityToDTO(RetailUser retailUser) {
        RetailUserDTO retailUserDTO =  modelMapper.map(retailUser, RetailUserDTO.class);
        Code code = codeService.getByTypeAndCode("USER_STATUS", retailUser.getStatus());
        if(retailUser.getCreatedOnDate()!=null) {
            retailUserDTO.setCreatedOn(DateFormatter.format(retailUser.getCreatedOnDate()));
        }
        if(retailUser.getLastLoginDate()!=null) {
            retailUserDTO.setLastLogin(DateFormatter.format(retailUser.getLastLoginDate()));
        }
        if (code != null) {
            retailUserDTO.setStatus(code.getDescription());
        }
        return retailUserDTO;
    }

    private RetailUser convertDTOToEntity(RetailUserDTO retailUserDTO) {
       return   modelMapper.map(retailUserDTO, RetailUser.class);
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

}
