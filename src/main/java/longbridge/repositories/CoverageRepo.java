package longbridge.repositories;

import longbridge.models.Coverage;
import longbridge.models.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface CoverageRepo extends CommonRepo<Coverage, Long> {



    Boolean existsCoverageByEntityId(EntityId id);
    @Query("select entityId.eid from Coverage")
    Set<Long> getAllEntityId_Eid();
    @Query("select c.code from Coverage c where c.entityId.eid =:id")
    Set<String> getCoverageCodesForEntityId_Eid(@Param("id") Long id);
    Page<Coverage> findByEntityId(EntityId id, Pageable pageable );
    @Query("select c.entityId from Coverage c where c.entityId.eid=:id")
    EntityId getEntityId(@Param("id") Long id);
    Optional<Coverage> findFirstByEntityIdAndCode(EntityId id, String code);



}