package longbridge.controllers.admin;

import longbridge.models.AdminUser;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private RetailUserService retailUserService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private OperationsUserService operationsUserService;

    @RequestMapping(value = {"/admin/dashboard", "/admin"})
    public String getAdminDashboard(Model model, Principal principal) {

        if (principal== null) {
            return "redirect:/login/admin";
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
    public String getAdminErrorPage(Principal principal) {

            if (principal == null) {
                return "redirect:/admin/logout";
            }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication!=null) {
                CustomUserPrincipal princip = (CustomUserPrincipal) authentication.getPrincipal();
                User user = princip.getUser();
                if (!UserType.ADMIN.equals(user.getUserType())) {
                    return "redirect:/admin/logout";
                }
            }
            else {
                return "redirect:/login/admin";
            }

        return "/adm/error";

    }
}
