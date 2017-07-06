package longbridge.repositories;

import longbridge.models.AuditConfig;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.internal.IdentifierEqAuditExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Repository
@Transactional
public interface AuditConfigRepo extends CommonRepo<AuditConfig, Long>
{


    AuditConfig findFirstByEntityName(String name);
    boolean existsByEntityName(String name);
    boolean existsByEntityNameAndEnabled(String name, String enabled);





}
