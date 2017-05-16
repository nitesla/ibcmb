package longbridge.services.implementations;

import longbridge.api.AccountInfo;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.CorporateDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.CorporateService;
import longbridge.services.IntegrationService;
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

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Fortune on 4/5/2017.
 */
@Service
public class CorporateServiceImpl implements CorporateService {

    private CorporateRepo corporateRepo;
    private CorpLimitRepo corpLimitRepo;
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



    @Autowired
    public CorporateServiceImpl(CorporateRepo corporateRepo, CorpLimitRepo corpLimitRepo){
        this.corporateRepo = corporateRepo;
        this.corpLimitRepo = corpLimitRepo;
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
    public List<CorporateDTO> getCorporates() {
        Iterable<Corporate> corporateDTOS = corporateRepo.findAll();
        return convertEntitiesToDTOs(corporateDTOS);
    }


    @Override
    public boolean addAccount(Corporate corporate, Account account) throws  InternetBankingException {
        accountService.AddAccount(corporate.getCustomerId(),account);
        return true;
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

    private CorporateDTO convertEntityToDTO(Corporate corporate){
        CorporateDTO corporateDTO = modelMapper.map(corporate,CorporateDTO.class);
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

}
