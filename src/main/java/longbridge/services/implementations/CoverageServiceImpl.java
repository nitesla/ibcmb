package longbridge.services.implementations;


import longbridge.dtos.AddCoverageDTO;
import longbridge.dtos.CoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CodeRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CoverageRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.CoverageAdministrationService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
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


    @Autowired
    public CoverageServiceImpl(CoverageRepo coverageRepository, ModelMapper modelMapper) {
        coverageRepo = coverageRepository;
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
        corporateids.forEach(id -> {
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
    public void addCoverageForNewCorporate(Corporate corporate) {
        List<Code> coverageCodes = codeRepo.findAllByType(coverage);
        List<Coverage> coverageList = new ArrayList<>();
        coverageCodes.forEach(codes->{
            EntityId entityId = new EntityId();
            entityId.setEid(corporate.getId());
            entityId.setType(UserType.CORPORATE);
            Coverage coverage = new Coverage();
            coverage.setCode(codes.getCode());
            coverage.setEnabled(true);
            coverage.setEntityId(entityId);
            coverage.setCodeEntity(codes);
            coverageList.add(coverage);

        });

        corporate.setCoverages(coverageList);
    }

    @Override
    public void addCoverageForNewRetail(RetailUser retailUser) {
        List<Code> coverageCodes = codeRepo.findAllByType(coverage);
        List<Coverage> coverageList = new ArrayList<>();
        coverageCodes.forEach(codes->{
            EntityId entityId = new EntityId();
            entityId.setEid(retailUser.getId());
            entityId.setType(UserType.RETAIL);
            Coverage coverage = new Coverage();
            coverage.setCode(codes.getCode());
            coverage.setEnabled(true);
            coverage.setEntityId(entityId);
            coverage.setCodeEntity(codes);
            coverageList.add(coverage);

        });

        retailUser.setCoverages(coverageList);

    }

    @Override
    public void addCoverageForNewCodes(Code code) {
        Set<Long> corporateids = corporateRepo.getAllCorporateId();
        Set<Long> retailids = retailUserRepo.getAllRetailUserId();
        List<Coverage> coverageList = new ArrayList<Coverage>();

        corporateids.forEach(id -> {
            EntityId entityId = new EntityId();
            entityId.setEid(id);
            entityId.setType(UserType.CORPORATE);
            Coverage coverage = new Coverage();
            coverage.setCode(code.getCode());
            coverage.setEnabled(true);
            coverage.setEntityId(entityId);
            coverage.setCodeEntity(code);
            coverageList.add(coverage);
        });
        retailids.forEach(id-> {
            Coverage coverage= new Coverage();
            EntityId entityId = new EntityId();
            entityId.setEid(id);
            entityId.setType(UserType.RETAIL);
            coverage.setCode(code.getCode());
            coverage.setEnabled(true);
            coverage.setEntityId(entityId);
            coverage.setCodeEntity(code);
            coverageList.add(coverage);

        });
        code.setCoverages(coverageList);

    }

    @Override
    public void newCodes(String code) {
        Set<Long> corporateids = corporateRepo.getAllCorporateId();
        Set<Long> retailids = retailUserRepo.getAllRetailUserId();
        corporateids.stream().forEach(id -> {
                AddCoverageDTO addCoverageDTO = new AddCoverageDTO();
                EntityId entityId = new EntityId();
                entityId.setEid(id);
                entityId.setType(UserType.CORPORATE);
                addCoverageDTO.setCode(code);
                addCoverageDTO.setId(entityId);
                addCoverage(addCoverageDTO);

    });
        retailids.forEach(id-> {
                AddCoverageDTO addCoverageDTO = new AddCoverageDTO();
                EntityId entityId = new EntityId();
                entityId.setEid(id);
                entityId.setType(UserType.RETAIL);
                addCoverageDTO.setCode(code);
                addCoverageDTO.setId(entityId);
                addCoverage(addCoverageDTO);

        });
}
}
