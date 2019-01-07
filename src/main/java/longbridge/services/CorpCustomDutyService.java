package longbridge.services;

import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;

public interface CorpCustomDutyService {

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt, String customCode);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomsAreaCommand getCustomsAreaCommands();

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomTransactionStatus getPaymentStatus(CorpPaymentRequest corpPaymentRequest);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomTransactionStatus updatePayamentStatus(Long id);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    public boolean isAccountBalanceEnough(String acctNumber, BigDecimal amount);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    public String saveCustomPaymentRequestForAuthorization(CorpPaymentRequest corpPaymentRequest);

    String addAuthorization(CorpTransReqEntry transReqEntry, Principal principal);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    @Transactional
    String saveCustomPaymentRequestForAuthorization(CustomAssessmentDetail assessmentDetail, CustomAssessmentDetailsRequest assessmentDetailsRequest,  Principal principal,Corporate corporate );

    boolean userCanAuthorize(TransRequest transRequest);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    Page<CorpPaymentRequest> getPaymentRequests(Pageable pageable);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CorpPaymentRequest getPayment(Long id);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    Page<CorpPaymentRequest> getPayments(Pageable pageable, String search);

    CorpTransferAuth getAuthorizations(CorpPaymentRequest corpPaymentRequest);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomDutyPayment saveCustomDutyPayment(CustomAssessmentDetail assessmentDetail, CustomAssessmentDetailsRequest assessmentDetailsRequest, Principal principal);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CorpPaymentRequest saveCorpPaymentRequest(CustomDutyPayment customDutyPayment, Corporate corporate,Principal principal, boolean isSole);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    Page<CorpPaymentRequest> findEntities(String filter,String search, Pageable pageable);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    Page<CorpPaymentRequest> getEntities(Pageable pageable);


}
