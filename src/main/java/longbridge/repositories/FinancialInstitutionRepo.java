package longbridge.repositories;

import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
@Repository
public interface FinancialInstitutionRepo extends CommonRepo<FinancialInstitution, Long>{

    Iterable<FinancialInstitution> findByInstitutionTypeOrderByInstitutionNameAsc(FinancialInstitutionType institutionType);

    FinancialInstitution findByInstitutionCode(String institutionCode);
    FinancialInstitution findBySortCode(String bankCode);
  FinancialInstitution findFirstByInstitutionCodeIgnoreCase(String institutionName);
  List<FinancialInstitution> findByInstitutionCodeIgnoreCaseNot(String institutionCode);
    Page<FinancialInstitution> findBySortCodeIsNotNull(Pageable pageable);

    List<FinancialInstitution> findByInstitutionTypeAndInstitutionCodeIgnoreCaseNot(FinancialInstitutionType institutionType ,String institutionCode);
  FinancialInstitution findByInstitutionCodeAndInstitutionType(String code, FinancialInstitutionType type);

//    @Query("select r from FinancialInstitution r where (r.institutionName like %:search% and r.sortCode is not null) or r.sortCode like %:search% order by r.institutionName desc")
//    Page<FinancialInstitution> findUsingPattern(@Param("search") String search, Pageable pageable);
}
