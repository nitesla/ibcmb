package longbridge.services.implementations;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.AccountCoverage;
import longbridge.models.Code;
import longbridge.models.Corporate;
import longbridge.repositories.AccountCoverageRepo;
import longbridge.repositories.CodeRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.AccountCoverageService;
import longbridge.services.CodeService;
import longbridge.services.CorporateService;
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

import java.io.IOException;
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
    private MessageSource messageSource;
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
            if(coverageRepo.coverageExist(corpId,h.getId())){
               AccountCoverage coverage = coverageRepo.getAccountCoverageByCodeAndCorporate(corpId,h.getId());
               coverageDTO.setEnabled(coverage.isEnabled());
               coverageDTO.setCode(coverage.getCode().getCode());
               coverageDTO.setDescription(coverage.getCode().getDescription());
               coverageDTO.setCodeId(h.getId());
                coverageDTO.setCorpId(corpId);
               coverageDTOList.add(coverageDTO);
               }
            else{
                coverageDTO.setEnabled(false);
                coverageDTO.setCode(h.getCode());
                coverageDTO.setDescription(h.getDescription());
                coverageDTO.setCodeId(h.getId());
                coverageDTO.setCorpId(corpId);
                coverageDTOList.add(coverageDTO);
            }
            });
      Page<AccountCoverageDTO> page = new PageImpl<AccountCoverageDTO>(coverageDTOList,pageDetails,coverageDTOList.size());
      return page;
    }

    @Override
    public String  enableCoverage(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException, IOException {
         Long codeId = updateCoverageDTO.getCodeId();
         Long corpId = updateCoverageDTO.getCorpId();
         Boolean enabled =updateCoverageDTO.getEnabled();
        try {
            if(coverageRepo.coverageExist(corpId,codeId)){
            coverageRepo.enableCoverage(corpId,codeId,enabled);
            }else {
               addCoverage(corpId,codeId);
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
    public void addCoverage(Long corpId,Long codeId) {
        Corporate corporate = corporateRepo.findById(corpId).get();
        Code code =  codeService.getCodeById(codeId);
        AccountCoverage coverage =  new AccountCoverage();
        coverage.setEnabled(false);
        coverage.setCode(code);
        coverage.setCorporate(corporate);
        coverageRepo.save(coverage);
       }


    @Override
    public List<AccountCoverage> getEnabledCoverageForCorporate(Long corpId) {
        return coverageRepo.getEnabledAccountCoverageByCorporate(corpId);
    }

    @Override
    public Boolean enabledCoverageExist(Long corpId) {
        return coverageRepo.enabledCoverageExist(corpId);
    }

    @Override
    public String getCustomerNumber(Long corpId) {
        return corporateRepo.findById(corpId).get().getCustomerId();
    }
}












