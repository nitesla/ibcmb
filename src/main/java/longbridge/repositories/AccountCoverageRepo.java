package longbridge.repositories;

import longbridge.models.AccountCoverage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AccountCoverageRepo extends CommonRepo<AccountCoverage, Long> {

    @Modifying
    @Query("update AccountCoverage c set c.enabled = :enabled where c.corporate.id = :corpId and c.code.id=:codeId")
    void enableCoverageForCorporate(@Param("corpId") Long corpId, @Param("codeId") Long codeId, @Param("enabled") Boolean enabled);

    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where c.code.id=:codeId and c.corporate.id=:corpId")
    Boolean coverageExistForCorporate(@Param("corpId") Long corpId, @Param("codeId") Long codeId);

    @Query("select distinct c from AccountCoverage c where c.code.id=:codeId and c.corporate.id=:corpId ")
    AccountCoverage getAccountCoverageByCodeAndCorporate(@Param("corpId") Long corpId,@Param("codeId") Long codeId);

    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where  c.corporate.id=:corpId and c.enabled=true")
    Boolean enabledCoverageExistForCorporate(@Param("corpId") Long corpId);

    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where  c.customerId=:customerId and c.enabled=true")
    Boolean enabledCoverageExistForCustomer(@Param("customerId") String customerId);

    @Query("select distinct c from AccountCoverage c where c.customerId =:customerId and c.enabled=true")
    List<AccountCoverage> getEnabledAccountCoverageByCustomerId(@Param("customerId") String customerId);

    @Query("select distinct c from AccountCoverage c where c.corporate.id =:corpId and c.enabled=true")
    List<AccountCoverage> getEnabledAccountCoverageByCorporate(@Param("corpId") Long corpId);

    @Modifying
    @Query("update AccountCoverage c set c.enabled = :enabled where c.retailUser.id = :retId and c.code.id=:codeId")
    void enableCoverageForRetailUser(@Param("retId") Long retId, @Param("codeId") Long codeId, @Param("enabled") Boolean enabled);

    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where c.code.id=:codeId and c.retailUser.id=:retId")
    Boolean coverageExistForRetailUser(@Param("retId") Long retId, @Param("codeId") Long codeId);

    @Query("select distinct c from AccountCoverage c where c.code.id=:codeId and c.retailUser.id=:retId")
    AccountCoverage getAccountCoverageByCodeAndRetailUser(@Param("retId") Long retId,@Param("codeId") Long codeId);


    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where  c.retailUser.id=:retId and c.enabled=true")
    Boolean enabledCoverageExistForRetailUser(@Param("retId") Long retId);

    @Query("select distinct c from AccountCoverage c where c.retailUser.id =:retId and c.enabled=true")
    List<AccountCoverage> getEnabledAccountCoverageByRetailUser(@Param("retId") Long retId);



}