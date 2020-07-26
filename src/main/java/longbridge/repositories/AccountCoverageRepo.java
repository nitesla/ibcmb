package longbridge.repositories;

import longbridge.models.AccountCoverage;
import longbridge.models.EntityId;
import longbridge.models.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccountCoverageRepo extends CommonRepo<AccountCoverage, Long> {



    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where  c.customerId=:customerId and c.enabled=true")
    Boolean enabledCoverageExistForCustomer(@Param("customerId") String customerId);

    @Query("select c from AccountCoverage c where c.customerId =:customerId and c.enabled=true")
    Page<AccountCoverage> getEnabledAccountCoverageByCustomerId(@Param("customerId") String customerId, Pageable pageable);

    Page<AccountCoverage> findByEntityId(EntityId id, Pageable pageable );

    Optional<AccountCoverage> findFirstByEntityIdAndCode(EntityId id, String code);


}