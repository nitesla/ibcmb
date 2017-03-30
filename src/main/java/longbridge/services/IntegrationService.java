package longbridge.services;

import longbridge.models.Account;

/**
 * The {@IntegrationService} interface provides the methods for accessing the various integration service
 */
public interface IntegrationService {

    /**
     * Returns all the accounts of a customer
     * @param cifid the customer's id
     * @return  a list of accounts
     */
    Iterable<Account> fetchAccount(String cifid);


        }
