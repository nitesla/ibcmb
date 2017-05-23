package longbridge.controllers.corporate;

import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.models.ServiceReqConfig;
import longbridge.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@ControllerAdvice(basePackages={"longbridge.controllers.corporate"})
public class CorporateControllerAdvice {
    private CorporateUserService corporateUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private ServiceReqConfigService reqConfigService;

    @Autowired

    public CorporateControllerAdvice(CorporateUserService corporateUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, ServiceReqConfigService reqConfigService) {
        this.corporateUserService = corporateUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.reqConfigService = reqConfigService;
    }

    @ModelAttribute
    public String globalAtrributes(Model model, Principal principal){
        String greeting="";
        if(principal.getName()==null){
            return "redirect:/login/corporate";
        }

        CorporateUser corporateUser=corporateUserService.getUserByName(principal.getName());
        String bankVerificationNumber;
        if(corporateUser.getCorporate().getBvn()==null)
        {bankVerificationNumber="";}
        else
        {bankVerificationNumber=corporateUser.getCorporate().getBvn();}

        model.addAttribute("bvn",bankVerificationNumber);
        model.addAttribute("lastLogin",corporateUser.getLastLoginDate());
        Calendar calendar=Calendar.getInstance();
        int timeOfDay=calendar.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay>=0 && timeOfDay<12){
            greeting="Good morning, ";

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good afternoon, ";

        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            greeting = "Good evening, ";
        }
        model.addAttribute("greeting", greeting);

        String firstName = corporateUser.getFirstName();
        String lastName = "";
        if (corporateUser.getLastName() == null) {
            lastName = "";

        } else {
            lastName = corporateUser.getLastName();
        }

        String name = firstName + ' ' + lastName;
        model.addAttribute("name", name);

        List<ServiceReqConfig> requestList = reqConfigService.getServiceReqConfs();
        model.addAttribute("serviceRequests", requestList);
        return "";
        }
    @ModelAttribute
    public String getCustmerAccounts(Model model, Principal principal) {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }



        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        if (corporateUser != null) {
            List<String> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(corporateUser.getCorporate().getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i.getAccountNumber()));


            model.addAttribute("accounts", accountList);
        }

        return "";
    }



}
