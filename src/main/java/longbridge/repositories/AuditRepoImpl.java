package longbridge.repositories;

import longbridge.audit.AuditBlob;
import longbridge.audit.RevisionDTO;
import longbridge.config.audits.ModifiedType;
import longbridge.exception.InternetBankingException;
import longbridge.services.AuditConfigService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.configuration.internal.AuditEntitiesConfiguration;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.Queryable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AuditRepoImpl extends SimpleJpaRepository<ModifiedType, Long> implements AuditRepo {

    static long MILLISECONDS_IN_A_DAY = 86400000L;
    private EntityManager em;
    private AuditConfigService auditCfgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    public AuditRepoImpl(EntityManager em, AuditConfigService adCfg) {
        super(ModifiedType.class, em);
        this.em = em;
        this.auditCfgService = adCfg;

    }


    @Override
    public Page<ModifiedType> findByRevision(Pageable details, RevisionDTO crit) {

        StringBuilder queryString = new StringBuilder("select mon from Revision as crv " +
                " inner join crv.modifiedTypes  mon");

        StringBuilder countQueryString = new StringBuilder("select count(mon) from Revision as crv " +
                " inner join crv.modifiedTypes  mon");

        ArrayList<String> criteria = new ArrayList<>();
        List<String> traceCriteria = new ArrayList<>();
        boolean addIpAddress = false;
        boolean addModifier = false;
        boolean addApprover = false;
        boolean addOps = false;
        if (crit != null) {
            if (crit.getFrom() != null) {
                criteria.add("crv.timestamp >= " + crit.getFrom().getTime());

            }
            if (crit.getTo() != null) {
                criteria.add("crv.timestamp <= " + crit.getTo().getTime());
            }
            if (StringUtils.isNotBlank(crit.getIpAddress())) {
                //criteria.add("crv.ipAddress = '" + crit.getIpAddress() + "'");
                criteria.add(" crv.ipAddress = :ipAddress ");
                addIpAddress = true;
            }

            if (StringUtils.isNotBlank(crit.getModifier())) {
                //criteria.add("crv.lastChangedBy like '%" + crit.getModifier() + "%'");
                criteria.add(" crv.lastChangedBy like :modifiedBy ");
                addModifier = true;
            }

            if (StringUtils.isNotBlank(crit.getApprover())) {
                criteria.add(" crv.approvedBy like :approver ");
                addApprover = true;
            }

            if (crit.getOperations() != null && !crit.getOperations().isEmpty()) {
                int traceIdx = 0;
                for (String op : crit.getOperations()) {
                    traceCriteria.add(String.format(" :ops_%d in (crv.trace)  ", traceIdx++));
                }
                addOps = true;
            }
            if (crit.getTableIndex() != null) {
                criteria.add("mon.entityClassName = '" + auditCfgService.getAuditEntity(crit.getTableIndex()).getFullName() + "'");
            }

        }

        boolean whereSet = false;
        StringBuilder where = new StringBuilder();
        Iterator<String> iterator = criteria.iterator();
        if (iterator.hasNext()) {
            whereSet = true;
            where.append(" where " + iterator.next());
            while (iterator.hasNext()) {
                where.append(" and " + iterator.next());
            }
        }

        iterator = traceCriteria.iterator();
        if (iterator.hasNext()) {
            if (whereSet)
                where.append(" and (" + iterator.next());
            else
                where.append(" where " + iterator.next());

            while (iterator.hasNext()) {
                where.append(" or " + iterator.next());
            }
            if (whereSet)
                where.append(")");
        }


        queryString.append(where);
        queryString.append(" order by crv.timestamp desc ");
        countQueryString.append(where);
        Query query = em.createQuery(queryString.toString());
        Query countQuery = em.createQuery(countQueryString.toString());
        if (addIpAddress) {
            query.setParameter("ipAddress", crit.getIpAddress());
            countQuery.setParameter("ipAddress", crit.getIpAddress());
        }
        if (addModifier) {
            query.setParameter("modifiedBy", "%" + crit.getModifier() + "%");
            countQuery.setParameter("modifiedBy", "%" + crit.getModifier() + "%");
        }
        if (addApprover) {
            query.setParameter("approver", "%" + crit.getApprover() + "%");
            countQuery.setParameter("approver", "%" + crit.getApprover() + "%");
        }
        if (addOps) {
            int idx = 0;
            List<String> operations = crit.getOperations();

            for (String op : traceCriteria) {
                //" :ops_%d in (crv.trace)  "
                String param = operations.get(idx);
                query.setParameter("ops_" + idx, param);
                countQuery.setParameter("ops_" + idx, param);
                idx++;
            }
        }

        if (details.isPaged()) {
            query.setFirstResult((int) details.getOffset());
            query.setMaxResults(details.getPageSize());
        }
        List<ModifiedType> resultList = query.getResultList();
        Long cnt = (Long) countQuery.getSingleResult();


        return new PageImpl<>(resultList, details, cnt);

    }


    @Override
    public Object findRevision(Long entityId) {
        ModifiedType entity = em.find(ModifiedType.class, entityId);
        if (entity == null) throw new InternetBankingException("Invalid Entity");
        String type = entity.getEntity();

        try {
            Class<?> aClass = Class.forName(type);
            AuditReader reader = AuditReaderFactory.get(em);
            AuditQuery auditQuery = reader.createQuery().forEntitiesAtRevision(aClass, entity.getRevision().getId());
            Object o = auditQuery.getSingleResult();
            return o;
        } catch (ClassNotFoundException e) {
            throw new InternetBankingException("Invalid Entity");
        }

    }

    public List<AuditBlob> getRevisionInfo(Long entityId) {
        ModifiedType entity = em.find(ModifiedType.class, entityId);
        if (entity == null) throw new InternetBankingException("Invalid Entity");
        String type = entity.getEntity();
        String table = getTable(type);
        String entityTable2 = StringUtils.substringBefore(table, "_aud");
        int rev = entity.getRevision().getId();


        JdbcTemplate template = new JdbcTemplate(datasource);

        String sql = String.format("select * from %s tab where tab.rev = %d", getTable(type), rev);
        Map<String, Object> revisionMap = template.queryForMap(sql);
        sanitize(revisionMap);

        //check for next revision of entity

        sql = String.format("select max(tab.rev) from %s tab where tab.id=%d and tab.rev < %d ", table, (Long) revisionMap.get("id"), rev);
        Long maxRev = template.queryForObject(sql, Long.class);
        Map<String, Object> newMap = null;
        if (maxRev != null) {
            // get it
            sql = String.format("select * from %s tab where tab.id=%d and tab.rev = %d order by tab.rev desc", table, (Long) revisionMap.get("id"), maxRev);
            newMap = template.queryForMap(sql);
            sanitize(newMap);
        } else {
            //get object if exists
            newMap = new HashMap();
        }

        List<AuditBlob> data = compact(revisionMap, newMap);
        return data;
    }

    private List<AuditBlob> compact(Map<String, Object> revisionMap, Map<String, Object> newMap) {
        List<AuditBlob> baggage = revisionMap.keySet().stream().filter(k -> !k.endsWith("_mod")).map(k -> {
            return AuditBlob.AuditBlobBuilder.anAuditBlob().withName(k).withNow(revisionMap.get(k)).withBefore(newMap.get(k)).build();
        }).collect(Collectors.toList());
        return baggage;
    }


    private void sanitize(Map<String, Object> map) {
        if (map.containsKey("password")) {
            map.replace("password", "**************");
        }
    }


    private String getTable(String entity) {
        SessionImplementor si = em.unwrap(SessionImplementor.class);
        EnversService enversService = si.getSessionFactory().getServiceRegistry().getService(EnversService.class);

        // Next we get the audit entity configuration and get the audited entity name
        AuditEntitiesConfiguration aec = enversService.getAuditEntitiesConfiguration();
        String auditEntityName = aec.getAuditEntityName(entity);

        // Now we need to get the entity information from ORM
        try {
            Class<?> aClass = Class.forName(entity);
            MetamodelImplementor metamodel = (MetamodelImplementor) si.getMetamodel();
//            ClassMetadata classMetadata = (ClassMetadata) metamodel.entityPersister(aClass.getName());
            return ((Queryable) metamodel.entityPersister(auditEntityName)).getTableName();
        } catch (ClassNotFoundException e) {
            throw new InternetBankingException("Entity not found");
        }
    }


}
