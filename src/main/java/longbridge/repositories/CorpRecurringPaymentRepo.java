package longbridge.repositories;

import longbridge.models.CorpRecurringPayment;

import java.util.Date;
import java.util.List;

/**
 * Created by mac on 26/02/2018.
 */
public interface CorpRecurringPaymentRepo extends CommonRepo<CorpRecurringPayment, Long> {
    List<CorpRecurringPayment> findByNextDebitDateEquals(Date date);

   // Page<DirectDebit> findByCorporate(Long corporate, Pageable pageable);

    List<CorpRecurringPayment> findByNextDebitDateBetween(Date start, Date end);
}
