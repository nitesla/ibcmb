package longbridge.controllers.admin;

import longbridge.models.AdminUser;
import longbridge.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.text.SimpleDateFormat;

/**
 * Created by Fortune on 6/30/2017.
 */

@Controller
public class AdmDashboardController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    RetailUserService retailUserService;

    @Autowired
    CorporateService corporateService;

    @Autowired
    OperationsUserService operationsUserService;

    @RequestMapping(value = {"/admin/dashboard", "/admin"})
    public String getAdminDashboard(Model model, Principal principal) {

        if (principal.getName() == null) {
            return "redirect://login/admin";
        }

        int noOfRetailCust = retailUserService.countUser().intValue();
        int noOfCorporateCust = corporateService.countCorporate().intValue();
        int noOfOpsUser = operationsUserService.countOps().intValue();
        int noOfAdmUser = adminUserService.countAdm().intValue();
        model.addAttribute("noOfRetailCust", noOfRetailCust);
        model.addAttribute("noOfCorporateCust", noOfCorporateCust);
        model.addAttribute("noOfOpsUser", noOfOpsUser);
        model.addAttribute("noOfAdmUser", noOfAdmUser);

    AdminUser adminUser = adminUserService.getUserByName(principal.getName());

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy hh:mm a");
        if(passwordPolicyService.displayPasswordExpiryDate(adminUser.getExpiryDate())){
        model.addAttribute("expireDate",dateFormat.format(adminUser.getExpiryDate()));
    }
        return "adm/dashboard";
    }



    @GetMapping("/admin/error")
    public String getAdminErrorPage() {
        return "/adm/error";

    }
}
