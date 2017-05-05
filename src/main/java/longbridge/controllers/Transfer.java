package longbridge.controllers;

import longbridge.models.Account;
import longbridge.services.AccountService;
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
    private AccountService accountService;

     @Autowired
    public Transfer(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }


    /*
    Fumction to get other accounts owned by a customer
     */
    @GetMapping("/dest/{accountId}/accounts")
 public List<String>  getDestinationAccounts(@PathVariable String accountId){


        List<String> accountList= new ArrayList<>();

//        integrationService.fetchAccounts(integrationService.viewAccountDetails(accountId).getCustId())
//                .stream()
//                .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
//                .collect(Collectors.toList())
//                .forEach(i-> accounts.add(i.getAccountNumber()));

        Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());
//                        .stream()
//                        .forEach(i -> accountList.add(i.getAccountNumber()));
        for(Account account: accounts){
            if(!accountId.equals(account.getAccountNumber())) {
                accountList.add(account.getAccountNumber());
            }
        }
              return accountList;


    }


    @GetMapping("/{accountId}/currency")

    public String getAccountCurrency(@PathVariable String accountId)
    {
        return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
    }







}
