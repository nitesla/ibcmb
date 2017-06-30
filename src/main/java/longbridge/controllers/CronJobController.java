package longbridge.controllers;

import longbridge.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Longbridge on 6/28/2017.
 */
@Controller
public class CronJobController {
    @Autowired
    private AccountService accountService;
    public void updatAllAccounts(){
        accountService.updateAccountDetails();
    }
}
