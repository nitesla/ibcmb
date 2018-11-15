package longbridge.controllers.corporate;

import longbridge.models.*;
import longbridge.services.CorpCustomDutyService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/corporate/custom")
public class CustomDutyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDutyController.class);

    @Autowired
    private CorpCustomDutyService customDutyService;

    @GetMapping
    public String getCustomDuty(Model model, Principal principal) {
        return "corp/custom/customduty";
    }

    @GetMapping("/payment")
    public String dutyPayment(Model model, Principal principal) {
        model.addAttribute("commands",getCustomsAreaCommands().getCommands());
        model.addAttribute("assessmentDetailsRequest",new CustomAssessmentDetailsRequest());
        model.addAttribute("paymentNotificationRequest",new CustomPaymentNotificationRequest());
        model.addAttribute("assessmentDetail",new CustomAssessmentDetail());
        return "corp/custom/custompayment";
    }

    @PostMapping("/accessment/details")
    @ResponseBody
    public CustomAssessmentDetail getAssessment(WebRequest webRequest,@ModelAttribute("assessmentDetailsRequest") @Valid CustomSADAsmt customSADAsmt){
        LOGGER.info("the assessmentDetailsRequest {}",customSADAsmt);
        return customDutyService.getAssessmentDetails(customSADAsmt);
    }

    @PostMapping("/commands")
    public CustomsAreaCommand getCustomsAreaCommands() {

        try {
            return customDutyService.getCustomsAreaCommands();
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return null;
    }

    @PostMapping("/payment")
    public CustomPaymentNotification customPayment(
            @ModelAttribute("assessmentDetail")  @Valid  CustomAssessmentDetail assessmentDetail,
            @ModelAttribute("account") String account){

        LOGGER.debug("Assessment Details: {}",assessmentDetail);
        customDutyService.checkAccountBalance(account,
                new BigDecimal(
                        assessmentDetail.getResponseInfo().getTotalAmount(), MathContext.DECIMAL64));

        CustomPaymentNotificationRequest paymentNotificationRequest = new CustomPaymentNotificationRequest();
        try {
            LOGGER.debug("making payment requests:{}",paymentNotificationRequest);
            customDutyService.paymentNotification(assessmentDetail);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return new CustomPaymentNotification();
    }

    public CustomTransactionStatus paymentStatus(@ModelAttribute @Valid CustomTransactionStatus customTransactionStatus){

        try {
            return customDutyService.paymentStatus(customTransactionStatus);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return null;

    }
}
