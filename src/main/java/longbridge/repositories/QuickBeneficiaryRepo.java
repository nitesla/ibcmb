package longbridge.repositories;

import longbridge.models.QuickBeneficiary;
import longbridge.models.RetailUser;

import java.util.List;


public interface QuickBeneficiaryRepo extends CommonRepo<QuickBeneficiary,Long>{
    List<QuickBeneficiary> findByUserAndBeneficiaryBankIsNotNull(RetailUser user);
    QuickBeneficiary findByUser_IdAndAccountNumber(Long id, String acctNo);

}
