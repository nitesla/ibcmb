package longbridge.services;

import longbridge.dtos.CorpPaymentRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;

public interface CorpCustomDutyService {

    CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt);

    CustomsAreaCommand getCustomsAreaCommands();

    void paymentNotification(CustomAssessmentDetail assessmentDetail);

    CustomTransactionStatus paymentStatus(CustomTransactionStatus customTransactionStatus);

    public boolean isAccountBalanceEnough(String acctNumber, BigDecimal amount);


    public String saveCustomPaymentRequestForAuthorization(CorpPaymentRequest corpPaymentRequest);

    String addAuthorization(CorpTransReqEntry transReqEntry);

    boolean userCanAuthorize(TransRequest transRequest);

    Page<CorpPaymentRequest> getPaymentRequests(Pageable pageable);

    CorpPaymentRequest getPayment(Long id);

    CorpTransferAuth getAuthorizations(CorpPaymentRequest corpPaymentRequest);
}
