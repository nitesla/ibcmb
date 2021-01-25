package longbridge.audit;

import longbridge.config.audits.ModifiedType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AdmAuditService {

//    public Page<ModifiedEntityTypeEntity> getRevisions(Pageable pageDetails);

    Page<ModifiedType> getRevisions(Pageable pageDetails, RevisionDTO SearchCriteria);

    Object getRevision(Long entityId);

    List<AuditBlob> getRevision(AuditId id);

    Set<String> getAllOperations();
}
