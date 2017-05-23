package longbridge.repositories;

import longbridge.models.CorpInternationalBeneficiary;
import longbridge.models.CorporateUser;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public interface CorpInternationalBeneficiaryRepo extends CommonRepo<CorpInternationalBeneficiary,Long>{

    Iterable<CorpInternationalBeneficiary> findByUser(CorporateUser user);

    Iterable<CorpInternationalBeneficiary> findByUserAndDelFlag(CorporateUser user, String delFlag);

}

