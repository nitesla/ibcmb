package longbridge.controllers;

/**
 * Created by Longbridge on 6/11/2017.
 */

import longbridge.api.AccountDetails;
import longbridge.models.Account;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BatchJobController {
    @Autowired
    private AccountService accountService;
    public void updatAllAccounts(){
//        accountService.updateAccountDetails();
    }
}
