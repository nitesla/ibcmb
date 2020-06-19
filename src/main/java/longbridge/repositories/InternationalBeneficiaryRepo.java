package longbridge.repositories;

import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface InternationalBeneficiaryRepo extends CommonRepo<InternationalBeneficiary, Long>{

    Iterable<InternationalBeneficiary> findByUser(RetailUser user);
    InternationalBeneficiary findByUser_IdAndAccountNumber(Long id,String acctNo);

    Iterable<InternationalBeneficiary> findByUserAndDelFlag(RetailUser user, String delFlag);


}
