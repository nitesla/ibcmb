package longbridge.services;


import longbridge.dtos.RecurringPaymentDTO;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface RecurringPaymentService {

    String addCorpRecurringPayment(CorporateUser user, RecurringPaymentDTO recurringPaymentDTO);

    String addRecurringPayment(RetailUser user, RecurringPaymentDTO recurringPaymentDTO);

    Page<RecurringPaymentDTO> getUserRecurringPaymentDTOs(RetailUser user, Pageable pageable);
}
