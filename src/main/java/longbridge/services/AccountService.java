package longbridge.services;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.AccountRestrictionDTO;
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

    Iterable<Account> getCustomerAccounts(String customerId);
    
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

    /**  Fetches the details of an {@link Account} using the
     * account number
     * @param accountNumber The account number
     * @return an {@link Account} object containing the details of the account or null if the account
     * was not found
     */
    Account getAccountByAccountNumber(String accountNumber);

    /**
     * hides this account on the platform
     * @param id
     * @return
     */
    boolean hideAccount(Long id);

    /**
     * unhides the customers acoount from the platform
     * @param id
     * @return
     */
    boolean unhideAccount(Long id);

    /**
     * makes account the customers primary acoount
     * @param id
     * @param accounts
     * @return
     */
    boolean makePrimaryAccount(Long id, Iterable<Account> accounts);

    /**
     * Adds an account to a list of restricted accounts
     * @param accountRestrictionDTO contains details of the restriction
     */
    void addAccountRestriction(AccountRestrictionDTO accountRestrictionDTO)throws Exception;

    /**
     * updates the restriction on the account
     * @param accountRestrictionDTO contains details of the restriction
     */
    void updateAccountRestriction(AccountRestrictionDTO accountRestrictionDTO)throws Exception;


    /**
     * Returns the account restriction specified by the id
     * @param id the id
     * @return AccountRestrictionDTO
     */
    AccountRestrictionDTO getAccountRestriction(Long id);


    /**
     * Returns the account class restriction specified by the id
     * @param id the id
     * @return AccountClassRestrictionDTO
     */
    AccountClassRestrictionDTO getAccountClassRestriction(Long id);

    /**
     * Removes removes an account from the list of restricted accounts
     * @param id the id
     */
    void removeAccountRestriction(Long id);

    /**
     * Adds an account to a list of restricted accounts
     * @param accountClassRestrictionDTO contains details of the restriction
     */
    void addAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws Exception;

    /**
     * Updates the restriction on the account class
     * @param accountClassRestrictionDTO contains details of the restriction
     */
    void updateAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws Exception;


    /**
     * Removes removes an account class from the list of restricted account classes
     * @param id the id
     */
    void removeAccountClassRestriction(Long id);


    /**
     * Checks if the specified account is restricted for debit
     * @param accountNumber the account number
     * @return true if the account cannot be debited
     */
    boolean isAccountRestrictedForDebit(String accountNumber);

    /**
     * Checks if the specified account is restricted for credit
     * @param accountNumber the account number
     * @return true if the account cannot be credited
     */
    boolean isAccountRestrictedForCredit(String accountNumber);

    /**
     * Checks if the specified account is restricted for both debit and credit
     * @param accountNumber the account number
     * @return true if the account cannot be debited or credited
     */
    boolean isAccountRestrictedForDebitAndCredit(String accountNumber);


    /**
     * Checks if the specified account is restricted from being viewed by the user
     * @param accountNumber the account number
     * @return true if the account not be viewed from the platform
     */
    boolean isAccountRestrictedForView(String accountNumber);


    /**
     * Checks if the specified account class is restricted for debit
     * @param accountClass the account class
     * @return true if the account class cannot be debited
     */
    boolean isAccountClassRestrictedForDebit(String accountClass);

    /**
     * Checks if the specified account class is restricted for credit
     * @param accountClass the account class
     * @return true if the account class cannot be credited
     */
    boolean isAccountClassRestrictedForCredit(String accountClass);

    /**
     * Checks if the specified account class is restricted for both debit and credit
     * @param accountClass the account class
     * @return true if the account class cannot be debited or credited
     */
    boolean isAccountClassRestrictedForDebitAndCredit(String accountClass);


    /**
     * Checks if the specified account class is restricted from being viewed by the user
     * @param accountClass the account class
     * @return true if the account class not be viewed from the platform
     */
    boolean isAccountClassRestrictedForView(String accountClass);


    /**
     * Returns a list of restricted accounts
     * @return restricted accounts
     */
    Iterable<AccountRestrictionDTO> getRestrictedAccounts();



    /**
     * Returns a list of restricted account classes
     * @return restricted account classes
     */
    Iterable<AccountClassRestrictionDTO> getRestrictedAccountClasses();


    /**
     * Returns a paginated list of restricted accounts
     * @return restricted accounts
     */
    Page<AccountRestrictionDTO> getRestrictedAccounts(Pageable pageable);



    /**
     * Returns a paginated list of restricted account classes
     * @return restricted account classes
     */
    Page<AccountClassRestrictionDTO> getRestrictedAccountClasses(Pageable pageable);
}
