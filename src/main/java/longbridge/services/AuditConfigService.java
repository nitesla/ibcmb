package longbridge.services;

import longbridge.audit.AuditCfgDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AuditConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

//import longbridge.dtos.RevisionInfo;

public interface AuditConfigService {

    AuditConfig findEntity(String entityName);


    @PreAuthorize("hasAuthority('UPDATE_AUDIT_CONFIG')")
    boolean saveAuditConfig(AuditCfgDTO cfg) ;

    @PreAuthorize("hasAuthority('GET_AUDIT_TABLES')")
    Page<AuditConfig> getEntities(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_AUDIT_TABLES')")
    List<AuditConfig> getEntities();

    @PreAuthorize("hasAuthority('GET_AUDIT_TABLES')")
    Page<AuditConfig> findEntities(String pattern, Pageable pageDetails);

    AuditConfig getAuditEntity(Long auditId);


}