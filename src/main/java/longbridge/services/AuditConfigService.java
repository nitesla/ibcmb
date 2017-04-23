package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.AuditConfig;

public interface AuditConfigService {

	AuditConfig findEntity(String entityName);

	Iterable<AuditConfig> getAllEntities();

	boolean saveAuditConfig(AuditConfig cfg);

	Page<AuditConfig> getEntities(Pageable pageDetails);

}