package longbridge.controllers.operations;

import longbridge.models.CorpPaymentRequest;
import longbridge.services.CorpCustomDutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by mac on 17/11/2018.
 */
@Controller
@RequestMapping("ops/custom")
public class OpsCustomDutyController {
    @Autowired
    private CorpCustomDutyService customDutyService;

//    @GetMapping
//    public @ResponseBody DataTablesOutput<CorpPaymentRequest> String viewExistingCustomDuty(@Data ){
//
//        return "ops/customduty/view";
//    }
}
