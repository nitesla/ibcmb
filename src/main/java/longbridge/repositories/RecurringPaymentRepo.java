package longbridge.repositories;


import longbridge.models.RecurringPayment;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RecurringPaymentRepo extends CommonRepo<RecurringPayment,Long> {

//	List<RecurringPayment> findByNextDebitDateEquals(Date date);

	Page<RecurringPayment> findByRetailUser(RetailUser user, Pageable pageable);

	List<RecurringPayment> findByNextDebitDateBetween(Date start, Date end);

    Page<RecurringPayment> findByCorporate(Long corporate, Pageable pageable);
//    Optional<List<RecurringPayment>> findByBeneficiaryId(Long id);
//    Optional<List<RecurringPayment>> findByCorpLocalBeneficiaryId(Long id);
}
