package longbridge.services.implementations;

import longbridge.audit.AuditCfgDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AuditConfig;
import longbridge.repositories.AuditConfigRepo;
import longbridge.repositories.ModifiedTypeRepo;
import longbridge.repositories.RevisionRepo;
import longbridge.services.AuditConfigService;
import longbridge.utils.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class AuditConfigImpl implements AuditConfigService {


    @Autowired
    EntityManager entityManager;
    @Autowired
    RevisionRepo revisionRepo;
    @Autowired
    ModifiedTypeRepo modifiedTypeRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AuditConfigRepo configRepo;

    @Override
    public AuditConfig findEntity(String s) {
        return configRepo.findFirstByEntityName(s);
    }


    @Override
    @CacheEvict(value = "audit-config", key = "#result.fullName")
    @Verifiable(operation = "AUDIT_CONFIG", description = "Configuring audit")
    public boolean saveAuditConfig(AuditCfgDTO cfg) {
        AuditConfig auditConfig = configRepo.findById(cfg.getId()).orElseThrow(InternetBankingException::new);
        auditConfig.setEnabled(cfg.getEnabled());
        auditConfig.setVersion(cfg.getVersion());
        configRepo.save(auditConfig);
        return true;
    }

    @Override
    public Page<AuditConfig> getEntities(Pageable pageDetails) {
        return configRepo.findAll(pageDetails);
    }

    @Override
    public List<AuditConfig> getEntities() {
        return configRepo.findByOrderByEntityNameAsc();
    }


    @Override
    public Page<AuditConfig> findEntities(String pattern, Pageable pageDetails) {
        return configRepo.findUsingPattern(pattern, pageDetails);

    }


    @Override
    public AuditConfig getAuditEntity(Long id) {
        return this.configRepo.findById(id).orElseThrow(() -> new InternetBankingException("Unknown entity"));
    }


    private boolean isValidEntity(String entity) {
        return configRepo.existsByEntityName(entity);
    }


}
