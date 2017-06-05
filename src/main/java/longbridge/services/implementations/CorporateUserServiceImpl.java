package longbridge.services.implementations;

import longbridge.dtos.CorpCorporateUserDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.models.*;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.RoleRepo;
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
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private CorporateRepo corporateRepo;

    private Locale locale = LocaleContextHolder.getLocale();

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
    public CorporateUserDTO getUserDTOByName(String name) {
        CorporateUser corporateUser = this.corporateUserRepo.findFirstByUserName(name);
        return convertEntityToDTO(corporateUser);
    }

    @Override
    public CorporateUser getUserByName(String username) {
        return corporateUserRepo.findByUserName(username);
    }

    /*@Override
    public CorporateUser getUserByCustomerId(String custId) {
        CorporateUser corporateUser = this.corporateUserRepo.findFirstByCustomerId(custId);
        return corporateUser;
    }*/

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
    public String updateUser(CorporateUserDTO user) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            if (user.getRoleId() != null) {
                Role role = new Role();
                role.setId(Long.parseLong(user.getRoleId()));
                corporateUser.setRole(role);
            }

            corporateUserRepo.save(corporateUser);
            return messageSource.getMessage("user.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.update.failure", null, locale), e);

        }
    }

    @Override
    @Transactional
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
            corporateUser.setStatus("A");
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            String password = passwordPolicyService.generatePassword();
            corporateUser.setPassword(passwordEncoder.encode(password));
            corporateUser.setUsedPasswords(getUsedPasswords(password,corporateUser.getUsedPasswords()));
            user.setExpiryDate(new Date());
            Role role = new Role();
            role.setId(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            Corporate corporate = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corporate);
            corporateUserRepo.save(corporateUser);

            String fullName = corporateUser.getFirstName()+" "+corporateUser.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                   boolean result = securityService.createEntrustUser(corporateUser.getUserName(), fullName, true);
                    if (!result) {
                        throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));

                    }
                }
            }
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), fullName, user.getUserName(), password,corporate.getCustomerId()))
                    .build();
            mailService.send(email);
            logger.info("New corporate user {} created", corporateUser.getUserName());
            return messageSource.getMessage("user.add.success", null, locale);
        }

        catch (Exception e) {
            if(e instanceof EntrustException){
                throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale));
            }
            throw new InternetBankingException(messageSource.getMessage("user.add.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    public String addUserFromCorporateAdmin(CorpCorporateUserDTO user) throws InternetBankingException {

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
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setCreatedOnDate(new Date());
            String password = passwordPolicyService.generatePassword();
            corporateUser.setPassword(passwordEncoder.encode(password));
            corporateUser.setUsedPasswords(getUsedPasswords(password,corporateUser.getUsedPasswords()));
            corporateUser.setExpiryDate(new Date());
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            if (!"Authorizer".equals(role.getName())) {
                user.setStatus("A");
            }
            corporateUser.setRole(role);
            Corporate corporate = corporateRepo.findOne(Long.parseLong(user.getCorporateId()));
            corporateUser.setCorporate(corporate);
            corporateUserRepo.save(corporateUser);
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            String fullName = user.getFirstName() + " " + user.getLastName();

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    boolean result = securityService.createEntrustUser(corporateUser.getUserName(), fullName, true);
                    if (!result) {
                        throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));

                    }
                }
            }
            if (!"Authorizer".equals(role.getName())) {
                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("customer.create.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("customer.create.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);
            }
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
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            user.setStatus(newStatus);
            corporateUserRepo.save(user);
            String fullName = user.getFirstName() + " " + user.getLastName();
            if ((oldStatus == null)) {//User was just created
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setUsedPasswords(getUsedPasswords(password,user.getUsedPasswords()));

                user.setExpiryDate(new Date());
                corporateUserRepo.save(user);

                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);
            } else if (("I".equals(oldStatus)) && "A".equals(newStatus)) {//User is being reactivated
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setUsedPasswords(getUsedPasswords(password,user.getUsedPasswords()));
                user.setExpiryDate(new Date());
                corporateUserRepo.save(user);
                Email email = new Email.Builder()
                        .setRecipient(user.getEmail())
                        .setSubject(messageSource.getMessage("customer.reactivation.subject", null, locale))
                        .setBody(String.format(messageSource.getMessage("customer.reactivation.message", null, locale), fullName, user.getUserName(), password))
                        .build();
                mailService.send(email);
            }

            logger.info("Corporate user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }

    @Override
    public String changeCorpActivationStatus(Long userId) throws InternetBankingException {
        try {
            CorporateUser user = corporateUserRepo.findOne(userId);
            String oldStatus = user.getStatus();
            if (!"Authorizer".equals(user.getRole().getName())) {
                String newStatus = "A".equals(oldStatus) ? "I" : "A";
                user.setStatus(newStatus);
                corporateUserRepo.save(user);
                String fullName = user.getFirstName() + " " + user.getLastName();
                logger.info("Corporate user {} status changed from {} to {}", fullName, oldStatus, newStatus);
                return messageSource.getMessage("user.status.success", null, locale);

            } else {
                if (!"A".equals(user.getStatus())) {
                    throw new InternetBankingException(messageSource.getMessage("user.status.failure.permission", null, locale));
                } else {
                    String newStatus = "A".equals(oldStatus) ? "I" : "A";
                    user.setStatus(newStatus);
                    corporateUserRepo.save(user);
                    String fullName = user.getFirstName() + " " + user.getLastName();

                    logger.info("Corporate user {} status changed from {} to {}", fullName, oldStatus, newStatus);
                    return messageSource.getMessage("user.status.success", null, locale);
                }
            }

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }

    @Override
    public String resetPassword(Long userId) throws PasswordException {

        try {
            CorporateUser user = corporateUserRepo.findOne(userId);
            String newPassword = passwordPolicyService.generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUsedPasswords(getUsedPasswords(newPassword,user.getUsedPasswords()));
            user.setExpiryDate(new Date());
            String fullName = user.getFirstName() + " " + user.getLastName();
            corporateUserRepo.save(user);
            Email email = new Email.Builder()
                    .setRecipient(user.getEmail())
                    .setSubject(messageSource.getMessage("customer.password.reset.subject", null, locale))
                    .setBody(String.format(messageSource.getMessage("customer.password.reset.message", null, locale), fullName, newPassword))
                    .build();
            mailService.send(email);
            logger.info("Corporate user {} password reset successfully", user.getUserName());
            return messageSource.getMessage("password.reset.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.reset.failure", null, locale), e);
        }
    }


    @Override
    public String deleteUser(Long userId) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(userId);
            corporateUserRepo.delete(userId);
            String fullName = corporateUser.getFirstName()+" "+corporateUser.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_DELETION");

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    securityService.deleteEntrustUser(corporateUser.getUserName(), fullName);
                }
            }
            return messageSource.getMessage("user.delete.success", null, locale);
        }
        catch (InternetBankingSecurityException se) {
            throw new InternetBankingSecurityException(messageSource.getMessage("entrust.delete.failure", null, locale));
        }
        catch (Exception ibe) {
            throw new InternetBankingException(messageSource.getMessage("user.delete.failure", null, locale));
        }
    }

    @Override
    public void lockUser(CorporateUser user, Date unlockat) {
        //todo
    }

    @Override
    public String changePassword(CorporateUser user, CustChangePassword changePassword) {
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
            CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
            corporateUser.setPassword(this.passwordEncoder.encode(changePassword.getNewPassword()));
            corporateUser.setUsedPasswords(getUsedPasswords(changePassword.getNewPassword(),corporateUser.getUsedPasswords()));
            corporateUser.setExpiryDate(passwordPolicyService.getPasswordExpiryDate());
            this.corporateUserRepo.save(corporateUser);
            logger.info("User {} password has been updated", user.getId());
            return messageSource.getMessage("password.change.success", null, locale);
        } catch (Exception e) {
            throw new PasswordException(messageSource.getMessage("password.change.failure", null, locale), e);
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
            user.setUsedPasswords(getUsedPasswords(newPassword,user.getUsedPasswords()));
            corporateUserRepo.save(user);
            logger.info("User {} password has been updated", user.getUserName());
            //todo send the email.. messagingService.sendEmail(EmailDetail);
        } catch (Exception e) {
            logger.error("Error Occurred {}", e);
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

    private void sendUserCredentials(CorporateUser user, String password) throws InternetBankingException {
        String fullName = user.getFirstName() + " " + user.getLastName();
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), fullName, user.getUserName(), password))
                .build();
        mailService.send(email);
    }

    @Override
    public boolean changeAlertPreference(CorporateUserDTO corporateUser, AlertPref alertPreference) {
        boolean ok = false;
        try {
            if (getUser(corporateUser.getId()) == null) {
                logger.error("USER DOES NOT EXIST");
                return ok;
            }

            CorporateUser corp = convertDTOToEntity(corporateUser);
            Code code = codeService.getByTypeAndCode("ALERT_PREFERENCE", alertPreference.getPreference());
            corp.setAlertPreference(code);
            this.corporateUserRepo.save(corp);
            logger.info("User {}'s alert preference set", corp.getId());
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR OCCURRED", e);
        }
        return ok;
    }

    private CorporateUser convertDTOToEntity(CorporateUserDTO CorporateUserDTO) {
        return modelMapper.map(CorporateUserDTO, CorporateUser.class);
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
        CorporateUserDTO corporateUserDTO = modelMapper.map(corporateUser, CorporateUserDTO.class);
        corporateUserDTO.setRoleId(corporateUser.getRole().getId().toString());
        corporateUserDTO.setRole(corporateUser.getRole().getName());
        corporateUserDTO.setCorporateType(corporateUser.getCorporate().getCorporateType());
        corporateUserDTO.setCorporateName(corporateUser.getCorporate().getName());
        corporateUserDTO.setCorporateId(corporateUser.getCorporate().getId().toString());


        if (corporateUser.getCreatedOnDate() != null) {
            corporateUserDTO.setCreatedOn(DateFormatter.format(corporateUser.getCreatedOnDate()));
        }
        if (corporateUser.getLastLoginDate() != null) {
            corporateUserDTO.setLastLogin(DateFormatter.format(corporateUser.getLastLoginDate()));
        }

        return corporateUserDTO;
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
        Page<CorporateUserDTO> pageImpl = new PageImpl<CorporateUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


}
