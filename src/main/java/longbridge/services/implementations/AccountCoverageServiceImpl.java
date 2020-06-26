package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.CorporateDTO;
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
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AccountCoverageServiceImpl implements AccountCoverageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AccountCoverageRepo accountCoverageRepo;
    @Autowired
    private CodeRepo codeRepo;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private AccountCoverageService coverageService;
    @Autowired
    private CodeService codeService;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private MessageSource messageSource;

    private ModelMapper modelMapper;

    private  String accountCoverage = "ACCOUNT_COVERAGE";



    @Autowired
    public AccountCoverageServiceImpl(AccountCoverageRepo accountCoverageRepository, ModelMapper modelMapper) {
        accountCoverageRepo = accountCoverageRepository;
        this.modelMapper = modelMapper;
    }



    @Override
    public String addCoverage(Long corpId,Long codeId) throws InternetBankingException {
        CorporateDTO corporate = corporateService.getCorporate(corpId);
        Code code =  codeService.getCodeById(codeId);

         try {

             Boolean value = accountCoverageRepo.coverageExist(corpId,codeId);
                if (!value){
                    AccountCoverage coverage =  new AccountCoverage();
                    coverage.setEnabled(false);
                    coverage.setCode(code);
                    coverage.setCorporate(modelMapper.map(corporate,Corporate.class));
                    System.out.println(coverage);
                    accountCoverageRepo.save(coverage);
                    System.out.println("Save");
                    logger.info("New Coverage {} added", coverage.getCode());
                    return messageSource.getMessage("Coverage.add.success", null, locale);
                }
                else
                {
                    return messageSource.getMessage("Coverage.already.exist",null ,locale);
                }
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("Coverage.add.failure", null, locale), e);
        }

    }

    @Override
    public List<CodeDTO> getCoverage() {
        Iterable<Code> codes = this.codeRepo.findByType(accountCoverage);
        List<CodeDTO> coverageList = new ArrayList<>();
        for (Code code : codes) {
            CodeDTO codeDTO =  modelMapper.map(code, CodeDTO.class);
            coverageList.add(codeDTO);
        }
        return coverageList;

    }

    //
//    @Override
//    public String  enableCoverage( String coverageJson) throws InternetBankingException, IOException {
//            AccountCoverage accountCoverage = parseJson(coverageJson);
//            String code = accountCoverage.getCode();
//        try {
//            accountCoverageRepo. enableCoverage(code,accountCoverage.isEnabled());
//            logger.info("Coverage {}  enable",code);
//            return messageSource.getMessage("Coverage. enable.success", null, locale);
//        } catch (VerificationInterruptedException e) {
//            return e.getMessage();
//        } catch (Exception e) {
//            throw new InternetBankingException(messageSource.getMessage("Coverage. enable.failure", null, locale), e);
//        }
//    }
//
//    @Override
//    @Transactional
//    @Verifiable(operation = "DELETE_COVERAGE", description = "Deleting a Coverage")
//    public String deleteCoverage(Long coverageId) throws InternetBankingException {
//        AccountCoverage coverage = accountCoverageRepo.findById(coverageId).get();
//
//        try {
//             accountCoverageRepo.delete(coverage);
//
//           logger.info("Coverage {} has been deleted", coverage.toString());
//            return messageSource.getMessage("accountCoverage.delete.success", null, locale);
//        } catch (VerificationInterruptedException e) {
//            return e.getMessage();
//        } catch (InternetBankingException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new InternetBankingException(messageSource.getMessage("accountCoverage.delete.failure", null, locale), e);
//        }
//    }
//
//    @Override
//    public List<String> enabledCoverageList(){
//        return accountCoverageRepo.getEnabledCoverage();
//    }
//
//    @Override
//    public Iterable<AccountCoverageDTO> getAllCoverage() {
//            Iterable<AccountCoverage> coverages = accountCoverageRepo.findAll();
//            return convertEntitiesToDTOs(coverages);
//        }
//    @Override
//    public Long getCoverageId(String coverageCode){
//        return accountCoverageRepo.getAccountCoverageByCode(coverageCode).getId();
//
//
//    }
//    @Override
//    public Long getCodeId(String coverageCode)
//    {
//
//        return accountCoverageRepo.getAccountCoverageByCode(coverageCode).getCodeEntity().getId();
//
//    }
//
//    public AccountCoverage convertDTOToEntity(AccountCoverageDTO accountCoverageDTO) {
//        return modelMapper.map(accountCoverageDTO, AccountCoverage.class);
//    }
//    public Code convertDTOToEntity(CodeDTO codeDTO) {
//        return modelMapper.map(codeDTO, Code.class);
//    }
//
//
//    public AccountCoverageDTO convertEntityToDTO(AccountCoverage coverage) {
//        return modelMapper.map(coverage, AccountCoverageDTO.class);
//    }
//
//
//    public List<AccountCoverageDTO> convertEntitiesToDTOs(Iterable<AccountCoverage> coverages) {
//        List<AccountCoverageDTO> coverageDTOList = new ArrayList<>();
//        for (AccountCoverage coverage : coverages) {
//            AccountCoverageDTO accountCoverageDTO = convertEntityToDTO(coverage);
//            coverageDTOList.add(accountCoverageDTO);
//        }
//        return coverageDTOList;
//    }
//
//    private AccountCoverage parseJson(String coverageJson ) throws JsonParseException, IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode coverageObj = mapper.readTree(coverageJson);
//        AccountCoverage accountCoverage = new AccountCoverage();
//        accountCoverage.setCode(coverageObj.get("code").toString().replaceAll("[\"']",""));
//        accountCoverage.setEnabled(coverageObj.get("isEnabled").asBoolean());
//        return accountCoverage;
//
//    }




}
