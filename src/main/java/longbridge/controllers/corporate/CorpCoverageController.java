package longbridge.controllers.corporate;



import longbridge.services.AccountCoverageService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/corporate/accountcoverage")
public class CorpCoverageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountCoverageService coverageService;

    @Autowired
    MessageSource messageSource;


    @GetMapping(path = "/{corpId}")
    @ResponseBody
    public JSONObject getEnabledCoverageForCorporate(@PathVariable Long corpId){
       return coverageService.getAllEnabledCoverageDetailsForCorporate(corpId);
    }



}

