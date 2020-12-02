package longbridge.repositories;

import longbridge.models.CorpNeftBeneficiary;
import longbridge.models.Corporate;
import org.springframework.stereotype.Repository;

@Repository
public interface CorpNeftBeneficiaryRepo extends CommonRepo<CorpNeftBeneficiary, Long>{

    Iterable<CorpNeftBeneficiary> findByCorporate(Corporate user);
    boolean existsByCorporate_IdAndBeneficiaryAccountNumber(Long id,String s);
}
