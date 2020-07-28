package longbridge.controllers.retail;

import longbridge.config.CoverageInfo;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.services.AccountCoverageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/retail/accountcoverage")
public class RetailCoverageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    MessageSource messageSource;



    @Resource(name = "sessionCoverage")
    private Set<CoverageDetailsDTO> coverageDetails;

    @GetMapping(path = "/{retId}")
    @ResponseBody
    public Set<CoverageDetailsDTO> getCoverageForCorporate(@PathVariable Long  retId){
          return coverageDetails;
    }



}
