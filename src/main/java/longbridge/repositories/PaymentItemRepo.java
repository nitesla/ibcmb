package longbridge.repositories;

import longbridge.models.PaymentItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PaymentItemRepo extends CommonRepo<PaymentItem, Long>{

    PaymentItem findByPaymentItemId(Long paymentItemId);

    PaymentItem findByPaymentItemName(String name);

    @Transactional
    @Modifying
    @Query("update PaymentItem item set item.enabled = :status where item.id = :id")
    void enablePaymentItem(@Param("id") Long id, @Param("status") Boolean status);


    @Modifying
    @Query("update PaymentItem b set b.delFlag = 'Y' where b.id not in (:id) and b.billerId = :billerid")
    void removeObsolete(@Param("id") List<Long> collect, @Param("billerid") Long billerid);

    List<PaymentItem> findByBillerId(Long billerId);

    @Transactional
    @Modifying
    @Query("update PaymentItem p set p.readonly = :status where p.id = :id")
    void readOnly(@Param("id") Long id,@Param("status") Boolean status);

    List<PaymentItem> findAllByEnabledAndBillerId(Boolean enabled, Long billerId);

    List<PaymentItem> findAllByEnabledAndBillerId(boolean enabled, Long billerId);
}
