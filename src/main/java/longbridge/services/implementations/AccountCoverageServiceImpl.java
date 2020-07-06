package longbridge.services.implementations;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.AccountCoverage;
import longbridge.models.Code;
import longbridge.models.Corporate;
import longbridge.models.RetailUser;
import longbridge.repositories.AccountCoverageRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.AccountCoverageService;
import longbridge.services.CodeService;
import longbridge.services.IntegrationService;
import org.json.simple.JSONObject;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AccountCoverageServiceImpl implements AccountCoverageService {


    @Autowired
    private AccountCoverageRepo coverageRepo;
    @Autowired
    private CodeService codeService;
    @Autowired
    private CorporateRepo corporateRepo;
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private IntegrationService integrationService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();
    private ModelMapper modelMapper;
    private  final String accountCoverage = "ACCOUNT_COVERAGE";


    @Autowired
    public AccountCoverageServiceImpl(AccountCoverageRepo accountCoverageRepository, ModelMapper modelMapper) {
        coverageRepo = accountCoverageRepository;
        this.modelMapper = modelMapper;
    }




    @Override
    public Page<AccountCoverageDTO> getAllCoverageForCorporate(Long corpId, Pageable pageDetails) {
      List<CodeDTO> codeDTOList = codeService.getCodesByType(accountCoverage);
      List<AccountCoverageDTO> coverageDTOList = new ArrayList<>();
        codeDTOList.stream().forEach(h -> {
            AccountCoverageDTO coverageDTO = new AccountCoverageDTO();
            if(coverageRepo.coverageExistForCorporate(corpId,h.getId())){
               AccountCoverage coverage = coverageRepo.getAccountCoverageByCodeAndCorporate(corpId,h.getId());
               coverageDTO.setEnabled(coverage.isEnabled());
               coverageDTO.setCode(coverage.getCode().getCode());
               coverageDTO.setDescription(coverage.getCode().getDescription());
            }
            else{
                coverageDTO.setEnabled(false);
                coverageDTO.setCode(h.getCode());
                coverageDTO.setDescription(h.getDescription());
            }
            coverageDTO.setCodeId(h.getId());
            coverageDTO.setCorpId(corpId);
            coverageDTOList.add(coverageDTO);
        });
      Page<AccountCoverageDTO> page = new PageImpl<AccountCoverageDTO>(coverageDTOList,pageDetails,coverageDTOList.size());
      return page;
    }

    @Override
    public String  enableCoverageForCorporate(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException {
         Long codeId = updateCoverageDTO.getCodeId();
         Long corpId = updateCoverageDTO.getCorpId();
         Boolean enabled =updateCoverageDTO.getEnabled();
        try {
            if(coverageRepo.coverageExistForCorporate(corpId,codeId)){
                logger.info("Coverage Exist");
            coverageRepo.enableCoverageForCorporate(corpId,codeId,enabled);
            }else {
                logger.info("adding Coverage");
               addCoverageForCorporate(corpId,codeId);
            }
            logger.info("Coverage  enable");
            return messageSource.getMessage("Coverage. enable.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("Coverage. enable.failure", null, locale), e);
        }
    }
    @Override
    @Transactional
    public void addCoverageForCorporate(Long corpId,Long codeId) {
        Corporate corporate = corporateRepo.findById(corpId).get();
        String customerId = corporate.getCustomerId();
        Code code =  codeService.getCodeById(codeId);
        AccountCoverage coverage =  new AccountCoverage();
        coverage.setEnabled(false);
        coverage.setCode(code);
        coverage.setCorporate(corporate);
        coverage.setCustomerId(customerId);
        coverageRepo.save(coverage);
       }


    @Override
    public List<AccountCoverage> getEnabledCoverageForCorporate(Long corpId) {
        return coverageRepo.getEnabledAccountCoverageByCorporate(corpId);
    }

    @Override
    public List<CoverageDetailsDTO> getAllEnabledCoverageDetailsForCorporate(Long corpId) {
        return integrationService.getAllEnabledCoverageDetailsForCorporateFromEndPoint(corpId);
    }

    @Override
    public Page<AccountCoverageDTO> getAllCoverageForRetailUser(Long retId, Pageable pageDetails) {
        List<CodeDTO> codeDTOList = codeService.getCodesByType(accountCoverage);
        List<AccountCoverageDTO> coverageDTOList = new ArrayList<>();
        codeDTOList.stream().forEach(h -> {
            AccountCoverageDTO coverageDTO = new AccountCoverageDTO();
            if(coverageRepo.coverageExistForRetailUser(retId,h.getId())){
                AccountCoverage coverage = coverageRepo.getAccountCoverageByCodeAndRetailUser(retId,h.getId());
                coverageDTO.setEnabled(coverage.isEnabled());
                coverageDTO.setCode(coverage.getCode().getCode());
                coverageDTO.setDescription(coverage.getCode().getDescription());
            }
            else{
                coverageDTO.setEnabled(false);
                coverageDTO.setCode(h.getCode());
                coverageDTO.setDescription(h.getDescription());
            }
            coverageDTO.setCodeId(h.getId());
            coverageDTO.setRetId(retId);
            coverageDTOList.add(coverageDTO);
        });
        Page<AccountCoverageDTO> page = new PageImpl<AccountCoverageDTO>(coverageDTOList,pageDetails,coverageDTOList.size());
        return page;
    }

    @Override
    public String enableCoverageForRetailUser(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException {
        Long codeId = updateCoverageDTO.getCodeId();
        Long retId = updateCoverageDTO.getRetId();
        Boolean enabled =updateCoverageDTO.getEnabled();
        try {
            if(coverageRepo.coverageExistForRetailUser(retId,codeId)){
                logger.info("Coverage Exist");
                coverageRepo.enableCoverageForRetailUser(retId,codeId,enabled);
            }else {
                logger.info("adding Coverage");
                addCoverageForRetailUser(retId,codeId);
            }
            logger.info("Coverage  enable");
            return messageSource.getMessage("Coverage. enable.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("Coverage. enable.failure", null, locale), e);
        }
    }

    @Override
    @Transactional
    public void addCoverageForRetailUser(Long retId, Long codeId) {
        RetailUser retailUser = retailUserRepo.findById(retId).get();
        Code code =  codeService.getCodeById(codeId);
        AccountCoverage coverage =  new AccountCoverage();
        coverage.setEnabled(false);
        coverage.setCode(code);
        coverage.setRetailUser(retailUser);
        coverageRepo.save(coverage);

    }

    @Override
    public List<AccountCoverage> getEnabledCoverageForRetailUser(Long retId) {
        return null;
    }

    @Override
    public List<CoverageDetailsDTO> getAllEnabledCoverageDetailsForRetailUser(Long retId) {
        return integrationService.getAllEnabledCoverageDetailsForRetailFromEndPoint(retId);
    }

    @Override
    public List<CoverageDetailsDTO> getAllEnabledCoverageDetailsForCustomer(String customerId) {
        List<CoverageDetailsDTO> coverageDetailsDTOList = new ArrayList<>();

        if (coverageRepo.enabledCoverageExistForCustomer(customerId)){
            List<AccountCoverage> enabledCoverageList =coverageRepo.getEnabledAccountCoverageByCustomerId(customerId);

            for (AccountCoverage enabledCoverage:enabledCoverageList ) {
                coverageDetailsDTOList.add(integrationService.getCoverageDetails(enabledCoverage.getCode().getCode(),customerId));

            }
        }
        return coverageDetailsDTOList;
    }
}












