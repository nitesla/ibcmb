package longbridge.controllers.corporate;



import longbridge.services.AccountCoverageService;
import longbridge.services.IntegrationService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/corporate/accountcoverage")
public class CorpAccountCoverageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private AccountCoverageService coverageService;

    @Autowired
    MessageSource messageSource;

//
//    @GetMapping
//    public String listcoverage(Model model) {
//        List<String> coverageList =coverageService.enabledCoverageList();
//        if(!(integrationService.getAllCoverageDetails("12345").isEmpty())){
//        model.addAttribute("coverageList",coverageList);}
//
//        System.out.println(integrationService.getAllCoverageDetails("12345"));
//        return "corp/coverage/index";
//    }
//
//    @GetMapping(path = "/all")
//    @ResponseBody
//    public JSONObject getAllCoverage(){
//
//       return integrationService.getAllCoverageDetails("12345");
//    }



}

