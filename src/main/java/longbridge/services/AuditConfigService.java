package longbridge.services;

import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.AuditConfig;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AuditConfigService
{

	AuditConfig findEntity(String entityName);

	Iterable<AuditConfig> getAllEntities();

	@PreAuthorize("hasAuthority('UPDATE_AUDIT_CONFIG')")
	boolean saveAuditConfig(AuditConfig cfg) throws InternetBankingException;

	@PreAuthorize("hasAuthority('GET_AUDIT_TABLES')")
	Page<AuditConfig> getEntities(Pageable pageDetails);
	
	@PreAuthorize("hasAuthority('GET_AUDIT_TABLES')")
	Page<AuditConfig> findEntities(String pattern,Pageable pageDetails);

}