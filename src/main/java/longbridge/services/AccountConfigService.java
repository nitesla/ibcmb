package longbridge.services;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by Fortune on 5/1/2017.
 */
public interface AccountConfigService {


    boolean isAccountHidden(String accountNumber);

    /**
     * Adds an account to a list of restricted accounts
     * @param accountRestrictionDTO contains details of the restriction
     */
    @PreAuthorize("hasAuthority('ADD_ACCT_RESTRICT')")
    String addAccountRestriction(AccountRestrictionDTO accountRestrictionDTO)throws InternetBankingException;

    /**
     * updates the restriction on the account
     * @param accountRestrictionDTO contains details of the restriction
     */
    @PreAuthorize("hasAuthority('UPDATE_ACCT_RESTRICT')")
    String updateAccountRestriction(AccountRestrictionDTO accountRestrictionDTO)throws InternetBankingException;


    /**
     * Returns the account restriction specified by the id
     * @param id the id
     * @return AccountRestrictionDTO
     */
    @PreAuthorize("hasAuthority('GET_ACCT_RESTRICT')")
    AccountRestrictionDTO getAccountRestriction(Long id);


    /**
     * Returns the account class restriction specified by the id
     * @param id the id
     * @return AccountClassRestrictionDTO
     */
    @PreAuthorize("hasAuthority('ADD_ACCT_CLASS_RESTRICT')")
    AccountClassRestrictionDTO getAccountClassRestriction(Long id);

    /**
     * Removes removes an account from the list of restricted accounts
     * @param id the id
     */
    @PreAuthorize("hasAuthority('DELETE_ACCT_RESTRICT')")
    String deleteAccountRestriction(Long id) throws InternetBankingException;

    /**
     * Adds an account to a list of restricted accounts
     * @param accountClassRestrictionDTO contains details of the restriction
     */
    @PreAuthorize("hasAuthority('ADD_ACCT_CLASS_RESTRICT')")
    String addAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException;

    /**
     * Updates the restriction on the account class
     * @param accountClassRestrictionDTO contains details of the restriction
     */
    @PreAuthorize("hasAuthority('UPDATE_ACCT_RESTRICT')")
    String updateAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException;


    /**
     * Removes removes an account class from the list of restricted account classes
     * @param id the id
     */
    @PreAuthorize("hasAuthority('DELETE_ACCT_CLASS_RESTRICT')")
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
    boolean isAccountSchemeTypeRestrictedForDebit(String accountClass);

    /**
     * Checks if the specified account class is restricted for credit
     * @param accountClass the account class
     * @return true if the account class cannot be credited
     */
    boolean isAccountSchemeTypeRestrictedForCredit(String accountClass);



    /**
     * Checks if the specified account class is restricted from being viewed by the user
     * @param accountClass the account class
     * @return true if the account class not be viewed from the platform
     */
    boolean isAccountSchemeTypeRestrictedForView(String accountClass);


    boolean isAccountRestrictedForViewFromUser(Long accountId, Long userId);

    boolean isAccountRestrictedForTransactionFromUser(Long accountId, Long userId);





    /**
     * Returns a paginated list of restricted accounts
     * @return restricted accounts
     */
    @PreAuthorize("hasAuthority('GET_ACCT_RESTRICTS')")
    Page<AccountRestrictionDTO> getAccountRestrictions(Pageable pageable);



    /**
     * Returns a paginated list of restricted account classes
     * @return restricted account classes
     */
    @PreAuthorize("hasAuthority('GET_ACCT_CLASS_RESTRICTS')")
    Page<AccountClassRestrictionDTO> getAccountClassRestrictions(Pageable pageable);



    boolean isAccountSchemeCodeRestrictedForDebit(String schemeCode);

    boolean isAccountSchemeCodeRestrictedForCredit(String schemeCode);

    boolean isAccountSchemeCodeRestrictedForView(String schemeCode);


}
