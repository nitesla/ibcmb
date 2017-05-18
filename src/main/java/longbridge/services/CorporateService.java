package longbridge.services;

import longbridge.dtos.AccountDTO;
import longbridge.dtos.CorpTransferRuleDTO;
import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpLimit;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


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



    Corporate getCorporateByCustomerId(String customerId);
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
     * Changes the activation status of the corporate specified by the Id
     * @param id the Id of the corporate
     * @return a message is the status is changed successfully
     * @throws InternetBankingException if changing the status fails
     */
    String changeActivationStatus(Long id) throws InternetBankingException;


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
     * @param accountDTO           the account
     */
    String addAccount(Corporate corporate, AccountDTO accountDTO) throws InternetBankingException;

    /**
     * Adds a corporate user to a corporate customer
     *
     * @param corporate the corporate customer
     * @param corporateUser     the corporate user
     */
    String addCorporateUser(Corporate corporate, CorporateUser corporateUser) throws InternetBankingException;

    /**
     * Returns a page list of accounts owned by the corporate identified by the corpId
     * @param corpId the corporate Id
     * @param pageDetails the page details for pagination
     * @return a list of accounts
     */
   Page<AccountDTO> getAccounts(Long corpId, Pageable pageDetails);

    /**
     * Adds a corporate transfer rule
     * @param transferRuleDTO the transfer rule object
     * @return a message if successfully added
     * @throws InternetBankingException if adding the rule fails
     */
   String addCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException;

    /**
     * Returns a corporate transfer rule
     * @param id the id
     * @return corporate transfer rule
     */
   CorpTransferRuleDTO getCorporateRule(Long id);

    /**
     * updates the corporate transfer rule
     * @param transferRuleDTO the transfer rule object
     * @return a message if successfully updated
     * @throws InternetBankingException if updating the rule fails
     */
   String updateCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException;

    /**
     * Returns a list of corporate transfer rules
     * @return a list of rules
     */
   List<CorpTransferRuleDTO> getCorporateRules();

    /**
     * Returns a list of corporate transfer rules for the corporate specified by the corpId
     * @param corpId the Id of the corporate entity
     * @return a list of corporate rules
     */
   List<CorpTransferRuleDTO> getCorporateRules(Long corpId);

    /**
     * Deletes the corporate transfer rule
     * @param id the Id of the corporate rule
     * @return a message if successfully deleted
     * @throws InternetBankingException if deleting the rule fails
     */
   String deleteCorporateRule(Long id) throws InternetBankingException;

List<CorporateUserDTO> getAuthorizers(Long corpId);

}
