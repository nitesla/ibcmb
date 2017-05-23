package longbridge.controllers.corporate;

import longbridge.models.CorporateUser;
import longbridge.services.CorporateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Created by Showboy on 19/05/2017.
 */
@Controller
@RequestMapping("/corporate")
public class CorporateMainController {

    @Autowired
    private CorporateUserService corporateUserService;

    @RequestMapping("/dashboard")
    public String getCorporateDashboard(Model model, Principal principal) {
        CorporateUser corporateUser= corporateUserService.getUserByName(principal.getName());
       /* List<AccountDTO> accountList = accountService.getAccountsForDebitAndCredit(retailUser.getCustomerId());
        model.addAttribute("accountList", accountList);
      */  return "corp/dashboard";
    }
}
