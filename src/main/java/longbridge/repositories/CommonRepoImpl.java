package longbridge.repositories;


import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.exception.VerificationException;
import longbridge.models.AbstractEntity;
import longbridge.models.SerializableEntity;
import longbridge.models.User;
import longbridge.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by ayoade_farooq@yahoo.com on 4/7/2017.
 */
@Transactional
public class CommonRepoImpl<T extends AbstractEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CommonRepo<T, ID> {
    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private final Class<T> domainClass;

    @Autowired
    private VerificationService verificationService;



    public CommonRepoImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
    }


//    <S extends T> S save(S var1);
//
//    <S extends T> Iterable<S> save(Iterable<S> var1);


    public <T extends SerializableEntity<T>> String makerCheckerSave(T originalEntity, T entity,User createdBy) throws JsonProcessingException, VerificationException {

        AbstractEntity originalEntity1 = (AbstractEntity) (originalEntity);

        if (originalEntity1.getId() == null) {
                String message = verificationService.addNewVerificationRequest(entity,createdBy);
                return message;

        } else {

                String message = verificationService.addModifyVerificationRequest(originalEntity, entity);
                return message;

        }


    }




//    public <T extends SerializableEntity<T>> String makerCheckerUpdate(T entity) throws JsonProcessingException, VerificationException {
//        String message = verificationService.addNewVerificationRequest(entity);
//        return message;
//    }


    @Override
    @Transactional
    public void delete(ID id) {
        T t = findOne(id);
        t.setDelFlag("Y");
        t.setDeletedOn(new Date());

        super.save(t);
    }

    @Override
    @Transactional
    public void delete(T entity) {

        entity.setDelFlag("Y");
        entity.setDeletedOn(new Date());
        super.save(entity);
    }

    @Override
    @Transactional
    public void delete(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given Iterable of entities can  not be null!");
        Iterator<? extends T> var2 = entities.iterator();

        while (var2.hasNext()) {
            T entity = var2.next();
            entity.setDelFlag("Y");
            entity.setDeletedOn(new Date());
            super.save(entity);
        }

    }


    @Override
    @Transactional
    public void deleteAll() {
        Iterator<T> var1 = this.findAll().iterator();

        while (var1.hasNext()) {
            T entity = var1.next();
            entity.setDelFlag("Y");
            entity.setDeletedOn(new Date());
            super.save(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
//        super.deleteAllInBatch();
    }


}
