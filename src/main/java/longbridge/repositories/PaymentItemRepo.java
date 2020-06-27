package longbridge.repositories;

import longbridge.models.PaymentItem;

import java.util.List;


public interface PaymentItemRepo extends CommonRepo<PaymentItem, Long>{

    List<PaymentItem> findByPaymentItemName(String categoryName);
}
