package longbridge.services.implementations;

import longbridge.api.AccountInfo;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.TransferRuleException;
import longbridge.models.*;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorpTransferRuleRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.CorporateService;
import longbridge.services.IntegrationService;
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
    private CodeService  codeService;
    @Autowired
    private IntegrationService integrationService;

    private Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public CorporateServiceImpl(CorporateRepo corporateRepo, CorpLimitRepo corpLimitRepo, CorpTransferRuleRepo corpTransferRuleRepo) {
        this.corporateRepo = corporateRepo;
        this.corpLimitRepo = corpLimitRepo;
        this.corpTransferRuleRepo = corpTransferRuleRepo;
    }

    @Override
    public String addCorporate(CorporateDTO corporateDTO) throws InternetBankingException {
        try{
            Corporate corporate = convertDTOToEntity(corporateDTO);
            corporate.setCreatedOnDate(new Date());
            corporateRepo.save(corporate);
            String customerId = corporate.getCustomerId();
            Collection<AccountInfo> accounts = integrationService.fetchAccounts(customerId);
            for (AccountInfo acct : accounts) {
                accountService.AddFIAccount(customerId, acct);
            }

            logger.info("Corporate {} created", corporate.getCompanyName());
            return messageSource.getMessage("corporate.add.success", null, locale);
        }
        catch (Exception e){
                throw new InternetBankingException(messageSource.getMessage("corporate.add.failure",null,locale),e);
        }

    }

    @Override
    public String deleteCorporate(Long id) throws InternetBankingException {
        try {
            corporateRepo.delete(id);
            return messageSource.getMessage("corporate.delete.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("corporate.delete.failure",null,locale));

        }
    }

    @Override
    public String updateCorporate(CorporateDTO corporateDTO) throws InternetBankingException {
        try {
            Corporate corporate = corporateRepo.findOne(corporateDTO.getId());
            corporate.setVersion(corporateDTO.getVersion());
            corporate.setEmail(corporateDTO.getEmail());
            corporate.setCompanyName(corporateDTO.getCompanyName());
            corporate.setAddress(corporateDTO.getAddress());
            corporateRepo.save(corporate);
            return messageSource.getMessage("corporate.update.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("corporate.update.failure",null,locale),e);

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
    public String addAccount(Corporate corporate, AccountDTO accountDTO) throws  InternetBankingException {
        accountService.AddAccount(corporate.getCustomerId(),accountDTO);
        return messageSource.getMessage("account.add.success",null,locale);
    }

    @Override
    public String addCorporateUser(Corporate corporate, CorporateUser corporateUser) throws InternetBankingException{
        corporate.getUsers().add(corporateUser);
        corporateRepo.save(corporate);
        return  messageSource.getMessage("user.add.success",null,locale);
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
            logger.info("Corporate {} status changed from {} to {}", corporate.getCompanyName(), oldStatus, newStatus);
            return messageSource.getMessage("corporate.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("corporate.status.failure", null, locale), e);

        }
    }


    @Override
    @Transactional
    public String changeUserActivationStatus(Long id) throws InternetBankingException {
        try {
            CorporateUser corporateUser = corporateUserRepo.findOne(id);
            String oldStatus = corporateUser.getStatus();
            String newStatus = "A".equals(oldStatus) ? "I" : "A";
            corporateUser.setStatus(newStatus);
            corporateUserRepo.save(corporateUser);
            logger.info("Corporate user{} status changed from {} to {}", corporateUser.getUserName(), oldStatus, newStatus);
            return messageSource.getMessage("user.status.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("user.status.failure", null, locale), e);

        }
    }

    @Override
    public void setLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException{
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
        Page<CorporateDTO> pageImpl = new PageImpl<CorporateDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}

    @Override
    public Page<AccountDTO> getAccounts(Long corpId, Pageable pageDetails) {
        Corporate corporate = corporateRepo.findOne(corpId);
        Page<AccountDTO> page = accountService.getAccounts(corporate.getCustomerId(), pageDetails);
        List<AccountDTO> dtOs = page.getContent();
        long t = page.getTotalElements();
        Page<AccountDTO> pageImpl = new PageImpl<AccountDTO>(dtOs,pageDetails,t);
        return pageImpl;
    }

    @Override
    public String addCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException {


        try{
            CorpTransferRule corpTransferRule = convertTransferRuleDTOToEntity(transferRuleDTO);
            corpTransferRuleRepo.save(corpTransferRule);
            logger.info("Added transfer rule for corporate with Id {}",transferRuleDTO.getCorporateId());
            return messageSource.getMessage("rule.add.success",null,locale);
        }
        catch (Exception e){
         throw new InternetBankingException(messageSource.getMessage("rule.add.failure",null,locale),e);
        }
    }

    @Override
    public CorpTransferRuleDTO getCorporateRule(Long id) {
        CorpTransferRule transferRule = corpTransferRuleRepo.findOne(id);
        return convertTransferRuleEntityToDTO(transferRule);
    }

    @Override
    @Transactional
    public String updateCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException {
        try{
            CorpTransferRule corpTransferRule = convertTransferRuleDTOToEntity(transferRuleDTO);
            corpTransferRule.setId(transferRuleDTO.getId());
            corpTransferRule.setVersion(transferRuleDTO.getVersion());
            corpTransferRuleRepo.save(corpTransferRule);
            logger.info("Updated transfer rule for corporate with Id {}",transferRuleDTO.getCorporateId());
            return messageSource.getMessage("rule.update.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("rule.update.failure",null,locale),e);
        }
    }

    @Override
    public List<CorpTransferRuleDTO> getCorporateRules() {
        List<CorpTransferRule> transferRules = corpTransferRuleRepo.findAll();
        return convertTransferRuleEntitiesToDTOs(transferRules);
    }

    @Override
    @Transactional
    public List<CorpTransferRuleDTO> getCorporateRules(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        List<CorpTransferRule> transferRules = corpTransferRuleRepo.findByCorporate(corporate);
        return convertTransferRuleEntitiesToDTOs(transferRules);
    }

    @Override
    public String deleteCorporateRule(Long id) throws InternetBankingException {
        try{
            corpTransferRuleRepo.delete(id);
            logger.info("Updated transfer rule  with Id {}",id);
            return messageSource.getMessage("rule.delete.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("rule.delete.failure",null,locale),e);
        }
    }

    @Override
    @Transactional
    public List<CorporateUserDTO> getAuthorizers(Long corpId) {
        Corporate corporate = corporateRepo.findOne(corpId);
        Collection<CorporateUser> corporateUsers = corporate.getUsers();
        List<CorporateUserDTO> authorizers = new ArrayList<CorporateUserDTO>();
        CorporateUserDTO userDTO;
        for (CorporateUser user : corporateUsers){
            if("Authorizer".equalsIgnoreCase(user.getRole().getName())){
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
    public List<CorporateUser> getQualifiedAuthorizers(CorpTransferRequest transferRequest) {
        Corporate corporate = transferRequest.getCorporate();
        List<CorpTransferRule> transferRules = corporate.getCorpTransferRules();
        Collections.sort(transferRules, new TransferRuleComparator());
        BigDecimal transferAmount = transferRequest.getAmount();
        CorpTransferRule applicableTransferRule = null;
        for(CorpTransferRule transferRule: transferRules){
            BigDecimal loweLimit = transferRule.getLowerLimitAmount();
            BigDecimal upperLimit = transferRule.getUpperLimitAmount();
            if(transferAmount.compareTo(loweLimit)>=0&&(transferAmount.compareTo(upperLimit)<=0)){
                applicableTransferRule = transferRule;
            }
            else if(transferAmount.compareTo(upperLimit)>0&&transferRule.isInfinite()){
                applicableTransferRule = transferRule;
            }
        }
        List<CorporateUser> authorizers = new ArrayList<>();
        if(applicableTransferRule!=null){
            authorizers = applicableTransferRule.getAuthorizers();
        }
        return authorizers;
    }

    private void validateTransferRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException{
        BigDecimal lowerLimit = transferRuleDTO.getLowerLimitAmount();
        BigDecimal upperLimit = transferRuleDTO.getUpperLimitAmount();
        if(upperLimit.compareTo(lowerLimit)<0){
            throw new TransferRuleException(messageSource.getMessage("rule.range.violation",null,locale));
        }

    }

    private CorpTransferRuleDTO convertTransferRuleEntityToDTO(CorpTransferRule transferRule){
        CorpTransferRuleDTO corpTransferRuleDTO = new CorpTransferRuleDTO();
        corpTransferRuleDTO.setId(transferRule.getId());
        corpTransferRuleDTO.setVersion(transferRule.getVersion());
        corpTransferRuleDTO.setLowerLimitAmount(transferRule.getLowerLimitAmount());
        corpTransferRuleDTO.setUpperLimitAmount(transferRule.getUpperLimitAmount());
        corpTransferRuleDTO.setCurrency(transferRule.getCurrency());
        corpTransferRuleDTO.setAnyOne(transferRule.isAnyOne());
        corpTransferRuleDTO.setCorporateId(transferRule.getCorporate().getId().toString());
        corpTransferRuleDTO.setCorporateName(transferRule.getCorporate().getCompanyName());

        List<CorporateUserDTO> authorizerList = new ArrayList<CorporateUserDTO>();
        for (CorporateUser authorizer: transferRule.getAuthorizers()){
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

    private CorpTransferRule convertTransferRuleDTOToEntity(CorpTransferRuleDTO transferRuleDTO){
        CorpTransferRule corpTransferRule = new CorpTransferRule();
        corpTransferRule.setLowerLimitAmount(transferRuleDTO.getLowerLimitAmount());
        corpTransferRule.setUpperLimitAmount(transferRuleDTO.getUpperLimitAmount());
        corpTransferRule.setCurrency(transferRuleDTO.getCurrency());
        corpTransferRule.setAnyOne(transferRuleDTO.isAnyOne());
        corpTransferRule.setCorporate(corporateRepo.findOne(Long.parseLong(transferRuleDTO.getCorporateId())));

        List<CorporateUser> authorizerList = new ArrayList<CorporateUser>();
        for (CorporateUserDTO authorizer: transferRuleDTO.getAuthorizers()){
            authorizerList.add(corporateUserRepo.findOne(authorizer.getId()));
        }
        corpTransferRule.setAuthorizers(authorizerList);
        return corpTransferRule;
    }

    private List<CorpTransferRuleDTO> convertTransferRuleEntitiesToDTOs(List<CorpTransferRule> transferRules){
        List<CorpTransferRuleDTO> transferRuleDTOs = new ArrayList<CorpTransferRuleDTO>();
        for(CorpTransferRule transferRule: transferRules){
            CorpTransferRuleDTO transferRuleDTO = convertTransferRuleEntityToDTO(transferRule);
            transferRuleDTOs.add(transferRuleDTO);
        }
        return transferRuleDTOs;
    }



    private CorporateDTO convertEntityToDTO(Corporate corporate){
        CorporateDTO corporateDTO = modelMapper.map(corporate,CorporateDTO.class);
        if(corporate.getCreatedOnDate()!=null){
            corporateDTO.setCreatedOn(DateFormatter.format(corporate.getCreatedOnDate()));
        }
        Code code = codeService.getByTypeAndCode("USER_STATUS", corporate.getStatus());
        if (code != null) {
            corporateDTO.setStatus(code.getDescription());
        }
        return corporateDTO;
    }

    private Corporate convertDTOToEntity(CorporateDTO corporateDTO){
        return  modelMapper.map(corporateDTO,Corporate.class);
    }

    private List<CorporateDTO> convertEntitiesToDTOs(Iterable<Corporate> corporates){
        List<CorporateDTO> corporateDTOList = new ArrayList<>();
        for(Corporate corporate: corporates){
            CorporateDTO corporateDTO = convertEntityToDTO(corporate);
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
