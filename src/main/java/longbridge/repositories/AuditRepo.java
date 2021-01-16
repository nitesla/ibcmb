package longbridge.repositories;

import longbridge.audit.AuditBlob;
import longbridge.audit.RevisionDTO;
import longbridge.config.audits.ModifiedType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface AuditRepo {

    Page<ModifiedType> findByRevision(Pageable details, RevisionDTO crit);

    Object findRevision(Long entityId);

    List<AuditBlob> getRevisionInfo(Long entityId);

}
