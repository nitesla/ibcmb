package longbridge.services.implementations;

import longbridge.api.AccountInfo;
import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RoleService roleService;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;


    private Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public CorporateServiceImpl(CorporateRepo corporateRepo, CorpLimitRepo corpLimitRepo, CorpTransferRuleRepo corpTransferRuleRepo) {
        this.corporateRepo = corporateRepo;
        this.corpLimitRepo = corpLimitRepo;
        this.corpTransferRuleRepo = corpTransferRuleRepo;
    }

    @Override
    @Transactional
    public String addCorporate(CorporateDTO corporateDTO, CorporateUserDTO user) throws InternetBankingException {

        Corporate corporate = corporateRepo.findByCustomerId(corporateDTO.getCustomerId());

        if (corporate != null) {
            throw new DuplicateObjectException(messageSource.getMessage("corporate.exist", null, locale));
        }


        CorporateUser corporateUser = corporateUserRepo.findFirstByUserName(user.getUserName());
        if (corporateUser != null) {
            throw new DuplicateObjectException(messageSource.getMessage("user.exist", null, locale));
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
            corporateUser.setCreatedOnDate(new Date());
            corporateUser.setStatus("A");
            String password = passwordPolicyService.generatePassword();
            corporateUser.setPassword(passwordEncoder.encode(password));
            corporateUser.setExpiryDate(new Date());
            if ("SOLE".equals(corporate.getCorporateType())) {
                Role role = roleRepo.findByUserTypeAndName(UserType.CORPORATE, "Sole Admin");
                corporateUser.setRole(role);
            } else if ("MULTI".equals(corporate.getCorporateType())) {
                Role role = roleRepo.findOne(Long.parseLong(user.getRoleId()));
                corporateUser.setRole(role);
            }
            corporateUser.setCorporate(corporate);
            corporate.getUsers().add(corporateUser);

            String fullName = corporateUser.getFirstName() + " " + corporateUser.getLastName();
            SettingDTO setting = configService.getSettingByName("ENABLE_ENTRUST_CREATION");
            if (setting != null && setting.isEnabled()) {
                if ("YES".equalsIgnoreCase(setting.getValue())) {
                    boolean result = securityService.createEntrustUser(corporateUser.getUserName(), fullName, true);
                    if (!result) {
                        throw new EntrustException(messageSource.getMessage("entrust.create.failure", null, locale));

                    }
                }
            }
            corporateRepo.save(corporate);
            sendUserCredentials(corporateUser, password);
            String customerId = corporate.getCustomerId();

            Collection<AccountInfo> accounts = integrationService.fetchAccounts(customerId);
            for (AccountInfo acct : accounts) {
                accountService.AddFIAccount(customerId, acct);
            }

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
    public String deleteCorporate(Long id) throws InternetBankingException {
        try {
            corporateRepo.delete(id);
            return messageSource.getMessage("corporate.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.delete.failure", null, locale));

        }
    }


    private void sendUserCredentials(CorporateUser user, String password) throws InternetBankingException {
        String fullName = user.getFirstName() + " " + user.getLastName();
        Email email = new Email.Builder()
                .setRecipient(user.getEmail())
                .setSubject(messageSource.getMessage("customer.create.subject", null, locale))
                .setBody(String.format(messageSource.getMessage("customer.create.message", null, locale), fullName, user.getUserName(), password))
                .build();
        mailService.send(email);
    }

    @Override
    public String updateCorporate(CorporateDTO corporateDTO) throws InternetBankingException {
        try {
            Corporate corporate = corporateRepo.findOne(corporateDTO.getId());
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
    public Corporate getCorporateByCustomerId(String customerId) {
        Corporate corporate = corporateRepo.findByCustomerId(customerId);
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
    public String changeActivationStatus(Long id) throws InternetBankingException {
        try {
            Corporate corporate = corporateRepo.findOne(id);
            String oldStatus = corporate.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            corporate.setStatus(newStatus);
            corporateRepo.save(corporate);
            logger.info("Corporate {} status changed from {} to {}", corporate.getName(), oldStatus, newStatus);
            return messageSource.getMessage("corporate.status.success", null, locale);

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

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
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
    public String addCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException {


        if (new BigDecimal(transferRuleDTO.getLowerLimitAmount()).compareTo(new BigDecimal("0")) < 0) {
            throw new TransferRuleException(messageSource.getMessage("rule.amount.zero", null, locale));
        }
        if (new BigDecimal(transferRuleDTO.getUpperLimitAmount()).compareTo(new BigDecimal(transferRuleDTO.getLowerLimitAmount())) < 0) {
            throw new TransferRuleException(messageSource.getMessage("rule.range.violation", null, locale));
        }

        try {
            CorpTransRule corpTransRule = convertTransferRuleDTOToEntity(transferRuleDTO);
            Corporate corporate = corpTransRule.getCorporate();
            corporate.getCorpTransRules().add(corpTransRule);
            corporateRepo.save(corporate);
            logger.info("Added transfer rule for corporate with Id {}", transferRuleDTO.getCorporateId());
            return messageSource.getMessage("rule.add.success", null, locale);
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
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rule.update.failure", null, locale), e);
        }
    }

    @Override
    public List<CorpTransferRuleDTO> getCorporateRules() {
        List<CorpTransRule> transferRules = corpTransferRuleRepo.findAll();
        return convertTransferRuleEntitiesToDTOs(transferRules);
    }

    @Override
    @Transactional
    public List<CorpTransferRuleDTO> getCorporateRules(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        List<CorpTransRule> transferRules = corporate.getCorpTransRules();
        Collections.sort(transferRules, new TransferRuleComparator());
        return convertTransferRuleEntitiesToDTOs(transferRules);
    }

    @Override
    public String deleteCorporateRule(Long id) throws InternetBankingException {
        try {
            CorpTransRule transferRule = corpTransferRuleRepo.findOne(id);
            Corporate corporate = corporateRepo.findOne(transferRule.getCorporate().getId());

            corporate.getCorpTransRules().remove(transferRule);
            corporateRepo.save(corporate);
            logger.info("Updated transfer rule  with Id {}", id);
            return messageSource.getMessage("rule.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rule.delete.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public List<CorporateUserDTO> getAuthorizers(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        Collection<CorporateUser> corporateUsers = corporate.getUsers();
        List<CorporateUserDTO> authorizers = new ArrayList<CorporateUserDTO>();
        CorporateUserDTO userDTO;
        for (CorporateUser user : corporateUsers) {
            if ("Authorizer".equalsIgnoreCase(user.getRole().getName())) {
                userDTO = new CorporateUserDTO();
                userDTO.setId(user.getId());
                userDTO.setUserName(user.getUserName());
                userDTO.setFirstName(user.getFirstName());
                userDTO.setLastName(user.getLastName());
                authorizers.add(userDTO);

            }
        }
        return authorizers;
    }

    @Override
    @Transactional
    public CorpTransRule getApplicableTransferRule(CorpTransRequest transferRequest) {
        Corporate corporate = transferRequest.getCorporate();
        List<CorpTransRule> transferRules = corporate.getCorpTransRules();
        Collections.sort(transferRules, new TransferRuleComparator());
        BigDecimal transferAmount = transferRequest.getAmount();
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

    @Override
    @Transactional
    public List<CorporateUser> getQualifiedAuthorizers(CorpTransRequest transferRequest) {
        Corporate corporate = transferRequest.getCorporate();
        List<CorpTransRule> transferRules = corporate.getCorpTransRules();
        Collections.sort(transferRules, new TransferRuleComparator());
        BigDecimal transferAmount = transferRequest.getAmount();
        CorpTransRule applicableTransferRule = null;
        for (CorpTransRule transferRule : transferRules) {
            BigDecimal lowerLimit = transferRule.getLowerLimitAmount();
            BigDecimal upperLimit = transferRule.getUpperLimitAmount();
            if (transferAmount.compareTo(lowerLimit) >= 0 && transferAmount.compareTo(upperLimit) <= 0) {
                applicableTransferRule = transferRule;
                break;
            } else if (transferAmount.compareTo(lowerLimit) >= 0 && transferRule.isUnlimited()) {
                applicableTransferRule = transferRule;
            }
        }
        List<CorporateUser> authorizers = new ArrayList<>();
        if (applicableTransferRule != null) {
            authorizers = applicableTransferRule.getAuths();
        }
        return authorizers;
    }


    private CorpTransferRuleDTO convertTransferRuleEntityToDTO(CorpTransRule transferRule) {
        CorpTransferRuleDTO corpTransferRuleDTO = new CorpTransferRuleDTO();
        corpTransferRuleDTO.setId(transferRule.getId());
        corpTransferRuleDTO.setVersion(transferRule.getVersion());
        corpTransferRuleDTO.setLowerLimitAmount(transferRule.getLowerLimitAmount().toString());
        corpTransferRuleDTO.setUpperLimitAmount(transferRule.isUnlimited() ? "Unlimited" : transferRule.getUpperLimitAmount().toString());
        corpTransferRuleDTO.setUnlimited(transferRule.isUnlimited());
        corpTransferRuleDTO.setCurrency(transferRule.getCurrency());
        corpTransferRuleDTO.setAnyCanAuthorize(transferRule.isAnyCanAuthorize());
        corpTransferRuleDTO.setCorporateId(transferRule.getCorporate().getId().toString());
        corpTransferRuleDTO.setCorporateName(transferRule.getCorporate().getName());

        List<CorporateUserDTO> authorizerList = new ArrayList<CorporateUserDTO>();
        for (CorporateUser authorizer : transferRule.getAuths()) {
            CorporateUserDTO authorizerDTO = new CorporateUserDTO();
            authorizerDTO.setId(authorizer.getId());
            authorizerDTO.setUserName(authorizer.getUserName());
            authorizerDTO.setFirstName(authorizer.getFirstName());
            authorizerDTO.setLastName(authorizer.getLastName());
            authorizerDTO.setRole(authorizer.getRole().getName());
            authorizerList.add(authorizerDTO);
        }
        corpTransferRuleDTO.setNumOfAuthorizers(authorizerList.size());
        corpTransferRuleDTO.setAuthorizers(authorizerList);
        return corpTransferRuleDTO;
    }

    private CorpTransRule convertTransferRuleDTOToEntity(CorpTransferRuleDTO transferRuleDTO) {
        CorpTransRule corpTransRule = new CorpTransRule();
        corpTransRule.setLowerLimitAmount(new BigDecimal(transferRuleDTO.getLowerLimitAmount()));
        corpTransRule.setUpperLimitAmount(new BigDecimal(transferRuleDTO.getUpperLimitAmount()));
        corpTransRule.setUnlimited(transferRuleDTO.isUnlimited());
        corpTransRule.setCurrency(transferRuleDTO.getCurrency());
        corpTransRule.setAnyCanAuthorize(transferRuleDTO.isAnyCanAuthorize());
        corpTransRule.setCorporate(corporateRepo.findOne(Long.parseLong(transferRuleDTO.getCorporateId())));

        List<CorporateUser> authorizerList = new ArrayList<CorporateUser>();
        for (CorporateUserDTO authorizer : transferRuleDTO.getAuthorizers()) {
            authorizerList.add(corporateUserRepo.findOne(authorizer.getId()));
        }
        corpTransRule.setAuths(authorizerList);
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
            corporateDTO.setCreatedOn(DateFormatter.format(corporate.getCreatedOnDate()));
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
    //    private List<AccountDTO> convertAccountEntitiesToDTOs(Iterable<Account> accounts){
//        List<AccountDTO> accountDTOList = new ArrayList<>();
//        for(Account account: accounts){
//            AccountDTO accountDTO = convertAccountEntityToDTO(account);
//            accountDTOList.add(accountDTO);
//        }
//        return accountDTOList;
//    }
//
//    private AccountDTO convertAccountEntityToDTO(Account account){
//        return  modelMapper.map(account,AccountDTO.class);
//    }
}
