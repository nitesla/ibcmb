package longbridge.services;

import longbridge.models.Account;
import longbridge.utils.AccountStatement;

import java.util.Date;
import java.util.Map;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface IntegrationService {

    Iterable<Account> fetchAccount(String cifid);


    /** Fetches the {@link longbridge.utils.AccountStatement} of the account identified by
     * {@code accountId} for the period between {@code fromDate} and {@code toDate}
     * @param accountId the finacle acid of the Account
     * @param fromDate the Date from where to begin fetching the account statement
     * @param toDate the Date to stop fetching the account statement (inclusive)
     * @return {@code AccountStatement} object
     */
    AccountStatement getAccountStatement(String accountId, Date fromDate, Date toDate);


    /** Fetches the account Balance of the account specified by accountId
     *
     * @param accountId accountId of the account
     * @return Map containing the available balance and ledger balance of the account
     */
    Map<String, String> getBalance(String accountId);


}
