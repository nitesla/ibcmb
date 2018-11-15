package longbridge.services;

import longbridge.models.*;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;

public interface CorpCustomDutyService {
    CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt);

    CustomsAreaCommand getCustomsAreaCommands();

    void paymentNotification(CustomAssessmentDetail assessmentDetail);

    CustomTransactionStatus paymentStatus(CustomTransactionStatus customTransactionStatus);

    public String checkAccountBalance(String acctNumber, BigDecimal amount);
}
