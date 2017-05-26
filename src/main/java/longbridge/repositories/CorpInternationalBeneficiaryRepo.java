package longbridge.repositories;

import longbridge.models.CorpInterBen;
import longbridge.models.CorporateUser;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public interface CorpInternationalBeneficiaryRepo extends CommonRepo<CorpInterBen,Long>{

    Iterable<CorpInterBen> findByUser(CorporateUser user);

    Iterable<CorpInterBen> findByUserAndDelFlag(CorporateUser user, String delFlag);

}

