package longbridge.services.implementations;

import longbridge.api.AccountInfo;
import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.Verifiable;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Fortune on 4/5/2017.
 */
@Service
public class CorporateServiceImpl implements CorporateService {

    private CorporateRepo corporateRepo;
    private CorpLimitRepo corpLimitRepo;
    private CorpTransferRuleRepo corpTransferRuleRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CodeService codeService;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private CorporateRoleRepo corporateRoleRepo;


    @Autowired
    private EntityManager entityManager;

    private Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public CorporateServiceImpl(CorporateRepo corporateRepo, CorpLimitRepo corpLimitRepo, CorpTransferRuleRepo corpTransferRuleRepo) {
        this.corporateRepo = corporateRepo;
        this.corpLimitRepo = corpLimitRepo;
        this.corpTransferRuleRepo = corpTransferRuleRepo;
    }

    @Override
    public Long countCorporate() {
        return corporateRepo.count();
    }

    public String addCorporate(CorporateDTO corporateDTO) throws InternetBankingException {

        Corporate corporate = corporateRepo.findByCustomerId(corporateDTO.getCustomerId());

        if (corporate != null) {
            throw new DuplicateObjectException(messageSource.getMessage("corporate.exist", null, locale));
        }

        try {
            corporate = convertDTOToEntity(corporateDTO);
            corporate.setStatus("A");
            corporate.setCreatedOnDate(new Date());
            corporateRepo.save(corporate);
            addAccounts(corporate);

            logger.info("Corporate {} created", corporate.getName());
            return messageSource.getMessage("corporate.add.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale), e);
        }
    }

    @Override
    public String addCorporate(CorporateDTO corporateDTO, CorporateUserDTO user) throws InternetBankingException {

        Corporate corporate = corporateRepo.findByCustomerId(corporateDTO.getCustomerId());

        if (corporate != null) {
            throw new DuplicateObjectException(messageSource.getMessage("corporate.exist", null, locale));
        }

        CorporateUser corporateUser = corporateUserRepo.findFirstByUserName(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }

        try {
            corporate = convertDTOToEntity(corporateDTO);
            corporate.setStatus("A");
            corporate.setCreatedOnDate(new Date());
            corporateUser = new CorporateUser();
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setUserName(user.getUserName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setAdmin(user.isAdmin());
            corporateUser.setAlertPreference(codeService.getByTypeAndCode("ALERT_PREFERENCE", "BOTH"));
            corporateUser.setCreatedOnDate(new Date());
            corporateUser.setStatus("A");
            Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            corporateUser.setRole(role);
            corporateUser.setCorporate(corporate);
            Corporate newCorporate = corporateRepo.save(corporate);
            corporateUser.setCorporate(newCorporate);
            CorporateUser corpUser = corporateUserRepo.save(corporateUser);
            createUserOnEntrustAndSendCredentials(corpUser);
            addAccounts(corporate);

            logger.info("Corporate {} created", corporate.getName());
            return messageSource.getMessage("corporate.add.success", null, locale);
        } catch (Exception e) {
            if (e instanceof EntrustException) {
                throw new InternetBankingSecurityException(messageSource.getMessage("entrust.create.failure", null, locale));
            }

            throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale), e);
        }
    }


