package longbridge.repositories;

import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
public interface CorpLocalBeneficiaryRepo extends CommonRepo<CorpLocalBeneficiary, Long> {
    Iterable<CorpLocalBeneficiary> findByUser(CorporateUser user);

    Iterable<CorpLocalBeneficiary> findByUserAndDelFlag(CorporateUser user, String delFlag);

}
