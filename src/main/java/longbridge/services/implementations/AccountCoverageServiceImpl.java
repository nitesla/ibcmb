package longbridge.services.implementations;

import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.AccountCoverage;
import longbridge.models.Code;
import longbridge.repositories.AccountCoverageRepo;
import longbridge.repositories.CodeRepo;
import longbridge.services.AccountCoverageService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AccountCoverageServiceImpl implements AccountCoverageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AccountCoverageRepo accountCoverageRepo;
    private CodeRepo codeRepo;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private MessageSource messageSource;

    private ModelMapper modelMapper;



    @Autowired
    public AccountCoverageServiceImpl(AccountCoverageRepo accountCoverageRepository, ModelMapper modelMapper) {
        accountCoverageRepo = accountCoverageRepository;
        this.modelMapper = modelMapper;
    }



    @Override
    public String addCoverage(Code code) throws InternetBankingException {
        System.out.println("Hello");
        try {
            AccountCoverage coverage =  new AccountCoverage();
            coverage.setCode(code.getCode());
            coverage.setEnabled(false);
            System.out.println(coverage);
//            accountCoverageRepo.save(coverage);
            logger.info("New Coverage {} added", coverage.getCode());
            return messageSource.getMessage("Coverage.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("Coverage.add.failure", null, locale), e);
        }

    }

    @Override
    public String updateCoverage(String id) {
        return null;
    }

    @Override
    public Iterable<AccountCoverageDTO> getAllCoverage() {
            Iterable<AccountCoverage> coverages = accountCoverageRepo.findAll();
            return convertEntitiesToDTOs(coverages);
        }



    public AccountCoverage convertDTOToEntity(AccountCoverageDTO accountCoverageDTO) {
        return modelMapper.map(accountCoverageDTO, AccountCoverage.class);
    }

    public AccountCoverageDTO convertEntityToDTO(AccountCoverage coverage) {
        return modelMapper.map(coverage, AccountCoverageDTO.class);
    }


    public List<AccountCoverageDTO> convertEntitiesToDTOs(Iterable<AccountCoverage> coverages) {
        List<AccountCoverageDTO> coverageDTOList = new ArrayList<>();
        for (AccountCoverage coverage : coverages) {
            AccountCoverageDTO accountCoverageDTO = convertEntityToDTO(coverage);
            coverageDTOList.add(accountCoverageDTO);
        }
        return coverageDTOList;
    }



}
