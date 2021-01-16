package longbridge.audit;

import longbridge.config.AnnotationInitializer;
import longbridge.config.audits.ModifiedType;
import longbridge.repositories.AuditRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
class AdmAuditServiceImpl implements AdmAuditService {

    @Autowired
    AuditRepo repo;

    @Autowired
    private AnnotationInitializer annotationInitializer;

    @Override
    public Page<ModifiedType> getRevisions(Pageable pageDetails, RevisionDTO searchCriteria) {

        return repo.findByRevision(pageDetails, searchCriteria);
    }

    @Override
    public Object getRevision(Long entityId) {
        return repo.findRevision(entityId);
    }


    @Override
    public List<AuditBlob> getRevision(AuditId entityId) {
        return repo.getRevisionInfo(entityId.getId());
    }

    @Override
    public Set<String> getAllOperations() {
        return annotationInitializer.getAllTraceOperations();
    }


}
