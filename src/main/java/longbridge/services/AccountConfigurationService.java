package longbridge.services;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Fortune on 5/1/2017.
 */
public interface AccountConfigurationService {


      boolean isAccountHidden(String accountNumber);

    /**
     * Adds an account to a list of restricted accounts
     * @param accountRestrictionDTO contains details of the restriction
     */
    String addAccountRestriction(AccountRestrictionDTO accountRestrictionDTO)throws InternetBankingException;

    /**
     * updates the restriction on the account
     * @param accountRestrictionDTO contains details of the restriction
     */
    String updateAccountRestriction(AccountRestrictionDTO accountRestrictionDTO)throws InternetBankingException;


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
    String deleteAccountRestriction(Long id) throws InternetBankingException;

    /**
     * Adds an account to a list of restricted accounts
     * @param accountClassRestrictionDTO contains details of the restriction
     */
    String addAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException;

    /**
     * Updates the restriction on the account class
     * @param accountClassRestrictionDTO contains details of the restriction
     */
    String updateAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException;


    /**
     * Removes removes an account class from the list of restricted account classes
     * @param id the id
     */
    String deleteAccountClassRestriction(Long id) throws InternetBankingException;


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
    Iterable<AccountRestrictionDTO> getAccountRestrictions();



    /**
     * Returns a list of restricted account classes
     * @return restricted account classes
     */
    Iterable<AccountClassRestrictionDTO> getAccountClassRestrictions();


    /**
     * Returns a paginated list of restricted accounts
     * @return restricted accounts
     */
    Page<AccountRestrictionDTO> getAccountRestrictions(Pageable pageable);



    /**
     * Returns a paginated list of restricted account classes
     * @return restricted account classes
     */
    Page<AccountClassRestrictionDTO> getAccountClassRestrictions(Pageable pageable);


}
