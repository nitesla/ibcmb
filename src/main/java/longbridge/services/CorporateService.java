package longbridge.services;

import longbridge.models.*;


/**
 *The {@code CorporateService} interface provides the method managing corporate operations
 *These operations include adding corporate customer, setting corporate rules and transfer limits.
 */
public interface CorporateService {

    /**
     * Adds a new corporate customer to the system
     *
     * @param corporate the corporate customer
     */
    void addCorporate(Corporate corporate);

    /**
     * Deletes the given corporate customer
     *
     * @param corporateId the corporate customer's id
     */
    void deleteCorporate(Long corporateId);

    /**
     * Updates the details of the corporate customer
     *
     * @param corporate the corporate customer
     */
    void updateCorporate(Corporate corporate);

    /**
     * Returns a {@code Corporate} object that has the details of the customer
     *
     * @param id the id of the corporate customer
     * @return the corporate customer
     */
    Corporate getCorporate(Long id);

    /**
     * Returns a list of all the corporate customers
     *
     * @return a list of the corporate customers
     */
    Iterable<Corporate> getCorporates();

    /**
     * Sets the limit of transaction amount for the corporate customer
     *
     * @param corporate the corporate customer
     * @param limit        the corporate limit
     */
    void setLimit(Corporate corporate, CorpLimit limit);

    /**
     * Updates the limit of transaction amount for the specified corporate customer
     *
     * @param corporate the corporate customer
     * @param limit        the corporate limit
     */
    void updateLimit(Corporate corporate,  CorpLimit limit);

    /**
     * Returns the transaction limit set for the specified customer
     *
     * @param corporate the corporate customer
     * @return the limit set for the corporate customer
     */
    Iterable<CorpLimit>  getLimit(Corporate corporate);

    /**
     * Deletes the transaction limit set for the specified customer
     *
     * @param corporateId the corporate customer's id
     * @param  limit the corporate limit
     */
    void deleteLimit(Long corporateId, CorpLimit limit);


    /**
     * Sets a transaction limit for the specified corporate user
     *
     * @param corporateUser   the corporate user
     * @param limitValue      the limit
     */
    void setCorporateUserLimit(CorporateUser corporateUser, double limitValue);

    /**
     * Updates the corporate user with the new limt
     *
     * @param corporateUser   the corporate user
     * @param limitValue      the limit
     */
    void updateCorporateUserLimit(CorporateUser corporateUser, double limitValue);

    /**
     * Returns the transaction limit set for the specified corporate user
     *
     * @param corporateUser   the corporate user
     * @return  the corporate user limit
     */
    double getCorporateUserLimit(CorporateUser corporateUser);

    /**
     * Deletes the transaction limit set for the corporate user
     *
     * @param corporateUser   the corporate user
     */
    void deleteCorporateUserLimit(CorporateUser corporateUser);

    /**
     * Adds an account to a corporate customer.
     * This makes the added account to be available for transactions
     * @param corporate the corporate customer
     * @param account           the account
     */
    void addAccount(Corporate corporate, Account account);

    /**
     * Adds a corporate user to a corporate customer
     *
     * @param corporate the corporate customer
     * @param corporateUser     the corporate user
     */
    void addCorporateUser(Corporate corporate, CorporateUser corporateUser);

    /**
     * Enables the corporate customer
     *
     * @param corporate the corporate customer
     */
    void enableCorporate(Corporate corporate);

    /**
     * Disables the corporate customer
     *
     * @param corporate the corporate customer
     *
     */
    void disableCorporate(Corporate corporate);





}
