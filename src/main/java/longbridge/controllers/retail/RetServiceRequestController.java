package longbridge.controllers.retail;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.services.ServiceReqConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Showboy on 10/04/2017.
 */
@Controller
@RequestMapping("/pub/servicerequest")
public class RetServiceRequestController {

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @GetMapping("/{reqId}")
    public String addConfig(@PathVariable Long reqId, Model model){
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        logger.info("Request Details {}", serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "cust/servicerequest/add";
    }

}
