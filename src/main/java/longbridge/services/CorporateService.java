package longbridge.services;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.utils.Verifiable;
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

    Long countCorporate();

    /**
     * Adds a new corporate customer to the system
     *
     * @param corporateDTO the corporate customer
     *
     */
    String addCorporate(CorporateDTO corporateDTO) ;

//    /**
//     * Adds a new corporate customer to the system
//     *
//     * @param corporate the corporate customer
//     */
//    @PreAuthorize("hasAuthority('ADD_CORPORATE')")
//    String  addCorporate(CorporateDTO corporate, CorporateUserDTO corporateUser) ;


    String addCorporate(CorporateRequestDTO corporateRequestDTO) ;

//    void saveCorporateRequest(CorporateRequestDTO corporateRequestDTO) ;

    /**
     * Deletes the given corporate customer
     *
     * @param corporateId the corporate customer's id
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE')")
    String deleteCorporate(Long corporateId) ;

    /**
     * Updates the details of the corporate customer
     *
     * @param corporate the corporate customer
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE')")
    String updateCorporate(CorporateDTO corporate) ;


    /**
     * Returns a {@code Corporate} object that has the details of the customer
     *
     * @param id the id of the corporate customer
     * @return the corporate customer
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    CorporateDTO getCorporate(Long id);

    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Corporate getCorp(Long id);


    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Corporate getCorporateByCustomerId(String customerId);

    @PreAuthorize("hasAuthority('GET_CORPORATE')")
    Corporate getCorporateByCorporateId(String corporateId);
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
    Page<CorporateDTO> findCorporates(String pattern, Pageable pageDetails);

    boolean corporateExists(String customerId);


    /**
     * Changes the activation status of the corporate specified by the Id
     * @param id the Id of the corporate
     * @return a message is the status is changed successfully
     * @ if changing the status fails
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_STATUS')")
    String changeActivationStatus(Long id) ;

    /**
     * Adds an account to a corporate customer.
     * This makes the added account to be available for transactions
     * @param corporate the corporate customer
     * @param accountDTO           the account
     */
    @PreAuthorize("hasAuthority('ADD_CORPORATE_ACCOUNT')")
    String addAccount(Corporate corporate, AccountDTO accountDTO) ;

    /**
     * Returns a page list of accounts owned by the corporate identified by the corpId
     * @param corpId the corporate Id
     * @param pageDetails the page details for pagination
     * @return a list of accounts
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_ACCOUNT')")
   Page<AccountDTO> getAccounts(Long corpId, Pageable pageDetails);

    List<Account> getAccounts(Long corpId);

    /**
     * Adds a corporate transfer rule
     * @param transferRuleDTO the transfer rule object
     * @return a message if successfully added
     * @ if adding the rule fails
     */
    @PreAuthorize("hasAuthority('ADD_CORPORATE_RULE')")
   String addCorporateRule(CorpTransferRuleDTO transferRuleDTO) ;

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
     * @ if updating the rule fails
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_RULE')")
   String updateCorporateRule(CorpTransferRuleDTO transferRuleDTO) ;

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
     * @ if deleting the rule fails
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE_RULE')")
   String deleteCorporateRule(Long id) ;

    List<CorporateRoleDTO> getRoles(Long corpId);

    Page<CorporateRoleDTO> getRoles(Long corpId, Pageable pageable);


//    @PreAuthorize("hasAuthority('GET_TRANSFER_RULE')")
//    CorpTransRule getApplicableTransferRule(CorpTransRequest transferRequest);
//
//    @PreAuthorize("hasAuthority('GET_BULKTRANSFER_RULE')")
//    CorpTransRule getApplicableBulkTransferRule(BulkTransfer bulkTransfer);


    @PreAuthorize("hasAuthority('GET_TRANSFER_RULE')")
    CorpTransRule getApplicableTransferRule(TransRequest transferRequest);


    @PreAuthorize("hasAuthority('ADD_CORPORATE_ROLE')")
    String addCorporateRole(CorporateRoleDTO roleDTO) ;

    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_ROLE')")
    String updateCorporateRole(CorporateRoleDTO roleDTO) throws  InternetBankingException;

    void updateCorporateRole(CorporateRole updatedRole) ;

    void updateUsersWithoutAuthorizerRoleToInitiators(Set<CorporateUser> originalUsers, Set<CorporateUser> updatedUsers);

    @PreAuthorize("hasAuthority('GET_CORPORATE_ROLE')")
    CorporateRoleDTO getCorporateRole(Long id);

    @PreAuthorize("hasAuthority('GET_CORPORATE_ROLE')")
    Set<CorporateRoleDTO> getCorporateRoles(Long corporateId);

    @PreAuthorize("hasAuthority('DELETE_CORPORATE_ROLE')")
    String deleteCorporateRole(Long id) ;

    @Verifiable(operation = "ADD_CORPORATE_ACCOUNT", description = "Adding accounts to a Corporate Entity", type = UserType.OPERATIONS)
    String addCorporateAccounts(CorporateRequestDTO requestDTO);

    void addAccounts(CorporateRequestDTO requestDTO);

    AccountPermissionDTO.Permission getDefaultAccountPermission();

    void addAccounts(Corporate corporate);

    CorporateUser createUserOnEntrustAndSendCredentials(CorporateUser corporateUser);

    Page<CorpTransferRuleDTO> getCorporateRules(Long corpId, Pageable pageable);

    boolean corporateIdExists(String corporateId);

    String deleteCorporateAccount(CorporateRequestDTO corporateRequestDTO);

    boolean isTransactionPending(Long corpId, String accountNumber);
}
