package longbridge.services;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;
import longbridge.models.TransferRequest;
import longbridge.utils.AccountStatement;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
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
    Collection<AccountInfo> fetchAccounts(String cifid);


    /** Fetches the {@link longbridge.utils.AccountStatement} of the account identified by
     * {@code accountId} for the period between {@code fromDate} and {@code toDate}
     * @param accountId the finacle acid of the Account
     * @param fromDate the Date from where to begin fetching the account statement
     * @param toDate the Date to stop fetching the account statement (inclusive)
     * @return {@code AccountStatement} object
     */
    AccountStatement getAccountStatements(String accountId, Date fromDate, Date toDate);


    /** Fetches the account Balance of the account specified by accountId
     * @param accountId accountId of the account
     * @return Map containing the available balance and ledger balance of the account
     */
    Map<String, BigDecimal> getBalance(String accountId);


    /** Initiates a transfer request to the relevant Transfer service.
     *
     */
    void makeTransfer(TransferRequest transferRequest);

    /**Fetches the account Name, Balance , Type from the account table specified by account Number
     *
     * @param acctNo account number of the account
     * @return Map containing the available details of the account
     */
    AccountDetails viewAccountDetails(String acctNo);

    /** Validate the account, with a valid account number, email and dob
     *
     * @param accNo account number of the account
     * @param email email of the account
     * @param dob date of birth specified in the account
     * @return Map that returns false of the details doesnt correspond or exist
     */
    Boolean isAccountValid(String accNo,String email,String dob);

    /** This fetches the full name of the customer connected to the
     * specified account number
     * @param accountNumber
     * @return The full name of the customer or null
     */
    String getAccountName(String accountNumber);

    /** This method make an API that fetches the daily transactions with accountNo
     *
     * @param acctNo account number
     * @return map containing the total transaction amount
     */
    BigDecimal getDailyDebitTransaction(String acctNo);

    /** This method make an API that fetches the Daily Account Limit with accountNo and Channel
     *
     * @param accNo the account number
     * @param channel the transaction channel
     * @return map containing the total transaction limit
     */
    BigDecimal getDailyAccountLimit(String accNo,String channel);
    
    /** This sends a request to synchronize the token attached to the user
     * with the specified username
     * @param username the username of the required user
     */
    void synchronizeToken(String username);
    
    
     /** This validates the token string passed in.
     * 
     * @param username The username of the user
     * @param tokenString the inputted token string
     * @return true if the validation was true and false if not
     */
    boolean performTokenValidation(String username, String tokenString);
    
}
