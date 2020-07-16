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
    @Query("update AccountCoverage a set a.isEnabled = :isEnabled where a.code = :code")
    void  enableCoverage(@Param("code") String code,@Param("isEnabled") boolean isEnabled);

    @Query("select case when count (c)>0 then true else false end from AccountCoverage c where c.code.id=:codeId and c.corporate.id=:corpId")
    boolean coverageExist(@Param("corpId") Long corpId,@Param("codeId") Long codeId);

    @Query("select distinct c.code from AccountCoverage c where c.isEnabled = true and c.delFlag='N' ")
    List<String> getEnabledCoverage();

    @Query("select case when count (c.isEnabled)>0 then true else false end from AccountCoverage c where c.isEnabled = true and c.delFlag='N'")
    boolean enabledCoverageExist();


    AccountCoverage getAccountCoverageByCode(String code);

}