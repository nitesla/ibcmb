package longbridge.repositories;

import longbridge.models.CorpQuickBeneficiary;
import longbridge.models.Corporate;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
public interface CorpQuickBeneficiaryRepo extends CommonRepo<CorpQuickBeneficiary, Long> {
    Iterable<CorpQuickBeneficiary> findByCorporate(Corporate corporate);
    boolean existsByCorporate_IdAndAccountNumber(Long id,String s);
    CorpQuickBeneficiary findByCorporate_IdAndAccountNumber(Long id,String s);

}
