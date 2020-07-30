package longbridge.services.implementations;


import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CodeRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CoverageRepo;
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
public class CoverageServiceImpl implements CoverageAdministrationService {


    private final String coverage = "ACCOUNT_COVERAGE";
    @Autowired
    private CoverageRepo coverageRepo;
    @Autowired
    private CodeRepo codeRepo;
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private CorporateRepo corporateRepo;
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
    public CoverageServiceImpl(CoverageRepo coverageRepository, ModelMapper modelMapper) {
        coverageRepo = coverageRepository;
        this.modelMapper = modelMapper;
    }


    private CoverageDTO createDto(Coverage coverage) {
        CoverageDTO dto = new CoverageDTO();
        dto.setCode(coverage.getCode());
        dto.setEnabled(coverage.isEnabled());
        return dto;
    }


    @Override
    public Page<CoverageDTO> getAllCoverage(EntityId id, Pageable pageDetails) {
        Page<Coverage> coverageList = coverageRepo.findByEntityId(id, pageDetails);
        return coverageList.map(this::createDto);
    }

    @Override
    public void updateCoverage(UpdateCoverageDTO updateCoverageDTO) throws InternetBankingException {
        Optional<Coverage> firstByEntityIdAndCode = coverageRepo.findFirstByEntityIdAndCode(updateCoverageDTO.getId(), updateCoverageDTO.getCode());
        Coverage coverage = firstByEntityIdAndCode.orElse(createNew(updateCoverageDTO.getId()));
        coverage.setEnabled(updateCoverageDTO.getEnabled());
        coverage.setCode(updateCoverageDTO.getCode());
        coverage.setEntityId(updateCoverageDTO.getId());
        coverageRepo.save(coverage);
    }

    private Coverage createNew(EntityId id) {
        Coverage coverage = new Coverage();
        coverage.setEntityId(id);
        return coverage;
    }

    @Override
    public void addCoverage(AddCoverageDTO addCoverageDTO) {
        Coverage coverage = createNew(addCoverageDTO.getId());
        coverage.setEnabled(true);
        coverage.setCode(addCoverageDTO.getCode());
        coverageRepo.save(coverage);
    }

    @Override
    public Coverage getCoverage(EntityId id, String code) {

        return coverageRepo.findFirstByEntityIdAndCode(id, code).orElseThrow(EntityNotFoundException::new);
    }



    @Override
    public void addCoverageForNewEntity() {
        Set<String> coverageCodes = codeRepo.getCodeByType(coverage);
        Set<Long> entityId_eids= coverageRepo.getAllEntityId_Eid();
        Set<Long> corporateids = corporateRepo.getAllCorporateId();
        Set<Long> retailids = retailUserRepo.getAllRetailUserId();
        corporateids.removeAll(entityId_eids);
        corporateids.stream().forEach(id -> {
               for (String codes:coverageCodes) {
                AddCoverageDTO addCoverageDTO = new AddCoverageDTO();
                EntityId entityId = new EntityId();
                entityId.setEid(id);
                entityId.setType(UserType.CORPORATE);
                addCoverageDTO.setCode(codes);
                addCoverageDTO.setId(entityId);
                addCoverage(addCoverageDTO);
                }
        });

        retailids.removeAll(entityId_eids);
        retailids.forEach(id-> {
            for (String codes:coverageCodes) {
                AddCoverageDTO addCoverageDTO = new AddCoverageDTO();
                EntityId entityId = new EntityId();
                entityId.setEid(id);
                entityId.setType(UserType.RETAIL);
                addCoverageDTO.setCode(codes);
                addCoverageDTO.setId(entityId);
                addCoverage(addCoverageDTO);
                           }
        });

    }

    @Override
    public void addCoverageForNewCodes() {
        Set<String> coverageCodes = codeRepo.getCodeByType(coverage);
        Set<Long> entityId_eids= coverageRepo.getAllEntityId_Eid();
        entityId_eids.forEach(id->{
           coverageCodes.removeAll(coverageRepo.getCoverageCodesForEntityId_Eid(id));
            for (String code:coverageCodes) {
                AddCoverageDTO addCoverageDTO = new AddCoverageDTO();
                EntityId entityId = coverageRepo.getEntityId(id);
                addCoverageDTO.setCode(code);
                addCoverageDTO.setId(entityId);
                addCoverage(addCoverageDTO);

            }
        });

    }
}