package longbridge.services;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.AuditConfigDTO;
//import longbridge.dtos.RevisionInfo;
import longbridge.dtos.AuditDTO;
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
	List<AuditConfig> getEntities();
	
	@PreAuthorize("hasAuthority('GET_AUDIT_TABLES')")
	Page<AuditConfig> findEntities(String pattern,Pageable pageDetails);

	AuditConfig getAuditEntity(Long auditId);

	List<T> revisedEntity(String entityName);
	Page<T> revisedEntityDetails(String entityName,String revisionNo,Pageable pageable);

	Page<ModifiedEntityTypeEntity> getRevisionEntities(Pageable pageable);

	Page<ModifiedEntityTypeEntity> getRevisionEntities(String pattern,Pageable pageDetails);

	Page<ModifiedEntityTypeEntity> audit(String pattern , Pageable pageDetails);

	Page<ModifiedEntityTypeEntity> getRevisedEntitiesDetails(Integer id,Pageable pageable);
	Page<ModifiedEntityTypeEntity> getRevisedDetailsForEntity(Integer id,String classname,Pageable pageable);
	Page<ModifiedEntityTypeEntity> getRevisionEntitiesByDate(Pageable pageable);
	Page<AuditDTO> revisedEntity(String entityName, Pageable pageable);
	Page<AuditDTO> searchRevisedEntity(String entityName, Pageable pageable,String search);
//	List<AuditDTO> revisedEntityForClass(String entityName);

}