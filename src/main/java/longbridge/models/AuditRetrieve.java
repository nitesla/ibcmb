package longbridge.models;

import org.apache.poi.ss.formula.functions.T;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.internal.IdentifierEqAuditExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by chiomarose on 04/07/2017.
 */
@Component
public class AuditRetrieve
{

    @Autowired
    EntityManager entityManager;

    public List<T> getRevisions(Class<?> className) {

        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(className, false, true);
        List<T> audit = query.getResultList();

        return audit;
    }




}
