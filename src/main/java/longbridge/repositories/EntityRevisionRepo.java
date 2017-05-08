package longbridge.repositories;


import longbridge.models.audits.CustomRevisionEntity;
import longbridge.models.audits.EntityWithRevision;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 4/6/2017.
 */
@Repository
@Transactional
public class EntityRevisionRepo {


    @PersistenceContext
    //@Autowired
    private EntityManager entityManager;



<<<<<<< HEAD
    public List<EntityWithRevision<CustomRevisionEntity>> listRevisions(Long id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);


        List<Number> revisions = auditReader.getRevisions(CustomRevisionEntity.class, id);

        List<EntityWithRevision<CustomRevisionEntity>> empRevisions = new ArrayList<>();
        for (Number revision : revisions) {
            CustomRevisionEntity empRevision = auditReader.find(CustomRevisionEntity.class, id, revision);
=======
    public List<EntityWithRevision<CustomRevisionEntity>> listEmployeeRevisions(Long empCode) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);


        List<Number> revisions = auditReader.getRevisions(CustomRevisionEntity.class, empCode);

        List<EntityWithRevision<CustomRevisionEntity>> empRevisions = new ArrayList<>();
        for (Number revision : revisions) {
            CustomRevisionEntity empRevision = auditReader.find(CustomRevisionEntity.class, empCode, revision);
>>>>>>> LAST PUSH FROM FAROOQ TODAY
            Date revisionDate = auditReader.getRevisionDate(revision);

            empRevisions.add(
                    new EntityWithRevision(
                            new CustomRevisionEntity(), empRevision));
        }

        return empRevisions;
    }



}