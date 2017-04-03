package longbridge.services;

import longbridge.models.Account;
import longbridge.models.Transfer;
import longbridge.utils.AccountStatement;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * The {@IntegrationService} interface provides the methods for accessing the various integration service
 */
public interface IntegrationService {

    /**
     * Returns all the accounts of a customer
     * @param cifid the customer's id
     * @return  a list of accounts
     */
    Iterable<Account> fetchAccounts(String cifid);


    /** Fetches the {@link longbridge.utils.AccountStatement} of the account identified by
     * {@code accountId} for the period between {@code fromDate} and {@code toDate}
     * @param accountId the finacle acid of the Account
     * @param fromDate the Date from where to begin fetching the account statement
     * @param toDate the Date to stop fetching the account statement (inclusive)
     * @return {@code AccountStatement} object
     */
    AccountStatement getAccountStatements(String accountId, LocalDate fromDate, LocalDate toDate);


    /** Fetches the account Balance of the account specified by accountId
     * @param accountId accountId of the account
     * @return Map containing the available balance and ledger balance of the account
     */
    Map<String, BigDecimal> getBalance(String accountId);


    /** Initiates a transfer request to the relevant Transfer service.
     *
     */
    void makeTransfer(Transfer transfer);

}
