package longbridge.repositories;

import longbridge.models.DirectDebit;
import longbridge.models.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 22/02/2018.
 */
public interface PaymentRepo extends CommonRepo<Payment,Long> {

    Collection<Payment> findByDirectDebit(DirectDebit directDebit);

    @Query("select p from Payment p where p.debitDate between :start and :endDate and p.paymentStatus='PENDING'")
    List<Payment> findByDebitDateBetween(@Param("start") Date start, @Param("endDate") Date endDate);


}
