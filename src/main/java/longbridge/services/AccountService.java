package longbridge.services;

import longbridge.dtos.AccountDTO;
import longbridge.models.Account;
import longbridge.utils.AccountStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * The {@code AccountService} interface provides the methods for retrieving
 * a customer's account information
 * @author Fortunatus Ekenachi
 * @see Account
 * Created on 3/28/2017.
 */
public interface AccountService {

    /**
     * Adds the specified account to a customer's list of accounts
     * @param customerId the customer's id
     * @param account the account to be added
     * @return an {@link Account} object
     */
    boolean AddAccount(String customerId, Account account);



    boolean customizeAccount(Long id, String name);
    /**
     * Returns a particular account for a customer
     * @param accId the account id
     * @return an {@link Account} object
     */
    AccountDTO getAccount(Long accId);

    /**
     * Returns a list of accounts owned by a particular customer
     * @param customerId the customer id of particular customer
     * @return a list of {@link Account}
     */
    Iterable<AccountDTO> getAccounts(String customerId);
    
    Page<Account> getAccounts(String customerId, Pageable pageDetails);

    /**
     * Returns the details of an account balance.
     * The details are contained in a map  to represent the various account balances.
     * A customer's account can have 'Ledger balance', 'Available balance' and other types of balances.
     * These types of balances are stored as keys of the map, while the amounts are stored as the values of the map
     * @param account the specified account
     * @return a map representing the types of account balances
     */
    Map<String,BigDecimal> getBalance(Account account);


    /**
     * Returns the details of financial transactions carried out a particular account within a specified period
     * @param account the particular account
     * @param fromDate the starting date
     * @param toDate the ending date
     * @return a {@link AccountStatement} containing details of the transactions
     */
    AccountStatement getAccountStatements(Account account, Date fromDate, Date toDate);

    /**
     * Returns a list of accounts owned by a particular customer for debit
     * @param customerId the customer id of particular customer
     * @return a list of {@link Account}
     */
    Iterable<Account> getAccountsForDebit(String customerId);

    /**
     * Returns a list of accounts owned by a particular customer for credit
     * @param customerId the customer id of particular customer
     * @return a list of {@link Account}
     */
    Iterable<Account> getAccountsForCredit(String customerId);

}
