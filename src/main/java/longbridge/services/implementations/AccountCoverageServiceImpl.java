package longbridge.services.implementations;


import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.AccountCoverage;
import longbridge.models.Corporate;
import longbridge.models.EntityId;
import longbridge.models.UserType;
import longbridge.repositories.AccountCoverageRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class AccountCoverageServiceImpl implements AccountCoverageAdministrationService {


    private final String accountCoverage = "ACCOUNT_COVERAGE";
    @Autowired
    private AccountCoverageRepo coverageRepo;
    @Autowired
    private CodeService codeService;
    @Autowired
    private CorporateService corporateService;
    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private IntegrationService integrationService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();
    private ModelMapper modelMapper;


    @Autowired
    public AccountCoverageServiceImpl(AccountCoverageRepo accountCoverageRepository, ModelMapper modelMapper) {
        coverageRepo = accountCoverageRepository;
        this.modelMapper = modelMapper;
    }


    private AccountCoverageDTO createDto(AccountCoverage coverage) {
        AccountCoverageDTO dto = new AccountCoverageDTO();
        dto.setCode(coverage.getCode());
        dto.setEnabled(coverage.isEnabled());
        return dto;
    }


    @Override
    public Page<AccountCoverageDTO> getAllCoverage(EntityId id, Pageable pageDetails) {
        Page<AccountCoverage> coverageList = coverageRepo.findByEntityId(id, pageDetails);
        return coverageList.map(this::createDto);
    }

    @Override
    public void updateCoverage(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException {
        Optional<AccountCoverage> firstByEntityIdAndCode = coverageRepo.findFirstByEntityIdAndCode(updateCoverageDTO.getId(), updateCoverageDTO.getCode());
        AccountCoverage accountCoverage = firstByEntityIdAndCode.orElse(createNew(updateCoverageDTO.getId()));
        accountCoverage.setEnabled(updateCoverageDTO.getEnabled());
        accountCoverage.setCode(updateCoverageDTO.getCode());
        accountCoverage.setEntityId(updateCoverageDTO.getId());
        coverageRepo.save(accountCoverage);
    }

    private AccountCoverage createNew(EntityId id) {
        AccountCoverage accountCoverage = new AccountCoverage();
        accountCoverage.setEntityId(id);
        return accountCoverage;
    }

    @Override
    public void addCoverage(AddCoverageDTO addCoverageDTO) {
        AccountCoverage accountCoverage = createNew(addCoverageDTO.getId());
        accountCoverage.setEnabled(true);
        coverageRepo.save(accountCoverage);
    }

    @Override
    public AccountCoverage getCoverage(EntityId id, String code) {
        return coverageRepo.findFirstByEntityIdAndCode(id, code).orElseThrow(EntityNotFoundException::new);
    }



    @Override
    public void addCoverageForNewEntity(EntityId entityId) {
        List<CodeDTO> coverageCodes = codeService.getCodesByType(accountCoverage);
        AddCoverageDTO addCoverageDTO = new AddCoverageDTO();
        for (CodeDTO codes:coverageCodes) {
            addCoverageDTO.setCode(codes.getCode());
            addCoverageDTO.setId(entityId);
            addCoverage(addCoverageDTO);
        }
    }


}