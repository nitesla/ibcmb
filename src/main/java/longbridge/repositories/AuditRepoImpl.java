package longbridge.repositories;

import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AuditSearchDTO;
import longbridge.models.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static longbridge.utils.StringUtil.searchModifiedEntityTypeEntity;

/**
 * Created by Longbridge on 10/28/2017.
 */
@Transactional
@Service
public class AuditRepoImpl {
    @Autowired
    EntityManager em;
Logger logger = LoggerFactory.getLogger(AuditRepoImpl.class);
//        @Override
//        public Page<T> findUsingPattern(String pattern, Pageable details)
//        {
//            String lpattern = pattern.toLowerCase();
//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<T> q = cb.createQuery(entityInformation.getJavaType());
//            Root<T> c = q.from(entityInformation.getJavaType());
//            Predicate[] predicates = null;
//            try {
//                predicates = new Predicate[getFields().size()];
//                int cnt = 0;
//                for (String field : getFields()) {
//                    Predicate predicate = cb.like(cb.lower(c.get(field)), "%" + lpattern + "%");
//                    predicates[cnt] = predicate;
//                    cnt++;
//                }
//            }
//            catch (InstantiationException | IllegalAccessException e)
//            {
//                return new PageImpl<>(new ArrayList<>());
//            }
//
//            CriteriaQuery<T> baseQuery = null;
//            CriteriaQuery<Long> qc = cb.createQuery(Long.class);
//            CriteriaQuery<Long> countQuery = null;
//            if(predicates.length > 0)
//            {
//                Predicate or = cb.or (predicates);
//                baseQuery = q.select(c).where(or);
//                countQuery = qc.select(cb.count(qc.from(entityInformation.getJavaType()))).where(or);
//            }
//            else
//            {
//                baseQuery = q.select(c);
//                countQuery = qc.select(cb.count(qc.from(entityInformation.getJavaType())));
//            }
//
//            TypedQuery<T> query = em.createQuery(baseQuery);
//            Long count = em.createQuery(countQuery).getSingleResult();
//            query.setFirstResult(details.getOffset());
//            query.setMaxResults(details.getPageSize());
//            List<T> list = query.getResultList();
//            return new PageImpl<T>(list, details, count);
//        }



    public Page<ModifiedEntityTypeEntity> findModifiedEntityBySearch(Pageable pageable, AuditSearchDTO auditSearchDTO){
        String search  = searchModifiedEntityTypeEntity(auditSearchDTO,true);
        String sql = "select m from  " +
                "ModifiedEntityTypeEntity m"+search;
        try {
            TypedQuery<ModifiedEntityTypeEntity> query = em.createQuery(sql,ModifiedEntityTypeEntity.class);
            Long count = RevisedEntitiesUtil.fetchModifiedEntity(auditSearchDTO);
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<ModifiedEntityTypeEntity> entityTypeEntities = query.getResultList();
            logger.info("the size of searched query is {}",sql);
            logger.info("the size of searched record is {}",count);
            if(!entityTypeEntities.isEmpty()){
                System.out.println("the result is "+entityTypeEntities.get(0));
            }

            return new PageImpl<ModifiedEntityTypeEntity>(entityTypeEntities, pageable, count);
        } catch (Exception e) {
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>());
        }
    }



}
