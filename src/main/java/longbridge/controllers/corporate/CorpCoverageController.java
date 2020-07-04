package longbridge.controllers.corporate;



import longbridge.config.CoverageInfo;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.services.AccountCoverageService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Controller
@RequestMapping("/corporate/accountcoverage")
public class CorpCoverageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountCoverageService coverageService;

    @Autowired
    MessageSource messageSource;

    @Resource(name = "sessionScopedBean")
    CoverageInfo sessionScopedBean;


    @GetMapping(path = "/{corpId}")
    @ResponseBody
    public List<CoverageDetailsDTO> getEnabledCoverageForCorporate(@PathVariable Long corpId){
        coverageService.getAllEnabledCoverageDetailsForCorporate(corpId);
        return sessionScopedBean.getCoverage();
    }



}

