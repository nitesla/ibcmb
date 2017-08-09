package longbridge.controllers.operations;

import longbridge.models.OperationsUser;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.ws.soap.Addressing;
import java.security.Principal;
import java.text.SimpleDateFormat;

/**
 * Created by Fortune on 5/17/2017.
 */
@Controller
public class OpsDashboardController {

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    OperationsUserService operationsUserService;

    @RequestMapping(value = {"/ops/dashboard", "/ops"})
    public String getOpsDashboard(Model model, Principal principal) {


        if (principal.getName() == null) {
            return "redirect://login/ops";
        }


        OperationsUser operationsUser = operationsUserService.getUserByName(principal.getName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy hh:mm a");
        if(passwordPolicyService.displayPasswordExpiryDate(operationsUser.getExpiryDate())){
            model.addAttribute("expireDate",dateFormat.format(operationsUser.getExpiryDate()));
        }

        return "ops/dashboard";
    }


    @GetMapping("/ops/error")
    public String getOpsErrorPage() {
        return "/ops/error";

    }

}