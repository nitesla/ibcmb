package longbridge.repositories;


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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;

/**
 * Created by ayoade_farooq@yahoo.com on 4/7/2017.
 */
@Transactional
public class CommonRepoImpl<T extends AbstractEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CommonRepo<T, ID> {
    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em ;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public CommonRepoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em)
    {
        super(entityInformation, em);
        this.em = em;
        this.entityInformation = entityInformation;
    }


    @Override
    @Transactional
    public void delete(ID id)
    {
        try {
            T t = getOne(id);
            if(null !=t) {
                safeDelete(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void safeDelete(T entity){
        entity.setDelFlag("Y");
        entity.setDeletedOn(new Date());
        super.save(entity);
    }
    @Override
    public void deleteById(ID id) {
        delete(id);
    }

    @Override
    @Transactional
    public void delete(T entity) {

        entity.setDelFlag("Y");
        entity.setDeletedOn(new Date());
        super.save(entity);
    }

    @Transactional
    public void delete(Iterable<? extends T> entities) {

        Assert.notNull(entities, "The given Iterable of entities can not be null!");
        Iterator<? extends T> var2 = entities.iterator();

        while (var2.hasNext()) {
            T entity = var2.next();
            entity.setDelFlag("Y");
            entity.setDeletedOn(new Date());
            System.out.println("the entity to be deleted flag "+entity.getDelFlag() +" id "+entity.getId());

            super.save(entity);
        }
    }
    @Override
    @Transactional
    public T findOneById(ID id) {
        logger.info("the id to fetch {}",id);
        Assert.notNull(id, "The given id is null!");
        try {
            return super.getOne(id);
        } catch (EntityNotFoundException e) {
            System.out.println("the exception caught "+e.getMessage());
            return null;
        }catch (NoSuchElementException e) {
            return null;
        }catch (Exception e) {
            System.out.println("the exception caught "+e.getMessage());
            return null;
        }

    }




    @Override
    public void deleteInBatch(Iterable<T> entities) {
        Assert.notNull(entities, "The given Iterable of entities can not be null!");
        Iterator<? extends T> var2 = entities.iterator();

        while (var2.hasNext()) {
            T entity = var2.next();
            safeDelete(entity);
        }
    }
    @Override
    @Transactional
    public void deleteAll()
    {
        Iterator<T> var1 = this.findAll().iterator();

        while (var1.hasNext()) {
            T entity = var1.next();
            safeDelete(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
//        super.deleteAllInBatch();
    }

    @Override
    public Page<T> findUsingPattern(String pattern, Pageable details)
    {
        String lpattern = pattern.toLowerCase();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityInformation.getJavaType());
        Root<T> c = q.from(entityInformation.getJavaType());
        Predicate[] predicates = null;
        try {
            predicates = new Predicate[getFields().size()];
            int cnt = 0;
            for (String field : getFields()) {
                Predicate predicate = cb.like(cb.lower(c.get(field)), "%" + lpattern + "%");
                predicates[cnt] = predicate;
                cnt++;
            }
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            return new PageImpl<>(new ArrayList<>());
        }

        CriteriaQuery<T> baseQuery = null;
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        CriteriaQuery<Long> countQuery = null;
        System.out.println("the predicates "+predicates);
        if(predicates.length > 0)
        {
            Predicate or = cb.or (predicates);
            baseQuery = q.select(c).where(or);
            countQuery = qc.select(cb.count(qc.from(entityInformation.getJavaType()))).where(or);
        }
        else
        {
            baseQuery = q.select(c);
            countQuery = qc.select(cb.count(qc.from(entityInformation.getJavaType())));
        }

        TypedQuery<T> query = em.createQuery(baseQuery);
        Long count = em.createQuery(countQuery).getSingleResult();
        query.setFirstResult(Math.toIntExact(details.getOffset()));
        query.setMaxResults(details.getPageSize());
        List<T> list = query.getResultList();
        return new PageImpl<T>(list, details, count);
    }



    private List<String> getFields() throws InstantiationException, IllegalAccessException
    {
        Class<T> type = entityInformation.getJavaType();
        AbstractEntity en = type.newInstance();
        return en.getDefaultSearchFields();

    }
//   private List<ModifiedEntityTypeEntity> searchModidfiedEntity(){
//       CriteriaBuilder cb = em.getCriteriaBuilder();
//
//   }

}