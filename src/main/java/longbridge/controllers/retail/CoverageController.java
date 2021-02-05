package longbridge.controllers.retail;


import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/retail/coverage")
public class CoverageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserService retailUserService;

    @GetMapping("/view/{customerId}/{coverageName}")
    public String viewCoverageDetails(@PathVariable String customerId, @PathVariable String coverageName, Model model) {
        model.addAttribute("customerId", customerId);
        model.addAttribute("coverageName", coverageName.toUpperCase());
        return "cust/coverage/index";
    }


}
