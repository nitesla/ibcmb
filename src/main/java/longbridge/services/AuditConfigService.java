package longbridge.services;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.dtos.AuditConfigDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Code;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.AuditConfig;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

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

	AuditConfig getAuditEntity(Long auditId);

	List<T> revisedEntity(String entityName);
	Page<CustomRevisionEntity> revisedEntityDetails(String entityName,Pageable pageable);

}