package longbridge.controllers.corporate;

import longbridge.services.CorpCoverageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/corporate/coverage")
public class CorpCoverageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private CorpCoverageService corpCoverageService;

    @GetMapping
    public @ResponseBody
    ResponseEntity<?> getCoverageDetails(@RequestParam("coverageName") String coverageName) {
        try {
            return ResponseEntity.ok(corpCoverageService.getCoverage(coverageName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}