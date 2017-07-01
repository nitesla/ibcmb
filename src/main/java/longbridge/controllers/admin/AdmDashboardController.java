package longbridge.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Fortune on 6/30/2017.
 */

@Controller
public class AdmDashboardController {

    @RequestMapping(value = {"/admin/dashboard", "/admin"})
    public String getAdminDashboard() {
        return "adm/dashboard";
    }

}
