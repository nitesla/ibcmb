package longbridge.services;

import longbridge.dtos.BillPaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface PaymentService {



    String addBillPayment(BillPaymentDTO paymentDTO);


    String updatePaymentStatus(String status);




    Page<BillPaymentDTO> getBillPayments(Pageable pageable);

    Page<BillPaymentDTO> getCorpPayments(Pageable pageable);
}
