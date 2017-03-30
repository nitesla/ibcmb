package longbridge.services;

import longbridge.models.Account;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface IntegrationService {

    Iterable<Account> fetchAccount(String cifid);


        }
