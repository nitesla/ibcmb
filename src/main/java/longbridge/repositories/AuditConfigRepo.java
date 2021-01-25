package longbridge.repositories;

import longbridge.models.AuditConfig;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Repository
@Transactional
public interface AuditConfigRepo extends CommonRepo<AuditConfig, Long> {
    AuditConfig findFirstByEntityName(String name);

    boolean existsByEntityName(String name);

    boolean existsByFullName(String fullname);

    List<AuditConfig> findByOrderByEntityNameAsc();


}
