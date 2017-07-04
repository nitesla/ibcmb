package longbridge.services.implementations;

import longbridge.exception.InternetBankingException;
import longbridge.models.AuditConfig;
import longbridge.repositories.AuditConfigRepo;
import longbridge.services.AuditConfigService;
import longbridge.utils.Verifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
@Service
public class AuditConfigImpl implements AuditConfigService {

    @Autowired
    private AuditConfigRepo configRepo;
    @Autowired
	EntityManager entityManager;

	public AuditConfig findEntity(String s) {
		return configRepo.findFirstByEntityName(s);
	}

	@Override
	public Iterable<AuditConfig> getAllEntities() {
		return configRepo.findAll();
	}

	@Override
	@Verifiable(operation="AUDIT_CONFIG",description="Configuring Audit")
    public boolean saveAuditConfig(AuditConfig cfg) throws InternetBankingException {
		configRepo.save(cfg);
		return true;
	}

	@Override
	public Page<AuditConfig> getEntities(Pageable pageDetails) {
		return configRepo.findAll(pageDetails);
	}

	@Override
	public Page<AuditConfig> findEntities(String pattern, Pageable pageDetails) {
		return configRepo.findUsingPattern(pattern,pageDetails);
	}
}
