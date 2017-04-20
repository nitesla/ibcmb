package longbridge.services;

import longbridge.models.AuditConfig;

public interface AuditConfigService {

	AuditConfig findEntity(String entityName);
	
	Iterable<AuditConfig> getAllEntities();
	
	boolean saveAuditConfig(AuditConfig cfg);
	

}