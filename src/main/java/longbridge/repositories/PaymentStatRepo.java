package longbridge.repositories;

import longbridge.models.PaymentStat;
import longbridge.models.RecurringPayment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 22/02/2018.
 */
public interface PaymentStatRepo extends CommonRepo<PaymentStat,Long> {

    Collection<PaymentStat> findByRecurringPayment(RecurringPayment recurringPayment);

    @Query("select p from PaymentStat p where p.debitDate between :start and :endDate and p.paymentStatus='PENDING'")
    List<PaymentStat> findByDebitDateBetween(@Param("start") Date start, @Param("endDate") Date endDate);


}
