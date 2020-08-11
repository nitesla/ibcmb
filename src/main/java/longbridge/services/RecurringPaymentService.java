package longbridge.services;


import longbridge.dtos.PaymentStatDTO;
import longbridge.dtos.QuicktellerRequestDTO;
import longbridge.dtos.RecurringPaymentDTO;
import longbridge.models.CorporateUser;
import longbridge.models.PaymentStat;
import longbridge.models.RecurringPayment;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface RecurringPaymentService {

    String addCorpRecurringPayment(CorporateUser user, RecurringPaymentDTO recurringPaymentDTO);

    String addRecurringPayment(RetailUser user, RecurringPaymentDTO recurringPaymentDTO);

    Page<RecurringPaymentDTO> getUserRecurringPaymentDTOs(RetailUser user, Pageable pageable);

    String deleteRecurringPayment(Long recurringPaymentId);

    RecurringPayment getRecurringPayment(Long recurringPaymentId);

    Collection<PaymentStat> recurringPayments(RecurringPayment recurringPayment);

    String deletePayment(Long id);

    Collection<PaymentStatDTO>  getPayments(RecurringPayment recurringPayment);

    RecurringPayment getPaymentsRecurringPayment(Long paymentId);

    List<PaymentStat> getDuePayments();

    void performRecurringPayment(PaymentStat paymentStat);

    boolean validateRecurringPayment(QuicktellerRequestDTO dto);

    List<RecurringPayment> getDueRecurringPayment();

    RecurringPayment makeBackgroundTransfer(QuicktellerRequestDTO quicktellerRequestDTO, RecurringPayment recurringPayment);
}
