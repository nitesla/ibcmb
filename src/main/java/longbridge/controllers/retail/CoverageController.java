package longbridge.controllers.retail;


import longbridge.services.CoverageService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/retail/coverage")
public class CoverageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CoverageService coverageService;


    @GetMapping
    public @ResponseBody
    ResponseEntity<?> getCoverageDetails(@RequestParam("coverageName") String coverageName) {
        try {
            return ResponseEntity.ok(coverageService.getCoverage(coverageName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
}

