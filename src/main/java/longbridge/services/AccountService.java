package longbridge.services;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;
import longbridge.dtos.AccountDTO;
import longbridge.models.Account;
import longbridge.utils.statement.AccountStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The {@code AccountService} interface provides the methods for retrieving
 * a customer's account information
 * @author Fortunatus Ekenachi
 * @see Account
 * Created on 3/28/2017.
 */
public interface AccountService {


    boolean AddFIAccount(String customerId, AccountInfo account) ;

    /**
     * Adds the specified account to a customer's list of accounts
     *
     * @param customerId the customer's id
     * @param accountdto   the account to be added
     * @return an {@link Account} object
     */
    boolean AddAccount(String customerId, AccountDTO accountdto) ;


    String customizeAccount(Long id, String name) ;

    /**
     * Returns a particular account for a customer
     *
     * @param accId the account id
     * @return an {@link Account} object
     */
    AccountDTO getAccount(Long accId);


    List<Account> addAccounts(List<AccountDTO> accountDTOs);

    Account getAccountByCustomerId(String customerId);

    /**
     * Returns a list of accounts owned by a particular customer
     *
     * @param customerId the customer id of particular customer
     * @return a list of {@link Account}
     */
    List<AccountDTO> getAccounts(String customerId);

    List<Account> getCustomerAccounts(String customerId);

    Page<AccountDTO> getAccounts(String customerId, Pageable pageDetails);

    /**
     * Returns the details of an account balance.
     * The details are contained in a map  to represent the various account balances.
     * A customer's account can have 'Ledger balance', 'Available balance' and other types of balances.
     * These types of balances are stored as keys of the map, while the amounts are stored as the values of the map
     *
     * @param account the specified account
     * @return a map representing the types of account balances
     */
    Map<String, BigDecimal> getBalance(Account account);


    /**
     * Returns the details of financial transactions carried out a particular account within a specified period
     *
     * @param account  the particular account
     * @param fromDate the starting date
     * @param toDate   the ending date
     * @return a {@link AccountStatement} containing details of the transactions
     */
    AccountStatement getAccountStatements(Account account, Date fromDate, Date toDate,String transType);



    Iterable<Account> getCustomerAccounts(String customerId, String currencyCode);


    List<AccountInfo> getTransactionalAccounts(List<AccountInfo> accounts);


    List<Account> filterUnrestrictedAccounts(List<Account> accounts);

    boolean isAccountRestricted(AccountDetails account);

    List<Account> getAccountsForDebit(String customerId);
    Iterable<Account> getAccountsForDebit(List<Account> accounts);


    Iterable<Account> getAccountsForDebitForStatement(List<Account> accounts);

    List<AccountDTO> getAccountsForDebitAndCredit(String customerId);
    List<AccountDTO> getAccountsAndBalances(String customerId);


    List<AccountDTO> getAccountsAndBalances(List<Account> accounts);

     Iterable<Account> getAccountsForCredit(String customerId);

     Iterable<Account> getAccountsForCredit(List<Account> accounts);

    List<AccountDTO> getAccountsForReg(String customerId);
    List<Account> filterUnrestrictedAccountsForReg(List<Account> accounts);

    /**
     * Fetches the details of an {@link Account} using the
     * account number
     *
     * @param accountNumber The account number
     * @return an {@link Account} object containing the details of the account or null if the account
     * was not found
     */
    Account getAccountByAccountNumber(String accountNumber);

    /**
     * hides this account on the platform
     *
     * @param id
     * @return
     */
    String hideAccount(Long id) ;

    /**
     * unhides the customers acoount from the platform
     *
     * @param id
     * @return
     */
    String unhideAccount(Long id) ;

    /**
     * makes account the customers primary acoount
     *
     * @param id
     * @param customerId
     * @return
     */
    String makePrimaryAccount(Long id, String customerId) ;


    void deleteAccount(Long id);

    void validateAccount(String accountNumber) ;

    List<Account> getAccountByCifIdAndSchemeType(String cifId, String schemeType) ;

    Page<Account> getLoanAccounts(List<String> accountNumbers,Pageable pageable);
    List<Account> getLoanAccounts(List<String> accountNumbers);
    Page<Account> getFixedDepositAccounts(List<String> accountNumbers,Pageable pageable);
    List<Account> getFixedDepositAccounts(List<String> accountNumbers);


   }
