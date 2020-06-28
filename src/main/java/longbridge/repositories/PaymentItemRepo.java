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
    @Query("update PaymentItem item set item.enabled = :status where item.id = :id")
    void enablePaymentItem(@Param("id") Long id, boolean status);


    @Modifying
    @Query("update PaymentItem b set b.delFlag = 'Y' where b.id not in (:id) ")
    void removeObsolete(@Param("id") List<Long> collect);

    List<PaymentItem> findByBillerId(Long billerId);

    @Modifying
    @Query("update PaymentItem p set p.readonly = :status where p.id = :id")
    void readOnly(@Param("id") Long id, Boolean status);
}
