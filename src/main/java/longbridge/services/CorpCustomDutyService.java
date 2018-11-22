package longbridge.services;

import longbridge.dtos.CorpPaymentRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;
import java.security.Principal;

public interface CorpCustomDutyService {

    CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt);

    CustomsAreaCommand getCustomsAreaCommands();

    void paymentNotification(CustomAssessmentDetail assessmentDetail);

    CustomTransactionStatus paymentStatus(CustomTransactionStatus customTransactionStatus);

    public boolean isAccountBalanceEnough(String acctNumber, BigDecimal amount);


    public String saveCustomPaymentRequestForAuthorization(CorpPaymentRequest corpPaymentRequest);

    String addAuthorization(CorpTransReqEntry transReqEntry);

    @Transactional
    String saveCustomPaymentRequestForAuthorization(CustomAssessmentDetail assessmentDetail, Principal principal,Corporate corporate );

    boolean userCanAuthorize(TransRequest transRequest);

    Page<CorpPaymentRequest> getPaymentRequests(Pageable pageable);

    CorpPaymentRequest getPayment(Long id);

    Page<CorpPaymentRequest> getPayments(Pageable pageable, String search);

    CorpTransferAuth getAuthorizations(CorpPaymentRequest corpPaymentRequest);
}
