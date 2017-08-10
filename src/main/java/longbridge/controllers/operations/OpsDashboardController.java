package longbridge.controllers.operations;

import longbridge.models.OperationsUser;
import longbridge.services.CorporateService;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Autowired
    RetailUserService retailUserService;

    @Autowired
    CorporateService corporateService;

    @RequestMapping(value = {"/ops/dashboard", "/ops"})
    public String getOpsDashboard(Model model, Principal principal) {


        if (principal.getName() == null) {
            return "redirect://login/ops";
        }

        int noOfRetailCust = retailUserService.countUser().intValue();
        int noOfCorporateCust = corporateService.countCorporate().intValue();
        model.addAttribute("noOfRetailCust", noOfRetailCust);
        model.addAttribute("noOfCorporateCust", noOfCorporateCust);

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