package longbridge.controllers;

import longbridge.services.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ayoade_farooq@yahoo.com on 5/3/2017.
 */
@RestController
public class Transfer
{


    private IntegrationService integrationService;

     @Autowired
    public Transfer(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }


    /*
    Fumction to get other accounts owned by a customer
     */
    @GetMapping("/dest/{accountId}/accounts")
 public List<String>  getDestinationAccounts(@PathVariable String accountId){


        List<String> accounts= new ArrayList<>();

        integrationService.fetchAccounts(integrationService.viewAccountDetails(accountId).getCustId())
                .stream()
                .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                .collect(Collectors.toList())
                .forEach(i-> accounts.add(i.getAccountNumber()));

              return accounts;


    }


    @GetMapping("/{accountId}/currency")

    public String getAccountCurrency(@PathVariable String accountId)
    {
        return integrationService.viewAccountDetails(accountId).getAcctCrncyCode();
    }







}
