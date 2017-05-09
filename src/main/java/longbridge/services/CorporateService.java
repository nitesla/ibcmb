package longbridge.services;

import java.util.List;

import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.CorporateDTO;
import longbridge.models.Account;
import longbridge.models.CorpLimit;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;


/**
 *The {@code CorporateService} interface provides the method managing corporate operations
 *These operations include adding corporate customer, setting corporate rules and transfer limits.
 */
public interface CorporateService{

    /**
     * Adds a new corporate customer to the system
     *
     * @param corporate the corporate customer
     */
    String  addCorporate(CorporateDTO corporate) throws InternetBankingException;

    /**
     * Deletes the given corporate customer
     *
     * @param corporateId the corporate customer's id
     */
    String deleteCorporate(Long corporateId) throws InternetBankingException;

    /**
     * Updates the details of the corporate customer
     *
     * @param corporate the corporate customer
     */
    String updateCorporate(CorporateDTO corporate) throws InternetBankingException;

    /**
     * Returns a {@code Corporate} object that has the details of the customer
     *
     * @param id the id of the corporate customer
     * @return the corporate customer
     */
    CorporateDTO getCorporate(Long id);

    /**
     * Returns a list of all the corporate customers
     *
     * @return a list of the corporate customers
     */
    Iterable<CorporateDTO> getCorporates();
    
    
    Page<CorporateDTO> getCorporates(Pageable pageDetails);

    /**
     * Sets the limit of transaction amount for the corporate customer
     *
     * @param corporate the corporate customer
     * @param limit        the corporate limit
     */
    void setLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException;

    /**
     * Updates the limit of transaction amount for the specified corporate customer
     *
     * @param corporate the corporate customer
     * @param limit        the corporate limit
     */
    void updateLimit(Corporate corporate,  CorpLimit limit) throws InternetBankingException;

    /**
     * Returns the transaction limit set for the specified customer
     *
     * @param corporate the corporate customer
     * @return the limit set for the corporate customer
     */
    List<CorpLimit>  getLimit(Corporate corporate);

    /**
     * Deletes the transaction limit set for the specified customer
     *
     * @param corporateId the corporate customer's id
     * @param  limit the corporate limit
     */
    void deleteLimit(Long corporateId, CorpLimit limit) throws InternetBankingException;



    /**
     * Adds an account to a corporate customer.
     * This makes the added account to be available for transactions
     * @param corporate the corporate customer
     * @param account           the account
     */
    boolean addAccount(Corporate corporate, Account account) throws InternetBankingException;

    /**
     * Adds a corporate user to a corporate customer
     *
     * @param corporate the corporate customer
     * @param corporateUser     the corporate user
     */
    String addCorporateUser(Corporate corporate, CorporateUser corporateUser) throws InternetBankingException;

    /**
     * Enables the corporate customer
     *
     * @param corporate the corporate customer
     */
    void enableCorporate(Corporate corporate) throws InternetBankingException;

    /**
     * Disables the corporate customer
     *
     * @param corporate the corporate customer
     *
     */
    void disableCorporate(Corporate corporate) throws InternetBankingException;

}
