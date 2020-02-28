package longbridge.controllers;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.models.Code;
import longbridge.services.ServiceReqConfigService;
import longbridge.utils.MoneyConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by mac on 24/08/2018.
 */
@RestController
@RequestMapping("/util")
public class UtilityController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/convertAmountToWords")
//    @ResponseBody
    public String convertAmountToWords(WebRequest webRequest) {
        String amount = webRequest.getParameter("amount");
        String wordAmount = MoneyConversion.doubleConvert(amount);
        return wordAmount;
    }


}
