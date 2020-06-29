package longbridge.controllers.corporate;



import longbridge.repositories.AccountCoverageRepo;
import longbridge.services.AccountCoverageService;
import longbridge.services.IntegrationService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/corporate/accountcoverage")
public class CorpCoverageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    MessageSource messageSource;




    @GetMapping(path = "/{corpId}")
    @ResponseBody
    public JSONObject getEnabledCoverageForCorporate(@PathVariable Long corpId){
        return integrationService.getAllEnabledCoverageDetailsForCorporate(corpId);
    }



}

