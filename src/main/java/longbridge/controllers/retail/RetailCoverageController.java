package longbridge.controllers.retail;

import longbridge.services.AccountCoverageService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

public class RetailCoverageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountCoverageService coverageService;

    @Autowired
    MessageSource messageSource;


//    @GetMapping(path = "/{retId}")
//    @ResponseBody
//    public JSONObject getEnabledCoverageForCorporate(@PathVariable Long retId){
//        return coverageService.getAllEnabledCoverageDetailsForRetailUser(retId);
//    }



}
