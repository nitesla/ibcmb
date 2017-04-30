package longbridge.repositories;


import longbridge.models.AbstractEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by ayoade_farooq@yahoo.com on 4/7/2017.
 */
@Transactional
public class CommonRepoImpl<T extends AbstractEntity, ID extends Serializable> extends SimpleJpaRepository<T , ID>
        implements CommonRepo<T , ID> {
    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private final Class<T> domainClass;


    public CommonRepoImpl(Class<T > domainClass , EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
    }




    @Override
    @Transactional
    public void delete(ID id) {
        T t = findOne(id);
        t.setDelFlag("Y");

        super.save(t);
    }

    @Override
    @Transactional
    public void delete(T entity) {

        entity.setDelFlag("Y");
        super.save(entity);
    }

    @Override
    @Transactional
    public void delete(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given Iterable of entities can  not be null!");
        Iterator<? extends T> var2 = entities.iterator();

        while(var2.hasNext()) {
            T entity = var2.next();
            entity.setDelFlag("Y");
            super.save(entity);
        }

  }


    @Override
    @Transactional
    public void deleteAll() {
        Iterator<T> var1 = this.findAll().iterator();

        while(var1.hasNext()) {
            T entity = var1.next();
            entity.setDelFlag("Y");
            super.save(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
//        super.deleteAllInBatch();
    }



}
