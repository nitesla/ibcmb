package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.services.CorpCustomDutyService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CorpCustomDutyServiceImpl implements CorpCustomDutyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpCustomDutyServiceImpl.class);

    @Autowired
    private IntegrationService integrationService;

    @Override
    public CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt) {
        CustomAssessmentDetailsRequest assessmentDetailsRequest =  new CustomAssessmentDetailsRequest();
        assessmentDetailsRequest.setSadAsmt(sadAsmt);
       return integrationService.getAssessmentDetails(assessmentDetailsRequest);
    }

    @Override
    public CustomsAreaCommand getCustomsAreaCommands() {
        CustomsAreaCommandRequest customsAreaCommandRequest = new CustomsAreaCommandRequest();
        customsAreaCommandRequest.setAppId("test");
        customsAreaCommandRequest.setHash("ad1222bgfghj22j33m333");
        return integrationService.getCustomsAreaCommands(customsAreaCommandRequest);
    }

    @Override
    public void paymentNotification(CustomAssessmentDetail assessmentDetail) {
        CustomPaymentNotificationRequest paymentNotificationRequest = new CustomPaymentNotificationRequest();
        integrationService.paymentNotification(paymentNotificationRequest);
    }

    @Override
    public CustomTransactionStatus paymentStatus(CustomTransactionStatus customTransactionStatus) {
        return integrationService.paymentStatus(customTransactionStatus);
    }

    public String checkAccountBalance(String acctNumber, BigDecimal amount){
        BigDecimal availableBalance =  integrationService.getAvailableBalance(acctNumber);
        LOGGER.info("the availableBalance {}",availableBalance);
        if(availableBalance.compareTo(amount) > 0){

        }else{
            LOGGER.debug(" not Enough money in account: {}",availableBalance.compareTo(amount) > 0);
        }
        return null;
    }
}
