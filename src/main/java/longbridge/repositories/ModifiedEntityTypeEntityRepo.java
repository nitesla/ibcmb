package longbridge.repositories;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chiomarose on 07/07/2017.
 */
@Repository
public interface ModifiedEntityTypeEntityRepo extends CommonRepo<ModifiedEntityTypeEntity,Long>
{



    @Query("select r from ModifiedEntityTypeEntity r inner join r.revision  where r=:rev")
    Page<ModifiedEntityTypeEntity> findEnityByRevision(@Param("rev") CustomRevisionEntity revision, Pageable pageable);

    @Query("select r from ModifiedEntityTypeEntity r inner join r.revision  where r.revision.id in (:revidList) and r.entityClassName = :class order by r.revision.timestamp desc")
    Page<ModifiedEntityTypeEntity> findEnityByRevisions(@Param("revidList") List<CustomRevisionEntity> revisions, Pageable pageable,@Param("class") String classname);

    @Query("select r from ModifiedEntityTypeEntity r  order by r.revision.timestamp desc")
    Page<ModifiedEntityTypeEntity> findAllEnityByRevision(Pageable pageable);
    @Query("select r from ModifiedEntityTypeEntity r where r.entityClassName=:className order by r.revision.timestamp desc")
    Page<ModifiedEntityTypeEntity> findAllEnityByRevisionByClass(@Param("className") String className,Pageable pageable);
    @Query("select r from ModifiedEntityTypeEntity r where r.entityClassName=:className and r.revision.ipAddress like %:search% or r.revision.lastChangedBy like %:search% or r.revision.timestamp like %:search% order by r.revision.timestamp desc")
    Page<ModifiedEntityTypeEntity> findAllEnityByRevisionBySearch(@Param("className") String className,Pageable pageable,@Param("search") String search);
//    @Query("select r from ModifiedEntityTypeEntity r where r.entityClassName=:className")
//    Page<ModifiedEntityTypeEntity> findAllEnityByRevisionByClass(@Param("className") String className);

    @Query("select r from ModifiedEntityTypeEntity r where r.entityClassName like %:entityClassName%")
    Page<ModifiedEntityTypeEntity> findUsingPatterns(Pageable pageable,@Param("entityClassName")String entityClassName);


}
