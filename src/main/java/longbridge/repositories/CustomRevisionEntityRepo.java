package longbridge.repositories;

import longbridge.config.audits.CustomRevisionEntity;
//import longbridge.dtos.RevisionInfo;
import longbridge.models.UserType;
import longbridge.models.Verification;
import org.apache.commons.digester.annotations.rules.BeanPropertySetter;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chiomarose on 05/07/2017.
 */
@Repository
@Transactional
public interface CustomRevisionEntityRepo extends CommonRepo<CustomRevisionEntity,Long>
{
    @Query( "select c from CustomRevisionEntity c where  c.id in :revisionList")
    Page<CustomRevisionEntity> findCustomRevisionId(@Param("revisionList") List<T> revision, Pageable pageable);

    @Query("select c from CustomRevisionEntity c where c.id=:rev")
    CustomRevisionEntity findUniqueCustomEnity(@Param("rev") Integer revisionNumber);


//
// ;



}
