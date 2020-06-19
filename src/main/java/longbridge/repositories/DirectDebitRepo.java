package longbridge.repositories;


import longbridge.models.DirectDebit;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DirectDebitRepo extends CommonRepo<DirectDebit,Long> {

	List<DirectDebit> findByNextDebitDateEquals(Date date);

	Page<DirectDebit> findByRetailUser(RetailUser user, Pageable pageable);

	List<DirectDebit> findByNextDebitDateBetween(Date start, Date end);

    Page<DirectDebit> findByCorporate(Long corporate, Pageable pageable);
    Optional<List<DirectDebit>> findByBeneficiaryId(Long id);
    Optional<List<DirectDebit>> findByCorpLocalBeneficiaryId(Long id);
}
