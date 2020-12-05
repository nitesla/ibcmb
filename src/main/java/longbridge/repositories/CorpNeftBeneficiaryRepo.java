package longbridge.repositories;

import longbridge.models.CorpNeftBeneficiary;
import longbridge.models.Corporate;
import org.springframework.stereotype.Repository;

@Repository
public interface CorpNeftBeneficiaryRepo extends CommonRepo<CorpNeftBeneficiary, Long> {
    boolean existsByUser_IdAndBeneficiaryAccountNumber(Long id, String beneficiaryAccountNumber);

    Iterable<CorpNeftBeneficiary> findByUser(Corporate corporate);
}
