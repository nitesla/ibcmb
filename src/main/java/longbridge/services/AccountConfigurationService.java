package longbridge.services;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Fortune on 5/1/2017.
 */
public interface AccountConfigurationService {


    /**
     * Returns a list of accounts owned by a particular customer for debit
     *
     * @param customerId the customer id of particular customer
     * @return a list of {@link Account}
     */
    Iterable<Account> getAccountsForDebit(String customerId, String currency);

    /**
     * Returns a list of accounts owned by a particular customer for credit
     *
     * @param customerId the customer id of particular customer
     * @return a list of {@link Account}
     */
    Iterable<Account> getAccountsForCredit(String customerId, String currency);

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
     * Returns a list of restricted accounts
     * @return restricted accounts
     */
    Iterable<AccountRestrictionDTO> getAccountRestrictions();



    /**
     * Returns a list of restricted account classes
     * @return restricted account classes
     */
    Iterable<AccountClassRestrictionDTO> getdAccountClassRestrictions();


    /**
     * Returns a paginated list of restricted accounts
     * @return restricted accounts
     */
    Page<AccountRestrictionDTO> getAccountRestrictions(Pageable pageable);



    /**
     * Returns a paginated list of restricted account classes
     * @return restricted account classes
     */
    Page<AccountClassRestrictionDTO> getdAccountClassRestrictions(Pageable pageable);


}
