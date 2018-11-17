package longbridge.repositories;

import longbridge.models.CorpPaymentRequest;
import longbridge.models.CustomDutyPayment;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomDutyPaymentRepo extends CommonRepo<CustomDutyPayment, Long>{

//    public long saveCustomDuty(CustomDutyPayment customDutyPayment);
}
