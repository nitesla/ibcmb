package longbridge.repositories;

import longbridge.models.CorpInterBeneficiary;
import longbridge.models.Corporate;
import org.springframework.stereotype.Repository;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@Repository
public interface CorpInternationalBeneficiaryRepo extends CommonRepo<CorpInterBeneficiary,Long>{

    Iterable<CorpInterBeneficiary> findByCorporate(Corporate corporate);
    CorpInterBeneficiary findByCorporate_IdAndAccountNumber(Long id, String acctNo);
}