    @Override
    @Verifiable(operation = "ADD_CORPORATE", description = "Adding Corporate Entity")
    public String addCorporate(CorporateRequestDTO corporateRequestDTO) throws InternetBankingException {

        try {
            saveCorporateRequest(corporateRequestDTO);
            return messageSource.getMessage("corporate.add.success", null, locale);
        } catch (DuplicateObjectException doe) {
            throw doe;
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale), e);
        }
    }

    @Transactional
    public void saveCorporateRequest(CorporateRequestDTO corporateRequestDTO) throws InternetBankingException {

        validateCorporate(corporateRequestDTO);

        Corporate corporate = new Corporate();
        corporate.setCorporateType(corporateRequestDTO.getCorporateType());
        corporate.setName(corporateRequestDTO.getCorporateName());
        corporate.setCustomerId(corporateRequestDTO.getCustomerId());
        corporate.setCorporateId(corporateRequestDTO.getCorporateId());
        corporate.setBvn(corporateRequestDTO.getBvn());
        corporate.setRcNumber(corporateRequestDTO.getRcNumber());
        corporate.setEmail(corporateRequestDTO.getEmail());
        corporate.setPhoneNumber(corporateRequestDTO.getPhoneNumber());
        corporate.setCreatedOnDate(new Date());
        corporate.setStatus("A");
        List<Account> accounts = accountService.addAccounts(corporateRequestDTO.getAccounts());
        corporate.setAccounts(accounts);
        Corporate newCorporate = corporateRepo.save(corporate);


        List<CorporateUserDTO> authorizers = new ArrayList<>();

        for (CorporateUserDTO user : corporateRequestDTO.getCorporateUsers()) {

            validateCorporateUser(user);
            CorporateUser corporateUser = new CorporateUser();
            corporateUser.setUserName(user.getUserName());
            corporateUser.setFirstName(user.getFirstName());
            corporateUser.setLastName(user.getLastName());
            corporateUser.setEmail(user.getEmail());
            corporateUser.setPhoneNumber(user.getPhoneNumber());
            corporateUser.setAlertPreference(codeService.getByTypeAndCode("ALERT_PREFERENCE", "BOTH"));
            corporateUser.setCreatedOnDate(new Date());
            corporateUser.setStatus("A");
            Role role;
            if ("SOLE".equals(corporateRequestDTO.getCorporateType())) {
                role = getSoleCorporateRole();
            } else {
                role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
            }
            corporateUser.setRole(role);
            corporateUser.setCorpUserType(getUserType(user.getUserType()));
            corporateUser.setAdmin(CorpUserType.ADMIN.equals(corporateUser.getCorpUserType()));
            corporateUser.setCorporate(newCorporate);
            createUserOnEntrustAndSendCredentials(corporateUser);
            if ("AUTHORIZER".equals(user.getUserType())) {
                authorizers.add(user);
            }

        }


        if ("MULTI".equals(newCorporate.getCorporateType())) {

            List<CorporateRole> corporateRoles = new ArrayList<>();

            for (AuthorizerLevelDTO authorizerLevelDTO : corporateRequestDTO.getAuthorizers()) {
                CorporateRole role = new CorporateRole();
                role.setName(authorizerLevelDTO.getName());
                role.setRank(authorizerLevelDTO.getLevel());
                role.setCorporate(newCorporate);

                HashSet<CorporateUser> corpUsers = new HashSet<>();
                for (CorporateUserDTO user : authorizers) {
                    if (user.getAuthorizerLevel().equals(authorizerLevelDTO.getName() + " " + authorizerLevelDTO.getLevel())) {
                        CorporateUser corporateUser = corporateUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
                        corpUsers.add(corporateUser);
                    }
                }
                role.setUsers(corpUsers);
                CorporateRole corporateRole = corporateRoleRepo.save(role);
                corporateRoles.add(corporateRole);
            }

            for (CorpTransferRuleDTO transferRuleDTO : corporateRequestDTO.getCorpTransferRules()) {
                CorpTransRule corpTransRule = new CorpTransRule();
                corpTransRule.setLowerLimitAmount(new BigDecimal(transferRuleDTO.getLowerLimitAmount()));
                if (transferRuleDTO.isUnlimited()) {
                    corpTransRule.setUpperLimitAmount(new BigDecimal(Integer.MAX_VALUE));
                } else {
                    corpTransRule.setUpperLimitAmount(new BigDecimal(transferRuleDTO.getUpperLimitAmount()));
                }
                corpTransRule.setUnlimited(transferRuleDTO.isUnlimited());
                corpTransRule.setCurrency(transferRuleDTO.getCurrency());
                corpTransRule.setAnyCanAuthorize(transferRuleDTO.isAnyCanAuthorize());
                corpTransRule.setCorporate(newCorporate);

                List<CorporateRole> roleList = new ArrayList<CorporateRole>();
                for (CorporateRole role : corporateRoles) {
                    for (String authorizerLevel : transferRuleDTO.getAuthorizers()) {
                        if (authorizerLevel.equals(role.getName() + " " + role.getRank())) {
                            roleList.add(role);
                        }
                    }
                }
                corpTransRule.setRoles(roleList);
                corpTransferRuleRepo.save(corpTransRule);
            }

        }

    }

    private void validateCorporate(CorporateRequestDTO corporateRequestDTO) throws InternetBankingException {

        if (corporateIdExists(corporateRequestDTO.getCorporateId())) {
            throw new DuplicateObjectException(messageSource.getMessage("corp.id.exists", null, locale));
        }

        if (corporateRequestDTO.getAccounts().isEmpty()) {
            logger.error("Corporate creation request found with no accounts");
            throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale));
        }

        if ("SOLE".equals(corporateRequestDTO.getCorporateType())) {

            if (corporateRequestDTO.getCorporateUsers().size() != 1) {
                logger.error("Sole Corporate creation request found with {} users", corporateRequestDTO.getCorporateUsers().size());
                throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale));

            }

        } else if ("MULTI".equals(corporateRequestDTO.getCorporateType())) {
            if (corporateRequestDTO.getAuthorizers().isEmpty()) {
                logger.error("Corporate creation request found with no authorizer levels");
                throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale));

            }

            if (corporateRequestDTO.getCorporateUsers().isEmpty()) {
                logger.error("Sole Corporate creation request found with no users");
                throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale));

            }
        } else {
            logger.error("Corporate creation request found with invalid corporate type");
            throw new InternetBankingException(messageSource.getMessage("corporate.add.failure", null, locale));
        }
    }


    private void validateCorporateUser(CorporateUserDTO user) {
        CorporateUser corporateUser = corporateUserRepo.findFirstByUserNameIgnoreCase(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exists", null, locale));
        }
    }

    private CorpUserType getUserType(String userType) {

        if ("ADMIN".equals(userType)) {
            return CorpUserType.ADMIN;
        } else if ("INITIATOR".equals(userType)) {
            return CorpUserType.INITIATOR;
        } else if ("AUTHORIZER".equals(userType)) {
            return CorpUserType.AUTHORIZER;
        }
        return null;
    }


    private Role getSoleCorporateRole() {
        Role role = null;
        SettingDTO setting = configService.getSettingByName("SOLE_CORPORATE_ROLE");
        if (setting != null && setting.isEnabled()) {
            String roleName = setting.getValue();
            role = roleRepo.findByUserTypeAndName(UserType.CORPORATE, roleName);
        }
        return role;
    }

    public void addAccounts(Corporate corporate) {
        String customerId = corporate.getCustomerId();
        Corporate corp = corporateRepo.findFirstByCustomerId(customerId);
        if (corp != null) {
            Collection<AccountInfo> accounts = integrationService.fetchAccounts(customerId);
            for (AccountInfo acct : accounts) {
                accountService.AddFIAccount(customerId, acct);
            }
        }
    }


    @Transactional
    public void createUserOnEntrustAndSendCredentials(CorporateUser user) throws EntrustException {

        if ("".equals(user.getEntrustId()) || user.getEntrustId() == null) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            String entrustId = user.getUserName();
            String group = configService.getSettingByName("DEF_ENTRUST_CORP_GRP").getValue();

            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    boolean result = securityService.createEntrustUser(entrustId, group, fullName, true);
                    if (!result) {
                        throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));
                    }
                    boolean contactResult = securityService.addUserContacts(user.getEmail(), user.getPhoneNumber(), true, entrustId, group);
                    if (!contactResult) {
                        logger.error("Failed to add user contacts on Entrust");
                        securityService.deleteEntrustUser(entrustId, group);
                        throw new EntrustException(messageSource.getMessage("entrust.contact.failure", null, locale));

                    }
                }
                user.setEntrustId(entrustId);
                user.setEntrustGroup(group);
                String password = passwordPolicyService.generatePassword();
                user.setPassword(passwordEncoder.encode(password));
                user.setExpiryDate(new Date());
                corporateUserRepo.save(user);
                sendUserCredentials(user, password);
            }
        }
    }

    @Override
    public String deleteCorporate(Long id) throws InternetBankingException {
        try {
            corporateRepo.delete(id);
            return messageSource.getMessage("corporate.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.delete.failure", null, locale));

        }
    }

    @Async
    public void sendUserCredentials(CorporateUser user, String password) throws InternetBankingException {
        String fullName = user.getFirstName() + " " + user.getLastName();
        Corporate corporate = user.getCorporate();
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("corporate.customer.create.subject", null, locale))
                .setBody(String.format(messageSource.getMessage("corporate.customer.create.message", null, locale), fullName, user.getUserName(), password, corporate.getCorporateId()))
                .build();
        new Thread(() -> {
            mailService.send(email);
        }).start();
    }


    @Override
