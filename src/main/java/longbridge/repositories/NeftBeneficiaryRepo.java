package longbridge.repositories;

import longbridge.models.CorporateUser;
import longbridge.models.NeftBeneficiary;
import longbridge.models.RetailUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeftBeneficiaryRepo extends CommonRepo<NeftBeneficiary, Long>{
    Iterable<NeftBeneficiary> findByUser(RetailUser user);
    Iterable<NeftBeneficiary> findByUser(CorporateUser user);
    NeftBeneficiary findByUser_IdAndBeneficiaryAccountNumber(Long id, String acctNo);
    boolean existsByUser_IdAndBeneficiaryAccountNumber(Long id, String acctNo);
//    List<NeftBeneficiary> findByUser_IdAndBeneficiaryBankIgnoreCase(Long id, String bankCode);
//    List<NeftBeneficiary> findByUser_IdAndBeneficiarySortCode(Long id,String bankCode);
    List<NeftBeneficiary> findByUserAndBeneficiarySortCode(RetailUser user,String bankCode);
    List<NeftBeneficiary> findByUserAndBeneficiarySortCodeIsNotNull(RetailUser user);
}
