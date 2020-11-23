package longbridge.repositories;

import longbridge.models.QuicktellerBankCode;
import org.springframework.stereotype.Repository;


@Repository
public interface QuicktellerBankCodeRepo extends CommonRepo<QuicktellerBankCode, Long>{

    QuicktellerBankCode findByBankCode(String bankCode);
    QuicktellerBankCode findByBankCodeId(String bankCodeId);
    QuicktellerBankCode findFirstByBankNameIgnoreCase(String bankName);
    QuicktellerBankCode findByBankCodeAndCbnCode(String bankCode, String cbnCode);

//    @Query("select r from QuicktellerBankCode r where (r.institutionName like %:search% and r.sortCode is not null) or r.sortCode like %:search% order by r.institutionName desc")
//    Page<QuicktellerBankCode> findUsingPattern(@Param("search") String search, Pageable pageable);
}
