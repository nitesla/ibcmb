package longbridge.repositories;

import longbridge.models.AuditConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Repository
public interface AuditConfigRepo extends JpaRepository<AuditConfig, Long>
{


    AuditConfig findFirstByEntityName(String name);
    boolean existsByEntityName(String name);
    boolean existsByEntityNameAndEnabled(String name, String enabled);

}