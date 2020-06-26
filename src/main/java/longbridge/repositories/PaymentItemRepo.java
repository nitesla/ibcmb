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


    @Modifying
    @Query("update PaymentItem item set item.enabled = :status where item.id = :id")
    void enablePaymentItem(@Param("id") Long id, boolean status);

}
