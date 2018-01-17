package longbridge.repositories;

import longbridge.models.CorpInterBen;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public interface CorpInternationalBeneficiaryRepo extends CommonRepo<CorpInterBen,Long>{

    Iterable<CorpInterBen> findByCorporate(Corporate corporate);


}

