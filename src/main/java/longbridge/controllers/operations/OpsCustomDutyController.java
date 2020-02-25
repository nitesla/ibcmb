package longbridge.controllers.operations;

import longbridge.models.CorpPaymentRequest;
import longbridge.services.CorpCustomDutyService;
import longbridge.utils.CustomDutyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import longbridge.utils.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by mac on 17/11/2018.
 */
@Controller
@RequestMapping("/ops/custom")
public class OpsCustomDutyController {
    @Autowired
    private CorpCustomDutyService customDutyService;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @GetMapping
    public String viewCustomPaymentRequest(){
        return "ops/customduty/view";
    }


    @GetMapping("/all")
    public @ResponseBody DataTablesOutput<CorpPaymentRequest>  viewExistingCustomDuty(DataTablesInput input, @RequestParam("csearch") String search){
        Pageable pageable = DataTablesUtils.getPageable(input);
        logger.info("the custom duty {}",search);
        Page<CorpPaymentRequest> corpPaymentRequests =customDutyService.getPayments(pageable,search);
        for (CorpPaymentRequest request:corpPaymentRequests) {
            request.getCustomDutyPayment().setMessage(
                    CustomDutyCode.getCustomDutyCodeByCodeForOPS(
                            request.getCustomDutyPayment().getPaymentStatus()).replace("_"," "));
        }
        DataTablesOutput<CorpPaymentRequest> dataTablesOutput =  new DataTablesOutput<>();
        dataTablesOutput.setDraw(input.getDraw());

        dataTablesOutput.setData(corpPaymentRequests.getContent());
        dataTablesOutput.setRecordsFiltered(corpPaymentRequests.getTotalElements());
        dataTablesOutput.setRecordsTotal(corpPaymentRequests.getTotalElements());
        return dataTablesOutput;
    }
}
