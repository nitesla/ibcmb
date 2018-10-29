package longbridge.controllers.corporate;

import longbridge.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/corporate/custom")
public class CustomDutyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDutyController.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("http://localhost:8090/customduty/retrieveassessmentdetail")
    private String apiGetAssessmentDetailsUrl;

    //@Value("http://132.10.200.201:8083/customduty/getncscommand")
    @Value("${custom.duty}")
    private String apiGetncsCommandUrl;

    @Value("http://localhost:8090/customduty/payassessment")
    private String apiPaymentNotificationUrl;

    @Value("http://localhost:8090/customduty/checktransactionstatus")
    private String apiTransactionStatusUrl;

    @GetMapping
    public String getCustomDuty(Model model, Principal principal) {
        return "corp/custom/customduty";
    }

    @GetMapping("/payment")
    public String dutyPayment(Model model, Principal principal) {
        List<Commands> command = getCustomsAreaCommands();
        LOGGER.info("the camands {}",command.size());
        model.addAttribute("commands",command);
        return "corp/custom/custompayment";
    }

    @PostMapping("/details")
    public List<CustomAssessmentDetail> getAssessmentDetails(@ModelAttribute("assessmentDetailsRequest") @Valid CustomAssessmentDetailsRequest assessmentDetailsRequest){
        try {
            LOGGER.debug("Fetching data from coronation rest service via the url: {}", apiGetAssessmentDetailsUrl);

            ResponseEntity<CustomAssessmentDetail[]> response = restTemplate.postForEntity(apiGetAssessmentDetailsUrl, assessmentDetailsRequest, CustomAssessmentDetail[].class);
            CustomAssessmentDetail[] restData = response.getBody();
            LOGGER.debug("assessment details response: {}", restData);

            return Arrays.asList(restData);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return new ArrayList<>();
    }

    @PostMapping("/commands")
    public List<Commands> getCustomsAreaCommands() {
        CustomsAreaCommandRequest customsAreaCommandRequest = new CustomsAreaCommandRequest();
        customsAreaCommandRequest.setAppId("test");
        customsAreaCommandRequest.setHash("ad1222bgfghj22j33m333");
        try {
            LOGGER.debug("Fetching data from coronation rest service via the url: {}", apiGetncsCommandUrl);

            ResponseEntity<Commands[]> response = restTemplate.postForEntity(apiGetncsCommandUrl+"/customduty/getncscommand"   , customsAreaCommandRequest, Commands[].class);
            Commands[] restData = response.getBody();
            LOGGER.debug("Customs area command Response: {}", restData);

            return Arrays.asList(restData);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return new ArrayList<>();
    }

    @PostMapping("/payment")
    public List<CustomPaymentNotification> paymentNotification(@ModelAttribute @Valid CustomPaymentNotificationRequest paymentNotificationRequest){
        try {
            LOGGER.debug("Fetching data from coronation rest service via the url: {}", apiPaymentNotificationUrl);

            ResponseEntity<CustomPaymentNotification[]> response = restTemplate.postForEntity(apiPaymentNotificationUrl, paymentNotificationRequest, CustomPaymentNotification[].class);
            CustomPaymentNotification[] restData = response.getBody();
            LOGGER.debug("payment notification Response: {}", restData);

            return Arrays.asList(restData);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return new ArrayList<>();
    }

    public CustomTransactionStatus paymentStatus(@ModelAttribute @Valid CustomTransactionStatus customTransactionStatus){
        try {
            LOGGER.debug("Fetching data from coronation rest service via the url: {}", apiTransactionStatusUrl);
            ResponseEntity<CustomTransactionStatus> response = restTemplate.postForEntity(apiTransactionStatusUrl, customTransactionStatus, CustomTransactionStatus.class);
            CustomTransactionStatus restData = response.getBody();
            LOGGER.debug("Transaction Status Response: {}", restData);
            return restData;
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return null;

    }
}
