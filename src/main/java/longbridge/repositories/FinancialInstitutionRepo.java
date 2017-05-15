package longbridge.repositories;

import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
@Repository
public interface FinancialInstitutionRepo extends CommonRepo<FinancialInstitution, Long>{

    Iterable<FinancialInstitution> findByInstitutionType(FinancialInstitutionType institutionType);
  FinancialInstitution findByInstitutionCode(String institutionCode);
  FinancialInstitution findByInstitutionCodeAndInstitutionType(String code, FinancialInstitutionType type);
}
