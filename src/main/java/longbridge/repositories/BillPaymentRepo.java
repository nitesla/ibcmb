package longbridge.repositories;

import longbridge.models.BillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 09/07/2018.
 */
@Repository
public interface BillPaymentRepo extends CommonRepo<BillPayment,Long> {

    Page<BillPayment> findByUserId(Long userId, Pageable pageable);
}
