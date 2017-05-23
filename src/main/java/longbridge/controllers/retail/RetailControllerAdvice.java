package longbridge.controllers.retail;

import longbridge.models.Account;
import longbridge.models.RetailUser;
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
 * Created by Wunmi Sowunmi on 28/04/2017.
 */


@ControllerAdvice(basePackages = {"longbridge.controllers.retail"})
public class RetailControllerAdvice {

    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private ServiceReqConfigService reqConfigService;

    @Autowired
    public RetailControllerAdvice(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, ServiceReqConfigService reqConfigService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.reqConfigService = reqConfigService;
    }

    @ModelAttribute
    public String globalAttributes(Model model, Principal principal) {
        String greeting = "";

        if (principal.getName() == null) {
            return "redirect:/login/retail";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        model.addAttribute("bvn", user.getBvn());

        model.addAttribute("lastLogin", user.getLastLoginDate());

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good morning, ";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good afternoon, ";
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            greeting = "Good evening, ";
        }
        model.addAttribute("greeting", greeting);


        String firstName = user.getFirstName();
        String lastName = "";
        if (user.getLastName() == null) {
            lastName = "";

        } else {
            lastName = user.getLastName();
        }

        String name = firstName + ' ' + lastName;
        model.addAttribute("name", name);

        List<ServiceReqConfig> requestList = reqConfigService.getServiceReqConfs();
        model.addAttribute("serviceRequests", requestList);

        //System.out.println( new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) );
        return "";
    }


    @ModelAttribute
    public String getCustmerAccounts(Model model, Principal principal) {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/retail";
        }



        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user != null) {
            List<String> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i.getAccountNumber()));


            model.addAttribute("accounts", accountList);
        }

        return "";
    }



}