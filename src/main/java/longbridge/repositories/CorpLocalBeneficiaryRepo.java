package longbridge.repositories;

import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.Corporate;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
public interface CorpLocalBeneficiaryRepo extends CommonRepo<CorpLocalBeneficiary, Long> {
    Iterable<CorpLocalBeneficiary> findByCorporate(Corporate corporate);
    boolean existsByCorporate_IdAndAccountNumber(Long id,String s);
    CorpLocalBeneficiary findByCorporate_IdAndAccountNumber(Long id,String s);

}
