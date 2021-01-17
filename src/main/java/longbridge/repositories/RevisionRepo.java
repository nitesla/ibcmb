package longbridge.repositories;

import longbridge.config.audits.Revision;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//import longbridge.dtos.RevisionInfo;

/**
 * Created by chiomarose on 05/07/2017.
 */
@Repository
@Transactional
public interface RevisionRepo extends CommonRepo<Revision, Long> {
    @Query("select c from Revision c where  c.id in :revisionList")
    Page<Revision> findCustomRevisionId(@Param("revisionList") List<T> revision, Pageable pageable);

    @Query("select c from Revision c where c.id=:rev")
    Revision findUniqueCustomEnity(@Param("rev") Integer revisionNumber);

    @Query("select c.id from Revision c where c.id in :revisionList")
    List<Revision> findCustomEnityDetails(@Param("revisionList") List<Integer> revisionNumber);


//
// ;


}
