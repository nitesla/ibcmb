package longbridge.controllers.operations;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Fortune on 5/17/2017.
 */
@Controller
public class OpsDashboardController {

    @RequestMapping(value = {"/ops/dashboard", "/ops"})
    public String getOpsDashboard() {
        return "ops/dashboard";
    }

}
