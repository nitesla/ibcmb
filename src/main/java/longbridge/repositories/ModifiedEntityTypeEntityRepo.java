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

    @Query("select r from ModifiedEntityTypeEntity r inner join r.revision  where r.revision.id in :revidList")
    Page<ModifiedEntityTypeEntity> findEnityByRevisions(@Param("revidList") List<CustomRevisionEntity> revision, Pageable pageable);



}
