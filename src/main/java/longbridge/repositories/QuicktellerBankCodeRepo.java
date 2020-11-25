package longbridge.repositories;

import longbridge.models.QuicktellerBankCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuicktellerBankCodeRepo extends CommonRepo<QuicktellerBankCode, Long>{

    QuicktellerBankCode findByBankCode(String bankCode);
    QuicktellerBankCode findByBankCodeId(String bankCodeId);
    QuicktellerBankCode findFirstByBankNameIgnoreCase(String bankName);
    QuicktellerBankCode findByBankCodeAndCbnCode(String bankCode, String cbnCode);


    @Modifying
    @Query("update QuicktellerBankCode b set b.delFlag = 'Y' where b.id not in (:bankCode) ")
    void removeObsolete(@Param("bankCode") List<Long> collect);


//    @Query("select r from QuicktellerBankCode r where (r.institutionName like %:search% and r.sortCode is not null) or r.sortCode like %:search% order by r.institutionName desc")
//    Page<QuicktellerBankCode> findUsingPattern(@Param("search") String search, Pageable pageable);
}