//    @Verifiable(operation = "UPDATE_CORPORATE", description = "Updating Corporate Entity")
    public String updateCorporate(CorporateDTO corporateDTO) throws InternetBankingException {
        try {
            Corporate corporate = corporateRepo.findOne(corporateDTO.getId());
            entityManager.detach(corporate);
            corporate.setVersion(corporateDTO.getVersion());
            corporate.setEmail(corporateDTO.getEmail());
            corporate.setName(corporateDTO.getName());
            corporate.setAddress(corporateDTO.getAddress());
            corporateRepo.save(corporate);
            return messageSource.getMessage("corporate.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.update.failure", null, locale), e);

        }
    }

    @Override
    public CorporateDTO getCorporate(Long id) {
        Corporate corporate = corporateRepo.findOne(id);
        return convertEntityToDTO(corporate);
    }

    @Override
    public Corporate getCorp(Long id) {
        Corporate corporate = corporateRepo.findOne(id);
        return corporate;
    }

    @Override
    public Corporate getCorporateByCustomerId(String customerId) {
        Corporate corporate = corporateRepo.findByCustomerId(customerId);
        return corporate;
    }

    @Override
    public Corporate getCorporateByCorporateId(String corporateId) {
        Corporate corporate = corporateRepo.findFirstByCorporateIdIgnoreCase(corporateId);
        return corporate;
    }

    @Override
    public List<CorporateDTO> getCorporates() {
        Iterable<Corporate> corporateDTOS = corporateRepo.findAll();
        return convertEntitiesToDTOs(corporateDTOS);
    }


    @Override
    public String addAccount(Corporate corporate, AccountDTO accountDTO) throws InternetBankingException {

        try {
            boolean ok = accountService.AddAccount(corporate.getCustomerId(), accountDTO);
            if (ok) {
                return messageSource.getMessage("account.add.success", null, locale);
            } else {
                throw new InternetBankingException(messageSource.getMessage("corporate.account.add.failure", null, locale));
            }


        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.account.add.failure", null, locale), e);
        }
    }

    @Override

    @Transactional
    @Verifiable(operation = "UPDATE_CORPORATE_STATUS", description = "Change Corporate Activation Status")
    public String changeActivationStatus(Long id) throws InternetBankingException {
        try {
            Corporate corporate = corporateRepo.findOne(id);
            entityManager.detach(corporate);
            String oldStatus = corporate.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            corporate.setStatus(newStatus);
            corporateRepo.save(corporate);
            logger.info("Corporate {} status changed from {} to {}", corporate.getName(), oldStatus, newStatus);
            return messageSource.getMessage("corporate.status.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.status.failure", null, locale), e);

        }
    }

    public boolean corporateExists(String customerId) {
        Corporate corporate = corporateRepo.findByCustomerId(customerId);
        return (corporate != null) ? true : false;
    }

    @Override
    public void setLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException {
        limit.setCorporate(corporate);
        corpLimitRepo.save(limit);
    }

    @Override
    public void updateLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException {
        limit.setCorporate(corporate);
        corpLimitRepo.save(limit);
    }

    @Override
    public List<CorpLimit> getLimit(Corporate corporate) {
        return corpLimitRepo.findByCorporate(corporate);
    }

    @Override
    public void deleteLimit(Long corporateId, CorpLimit limit) {
        limit.setDelFlag("Y");
        corpLimitRepo.save(limit);
    }

    @Override
    public Page<CorporateDTO> getCorporates(Pageable pageDetails) {
        Page<Corporate> page = corporateRepo.findAll(pageDetails);
        List<CorporateDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<CorporateDTO> pageImpl = new PageImpl<CorporateDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Page<AccountDTO> getAccounts(Long corpId, Pageable pageDetails) {
        Corporate corporate = corporateRepo.findOne(corpId);
        Page<AccountDTO> page = accountService.getAccounts(corporate.getCustomerId(), pageDetails);
        List<AccountDTO> dtOs = page.getContent();
        long t = page.getTotalElements();
        Page<AccountDTO> pageImpl = new PageImpl<AccountDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public List<Account> getAccounts(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        return corporate.getAccounts();
    }


    @Override
    @Verifiable(operation = "ADD_CORPORATE_RULE", description = "Add Corporate Transfer Rule")
    public String addCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException {


        if (new BigDecimal(transferRuleDTO.getLowerLimitAmount()).compareTo(new BigDecimal("0")) < 0) {
            throw new TransferRuleException(messageSource.getMessage("rule.amount.zero", null, locale));
        }
        if (new BigDecimal(transferRuleDTO.getUpperLimitAmount()).compareTo(new BigDecimal(transferRuleDTO.getLowerLimitAmount())) < 0) {
            throw new TransferRuleException(messageSource.getMessage("rule.range.violation", null, locale));
        }

        try {
            CorpTransRule corpTransRule = convertTransferRuleDTOToEntity(transferRuleDTO);
            corpTransferRuleRepo.save(corpTransRule);

            logger.info("Added transfer rule for corporate with Id {}", transferRuleDTO.getCorporateId());
            return messageSource.getMessage("rule.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rule.add.failure", null, locale), e);
        }
    }

    @Override
    public CorpTransferRuleDTO getCorporateRule(Long id) {
        CorpTransRule transferRule = corpTransferRuleRepo.findOne(id);
        return convertTransferRuleEntityToDTO(transferRule);
    }

    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_CORPORATE_RULE", description = "Update Corporate Transfer Rule")
    public String updateCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException {

        if (new BigDecimal(transferRuleDTO.getLowerLimitAmount()).compareTo(new BigDecimal("0")) < 0) {
            throw new TransferRuleException(messageSource.getMessage("rule.amount.zero", null, locale));
        }
        if (new BigDecimal(transferRuleDTO.getUpperLimitAmount()).compareTo(new BigDecimal(transferRuleDTO.getLowerLimitAmount())) < 0) {
            throw new TransferRuleException(messageSource.getMessage("rule.range.violation", null, locale));
        }

        try {
            CorpTransRule corpTransRule = convertTransferRuleDTOToEntity(transferRuleDTO);
            corpTransRule.setId(transferRuleDTO.getId());
            corpTransRule.setVersion(transferRuleDTO.getVersion());
            corpTransferRuleRepo.save(corpTransRule);
            logger.info("Updated transfer rule for corporate with Id {}", transferRuleDTO.getCorporateId());
            return messageSource.getMessage("rule.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException ibe) {
            throw ibe;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rule.update.failure", null, locale), e);
        }
    }

    @Override
    public Page<CorporateRoleDTO> getRoles(Long corpId, Pageable pageable) {
        Corporate corporate = corporateRepo.findOne(corpId);
        Page<CorporateRole> page = corporateRoleRepo.findByCorporate(corporate, pageable);
        List<CorporateRoleDTO> dtOs = convertCorporateRoleEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CorporateRoleDTO> pageImpl = new PageImpl<CorporateRoleDTO>(dtOs, pageable, t);
        return pageImpl;
    }


    @Override
    public boolean corporateIdExists(String corporateId) {
        return corporateRepo.existsByCorporateIdIgnoreCase(corporateId);
    }

    @Override
    @Transactional
    public List<CorpTransferRuleDTO> getCorporateRules(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        List<CorpTransRule> transferRules = corpTransferRuleRepo.findByCorporate(corporate);
        Collections.sort(transferRules, new TransferRuleComparator());
        return convertTransferRuleEntitiesToDTOs(transferRules);

    }

    @Override
    @Transactional
    public Page<CorpTransferRuleDTO> getCorporateRules(Long corpId, Pageable pageable) {
        Corporate corporate = corporateRepo.findOne(corpId);
        Page<CorpTransRule> transferRules = corpTransferRuleRepo.findByCorporate(corporate, pageable);
        List<CorpTransRule> transRuleList = transferRules.getContent();
        List<CorpTransferRuleDTO> transferRuleDTOs = convertTransferRuleEntitiesToDTOs(transRuleList);
        Long t = transferRules.getTotalElements();
        PageImpl<CorpTransferRuleDTO> page = new PageImpl<CorpTransferRuleDTO>(transferRuleDTOs, pageable, t);
        return page;
    }

    @Override
    @Verifiable(operation = "DELETE_CORPORATE_RULE", description = "Delete Corporate Transfer Rule")
    public String deleteCorporateRule(Long id) throws InternetBankingException {
        try {
            CorpTransRule transferRule = corpTransferRuleRepo.findOne(id);
            corpTransferRuleRepo.delete(transferRule);
            logger.info("Updated transfer rule  with Id {}", id);
            return messageSource.getMessage("rule.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rule.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "ADD_CORPORATE_ROLE", description = "Adding a Corporate Role")
    public String addCorporateRole(CorporateRoleDTO roleDTO) throws InternetBankingException {


        if (roleDTO.getRank() < 1) {
            throw new InternetBankingException(messageSource.getMessage("auth.level.invalid", null, locale));
        }

        CorporateRole corporateRole = corporateRoleRepo.findFirstByNameAndRankAndCorporate_Id(roleDTO.getName(), roleDTO.getRank(), Long.parseLong(roleDTO.getCorporateId()));

        if (corporateRole != null) {
            throw new DuplicateObjectException(messageSource.getMessage("auth.level.exist", null, locale));
        }

        try {
            Corporate corporate = corporateRepo.findOne(NumberUtils.toLong(roleDTO.getCorporateId()));
            CorporateRole role = convertCorporateRoleDTOToEntity(roleDTO);
            role.setCorporate(corporate);

            HashSet<CorporateUser> corpUsers = new HashSet<>();
            for (CorporateUserDTO user : roleDTO.getUsers()) {
                CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
                corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
                corpUsers.add(corporateUser);
            }
            role.setUsers(corpUsers);
            corporateRoleRepo.save(role);
            return messageSource.getMessage("role.add.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.add.failure", null, locale), e);

        }


    }


    @Override
    @Verifiable(operation = "UPDATE_CORPORATE_ROLE", description = "Updating a Corporate Role")
    public String updateCorporateRole(CorporateRoleDTO roleDTO) throws InternetBankingException {

        if (roleDTO.getRank() < 1) {
            throw new InternetBankingException(messageSource.getMessage("auth.level.invalid", null, locale));
        }

        CorporateRole corporateRole = corporateRoleRepo.findFirstByNameAndRankAndCorporate_Id(roleDTO.getName(), roleDTO.getRank(), Long.parseLong(roleDTO.getCorporateId()));

        if (corporateRole != null && !roleDTO.getId().equals(corporateRole.getId())) {
            throw new DuplicateObjectException(messageSource.getMessage("auth.level.exist", null, locale));
        }


        try {
            CorporateRole role = corporateRoleRepo.findOne(roleDTO.getId());
            role.setVersion(roleDTO.getVersion());
            role.setName(roleDTO.getName());
            role.setRank(roleDTO.getRank());
            role.getUsers().clear();

            for (CorporateUserDTO user : roleDTO.getUsers()) {
                CorporateUser corporateUser = corporateUserRepo.findOne(user.getId());
                corporateUser.setCorpUserType(CorpUserType.AUTHORIZER);
                corporateUser.setAdmin(false);
                role.getUsers().add(corporateUser);
            }
            corporateRoleRepo.save(role);
            return messageSource.getMessage("role.update.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.update.failure", null, locale));

        }
    }


    @Override
    public CorporateRoleDTO getCorporateRole(Long id) {
        CorporateRole corporateRole = corporateRoleRepo.findOne(id);
        CorporateRoleDTO roleDTO = convertCorporateRoleEntityToDTO(corporateRole);
        return roleDTO;

    }

    @Override
    public Set<CorporateRoleDTO> getCorporateRoles(Long corporateId) {
        Corporate corporate = corporateRepo.findOne(corporateId);
        Set<CorporateRole> corporateRoles = corporate.getCorporateRoles();
        return convertCorporateRoleEntitiesToDTOs(corporateRoles);
    }

    @Override
    @Verifiable(operation = "DELETE_CORPORATE_ROLE", description = "Deleting a Corporate Role")
    public String deleteCorporateRole(Long id) throws InternetBankingException {

        CorporateRole role = corporateRoleRepo.findOne(id);
        if (!role.getUsers().isEmpty()) {
            throw new InternetBankingException(messageSource.getMessage("role.with.users", null, locale));
        }
        try {
            corporateRoleRepo.delete(role);
            return messageSource.getMessage("role.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("role.delete.failure", null, locale));
        }
    }


    @Override
    @Transactional
    public List<CorporateRoleDTO> getRoles(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        List<CorporateRole> corporateRoles = corporateRoleRepo.findByCorporate(corporate);
        sortRolesByRank(corporateRoles);
        List<CorporateRoleDTO> roles = convertCorporateRoleEntitiesToDTOs(corporateRoles);
        return roles;
    }

    @Override
    @Transactional
    public CorpTransRule getApplicableTransferRule(TransRequest transferRequest) {

        Corporate corporate = getCurrentUser().getCorporate();
        List<CorpTransRule> transferRules = corpTransferRuleRepo.findByCorporate(corporate);
        Collections.sort(transferRules, new TransferRuleComparator());
        BigDecimal transferAmount = transferRequest.getAmount();
        CorpTransRule applicableTransferRule = findApplicableRule(transferRules, transferAmount);
        return applicableTransferRule;
    }


    private CorpTransRule findApplicableRule(List<CorpTransRule> transferRules, BigDecimal transferAmount) {

        CorpTransRule applicableTransferRule = null;
        for (CorpTransRule transferRule : transferRules) {
            BigDecimal lowerLimit = transferRule.getLowerLimitAmount();
            BigDecimal upperLimit = transferRule.getUpperLimitAmount();

            if (transferAmount.compareTo(lowerLimit) >= 0 && (transferAmount.compareTo(upperLimit) <= 0)) {
                applicableTransferRule = transferRule;
                break;
            } else if (transferAmount.compareTo(lowerLimit) >= 0 && transferRule.isUnlimited()) {
                applicableTransferRule = transferRule;
            }
        }
        return applicableTransferRule;
    }

    private void sortRolesByRank(List<CorporateRole> roles) {
        Collections.sort(roles, new Comparator<CorporateRole>() {
            @Override
            public int compare(CorporateRole o1, CorporateRole o2) {
                return o1.getRank().compareTo(o2.getRank());
            }
        });
    }

    private CorporateRole convertCorporateRoleDTOToEntity(CorporateRoleDTO roleDTO) {
        CorporateRole corporateRole = new CorporateRole();
        corporateRole.setId(roleDTO.getId());
        corporateRole.setVersion(roleDTO.getVersion());
        corporateRole.setName(roleDTO.getName());
        corporateRole.setRank(roleDTO.getRank());
        corporateRole.setCorporate(corporateRepo.findOne(NumberUtils.toLong(roleDTO.getCorporateId())));
        Set<CorporateUserDTO> userDTOs = roleDTO.getUsers();
        Set<CorporateUser> users = new HashSet<CorporateUser>();
        for (CorporateUserDTO user : userDTOs) {
            CorporateUser corporateUser = new CorporateUser();
            corporateUser.setId(user.getId());
            users.add(corporateUser);
        }
        corporateRole.setUsers(users);
        return corporateRole;
    }

    private CorporateRoleDTO convertCorporateRoleEntityToDTO(CorporateRole role) {
        CorporateRoleDTO roleDTO = new CorporateRoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setVersion(role.getVersion());
        roleDTO.setName(role.getName());
        roleDTO.setRank(role.getRank());
        roleDTO.setCorporateId(role.getCorporate().getId().toString());
        Set<CorporateUserDTO> userDTOs = new HashSet<CorporateUserDTO>();
        for (CorporateUser user : role.getUsers()) {
            CorporateUserDTO userDTO = new CorporateUserDTO();
            userDTO.setId(user.getId());
            userDTO.setUserName(user.getUserName());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTOs.add(userDTO);

        }
        roleDTO.setUsers(userDTOs);

        return roleDTO;
    }

    private Set<CorporateRoleDTO> convertCorporateRoleEntitiesToDTOs(Set<CorporateRole> roles) {
        Set<CorporateRoleDTO> roleDTOs = new HashSet<>();
        for (CorporateRole role : roles) {
            CorporateRoleDTO roleDTO = convertCorporateRoleEntityToDTO(role);
            roleDTOs.add(roleDTO);
        }
        return roleDTOs;
    }

    private List<CorporateRoleDTO> convertCorporateRoleEntitiesToDTOs(List<CorporateRole> roles) {
        List<CorporateRoleDTO> roleDTOs = new ArrayList<>();
        for (CorporateRole role : roles) {
            CorporateRoleDTO roleDTO = convertCorporateRoleEntityToDTO(role);
            roleDTOs.add(roleDTO);
        }
        return roleDTOs;
    }

    private CorpTransferRuleDTO convertTransferRuleEntityToDTO(CorpTransRule transferRule) {
        CorpTransferRuleDTO corpTransferRuleDTO = new CorpTransferRuleDTO();
        corpTransferRuleDTO.setId(transferRule.getId());
        corpTransferRuleDTO.setVersion(transferRule.getVersion());
        corpTransferRuleDTO.setLowerLimitAmount(transferRule.getLowerLimitAmount().toString());
        corpTransferRuleDTO.setUpperLimitAmount(transferRule.isUnlimited() ? "UNLIMITED" : transferRule.getUpperLimitAmount().toString());
        corpTransferRuleDTO.setUnlimited(transferRule.isUnlimited());
        corpTransferRuleDTO.setCurrency(transferRule.getCurrency());
        corpTransferRuleDTO.setAnyCanAuthorize(transferRule.isAnyCanAuthorize());
        corpTransferRuleDTO.setRank(transferRule.isRank());
        corpTransferRuleDTO.setCorporateId(transferRule.getCorporate().getId().toString());
        corpTransferRuleDTO.setCorporateName(transferRule.getCorporate().getName());

        List<CorporateRoleDTO> roleDTOs = new ArrayList<>();
        for (CorporateRole role : transferRule.getRoles()) {
            CorporateRoleDTO roleDTO = new CorporateRoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setRank(role.getRank());
            if (!"Y".equals(role.getDelFlag())) {
                roleDTOs.add(roleDTO);
            }
        }
        corpTransferRuleDTO.setNumOfRoles(roleDTOs.size());
        corpTransferRuleDTO.setRoleNames(roleDTOs.toString());
        corpTransferRuleDTO.setRoles(roleDTOs);
        return corpTransferRuleDTO;
    }

    private CorpTransRule convertTransferRuleDTOToEntity(CorpTransferRuleDTO transferRuleDTO) {
        CorpTransRule corpTransRule = new CorpTransRule();
        corpTransRule.setLowerLimitAmount(new BigDecimal(transferRuleDTO.getLowerLimitAmount()));
        corpTransRule.setUpperLimitAmount(new BigDecimal(transferRuleDTO.getUpperLimitAmount()));
        corpTransRule.setUnlimited(transferRuleDTO.isUnlimited());
        corpTransRule.setCurrency(transferRuleDTO.getCurrency());
        corpTransRule.setAnyCanAuthorize(transferRuleDTO.isAnyCanAuthorize());
        corpTransRule.setRank(transferRuleDTO.isRank());
        corpTransRule.setCorporate(corporateRepo.findOne(Long.parseLong(transferRuleDTO.getCorporateId())));

        List<CorporateRole> roleList = new ArrayList<CorporateRole>();
        for (CorporateRoleDTO roleDTO : transferRuleDTO.getRoles()) {
            roleList.add(corporateRoleRepo.findOne((roleDTO.getId())));
        }
        corpTransRule.setRoles(roleList);
        return corpTransRule;
    }

    private List<CorpTransferRuleDTO> convertTransferRuleEntitiesToDTOs(List<CorpTransRule> transferRules) {
        List<CorpTransferRuleDTO> transferRuleDTOs = new ArrayList<CorpTransferRuleDTO>();
        for (CorpTransRule transferRule : transferRules) {
            CorpTransferRuleDTO transferRuleDTO = convertTransferRuleEntityToDTO(transferRule);
            transferRuleDTOs.add(transferRuleDTO);
        }

        return transferRuleDTOs;
    }

    private CorporateDTO convertEntityToDTO(Corporate corporate) {
        CorporateDTO corporateDTO = modelMapper.map(corporate, CorporateDTO.class);
        if (corporate.getCreatedOnDate() != null) {
            corporateDTO.setCreatedOnDate(DateFormatter.format(corporate.getCreatedOnDate()));
        }
        corporateDTO.setStatus(corporate.getStatus());
        return corporateDTO;
    }

    private Corporate convertDTOToEntity(CorporateDTO corporateDTO) {
        return modelMapper.map(corporateDTO, Corporate.class);
    }

    private List<CorporateDTO> convertEntitiesToDTOs(Iterable<Corporate> corporates) {
        List<CorporateDTO> corporateDTOList = new ArrayList<>();
        for (Corporate corporate : corporates) {
            CorporateDTO corporateDTO = convertEntityToDTO(corporate);
            Code code = codeService.getByTypeAndCode("CORPORATE_TYPE", corporate.getCorporateType());
            if (code != null) {
                corporateDTO.setCorporateType(code.getDescription());
            }
            corporateDTOList.add(corporateDTO);
        }
        return corporateDTOList;
    }

    @Override
    public Page<CorporateDTO> findCorporates(String pattern, Pageable pageDetails) {
        Page<Corporate> page = corporateRepo.findUsingPattern(pattern, pageDetails);
        List<CorporateDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<CorporateDTO> pageImpl = new PageImpl<CorporateDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    private CorporateUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        return (CorporateUser) user;
    }
}
