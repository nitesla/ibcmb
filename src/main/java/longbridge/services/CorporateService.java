package longbridge.services;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;


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
    @PreAuthorize("hasAuthority('ADD_CORPORATE')")
    String  addCorporate(CorporateDTO corporate, CorporateUserDTO corporateUser) throws InternetBankingException;

    /**
     * Deletes the given corporate customer
     *
     * @param corporateId the corporate customer's id
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE')")
    String deleteCorporate(Long corporateId) throws InternetBankingException;

    /**
     * Updates the details of the corporate customer
     *
     * @param corporate the corporate customer
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE')")
    String updateCorporate(CorporateDTO corporate) throws InternetBankingException;

    /**
     * Returns a {@code Corporate} object that has the details of the customer
     *
     * @param id the id of the corporate customer
     * @return the corporate customer
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    CorporateDTO getCorporate(Long id);


    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Corporate getCorporateByCustomerId(String customerId);
    /**
     * Returns a list of all the corporate customers
     *
     * @return a list of the corporate customers
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Iterable<CorporateDTO> getCorporates();

    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Page<CorporateDTO> getCorporates(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Page<CorporateDTO> findCorporates(String pattern,Pageable pageDetails);
    /**
     * Sets the limit of transaction amount for the corporate customer
     *
     * @param corporate the corporate customer
     * @param limit        the corporate limit
     */


    @PreAuthorize("hasAuthority('SET_CORPORATE_LIMIT')")
    void setLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException;

    public boolean corporateExists(String customerId);

    /**
     * Updates the limit of transaction amount for the specified corporate customer
     *
     * @param corporate the corporate customer
     * @param limit        the corporate limit
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_LIMIT')")
    void updateLimit(Corporate corporate,  CorpLimit limit) throws InternetBankingException;


    /**
     * Returns the transaction limit set for the specified customer
     *
     * @param corporate the corporate customer
     * @return the limit set for the corporate customer
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_LIMIT')")
    List<CorpLimit>  getLimit(Corporate corporate);

    /**
     * Deletes the transaction limit set for the specified customer
     *
     * @param corporateId the corporate customer's id
     * @param  limit the corporate limit
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE_LIMIT')")
    void deleteLimit(Long corporateId, CorpLimit limit) throws InternetBankingException;


    /**
     * Changes the activation status of the corporate specified by the Id
     * @param id the Id of the corporate
     * @return a message is the status is changed successfully
     * @throws InternetBankingException if changing the status fails
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_STATUS')")
    String changeActivationStatus(Long id) throws InternetBankingException;

    /**
     * Adds an account to a corporate customer.
     * This makes the added account to be available for transactions
     * @param corporate the corporate customer
     * @param accountDTO           the account
     */
    @PreAuthorize("hasAuthority('ADD_CORPORATE_ACCOUNT')")
    String addAccount(Corporate corporate, AccountDTO accountDTO) throws InternetBankingException;

    /**
     * Returns a page list of accounts owned by the corporate identified by the corpId
     * @param corpId the corporate Id
     * @param pageDetails the page details for pagination
     * @return a list of accounts
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_ACCOUNT')")
   Page<AccountDTO> getAccounts(Long corpId, Pageable pageDetails);

    /**
     * Adds a corporate transfer rule
     * @param transferRuleDTO the transfer rule object
     * @return a message if successfully added
     * @throws InternetBankingException if adding the rule fails
     */
    @PreAuthorize("hasAuthority('ADD_CORPORATE_RULE')")
   String addCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException;

    /**
     * Returns a corporate transfer rule
     * @param id the id
     * @return corporate transfer rule
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_RULE')")
   CorpTransferRuleDTO getCorporateRule(Long id);

    /**
     * updates the corporate transfer rule
     * @param transferRuleDTO the transfer rule object
     * @return a message if successfully updated
     * @throws InternetBankingException if updating the rule fails
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_RULE')")
   String updateCorporateRule(CorpTransferRuleDTO transferRuleDTO) throws InternetBankingException;

    /**
     * Returns a list of corporate transfer rules
     * @return a list of rules
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_RULE')")
    List<CorpTransferRuleDTO> getCorporateRules(Long corpId);

    /**
     * Returns a list of corporate transfer rules for the corporate specified by the corpId
     * @param corpId the Id of the corporate entity
     * @return a list of corporate rules
     */
//    @PreAuthorize("hasAuthority('GET_CORPORATE_RULE')")
//   Page<CorpTransferRuleDTO> getCorporateRules(Long corpId, Pageable pageable);

    /**
     * Deletes the corporate transfer rule
     * @param id the Id of the corporate rule
     * @return a message if successfully deleted
     * @throws InternetBankingException if deleting the rule fails
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE_RULE')")
   String deleteCorporateRule(Long id) throws InternetBankingException;

    List<CorporateRoleDTO> getRoles(Long corpId);

    Page<CorporateRoleDTO> getRoles(Long corpId, Pageable pageable);

    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    List<CorporateRole> getQualifiedRoles(CorpTransRequest transferRequest);

    @PreAuthorize("hasAuthority('GET_TRANSFER_RULE')")
    CorpTransRule getApplicableTransferRule(CorpTransRequest transferRequest);


    @PreAuthorize("hasAuthority('ADD_CORPORATE_ROLE')")
    String addCorporateRole(CorporateRoleDTO roleDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_ROLE')")
    String updateCorporateRole(CorporateRoleDTO roleDTO) throws  InternetBankingException;

    @PreAuthorize("hasAuthority('GET_CORPORATE_ROLE')")
    CorporateRoleDTO getCorporateRole(Long id);

    @PreAuthorize("hasAuthority('GET_CORPORATE_ROLE')")
    Set<CorporateRoleDTO> getCorporateRoles(Long corporateId);

    @PreAuthorize("hasAuthority('DELETE_CORPORATE_ROLE')")
    String deleteCorporateRole(Long id) throws InternetBankingException;

    void addAccounts(Corporate corporate);

    void createUserOnEntrustAndSendCredentials(CorporateUser corporateUser);

    Page<CorpTransferRuleDTO> getCorporateRules(Long corpId, Pageable pageable);


}
