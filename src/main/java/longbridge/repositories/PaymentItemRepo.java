package longbridge.repositories;

import longbridge.models.PaymentItem;
import longbridge.models.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PaymentItemRepo extends CommonRepo<PaymentItem, Long>{

    List<PaymentItem> getAllByBillerId(Long billerId);

    PaymentItem findByPaymentItemId(Long paymentItemId);

    List<PaymentItem> findByPaymentItemIdNotIn(List<Long> paymentItemId);

    @Transactional
    @Modifying
    @Query("update PaymentItem b set b.enabled = false where b.id = :id")
    int disablePaymentItem(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("update PaymentItem b set b.enabled = true where b.id = :id")
    int enablePaymentItem(@Param("id") Long id);

}
