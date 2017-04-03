package longbridge.services;

import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.Limit;


/**
 *The {@code CorporateService} interface provides the method managing corporate operations
 *These operations include adding corporate customer, setting corporate rules and transfer limits.
 */
public interface CorporateService {

    /**
     * Adds a new corporate customer to the system
     *
     * @param corporateCustomer the corporate customer
     */
    void addCorporate(Corporate corporateCustomer);

    /**
     * Deletes the given corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void deleteCorporate(Corporate corporateCustomer);

    /**
     * Updates the details of the corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void updateCorporateCustomer(Corporate corporateCustomer);

    /**
     * Returns a {@code CorporateCustomer} object that has the details of the customer
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
    Iterable<Corporate> getCorporateCustomers();

    /**
     * Sets the limit of transaction amount for the corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param limitValue        the limit
     */
    void setLimit(Corporate corporateCustomer, Limit limitValue);

    /**
     * Updates the limit of transaction amount for the specified corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param limitValue        the limit
     */
    void updateLimit(Corporate corporateCustomer,  Limit limitValue);

    /**
     * Returns the transaction limit set for the specified customer
     *
     * @param corporateCustomer the corporate customer
     * @return the limit set for the corporate customer
     */
    Iterable<Limit>  getLimit(Corporate corporateCustomer);

    /**
     * Deletes the transaction limit set for the specified customer
     *
     * @param corporateCustomer the corporate customer
     */
    void deleteLimit(Corporate corporateCustomer, Limit limit);


    /**
     * Sets a transaction limit for the specified corporate user
     *
     * @param corporateUser   the corporate user
     * @param limitValue      the limit
     */
    void setCorporateUserLimit(CorporateUser corporateUser, double limitValue);

    /**
     * Updates the set of the corporate user with the new limt
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
     * @param corporateCustomer the corporate customer
     * @param account           the account
     */
    void addAccount(Corporate corporateCustomer, Account account);

    /**
     * Adds a corporate user to a corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param corporateUser     the corporate user
     */
    void addCorporateUser(Corporate corporateCustomer, CorporateUser corporateUser);

    /**
     * Enables the corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void enableCorporate(Corporate corporateCustomer);

    /**
     * Disables the corporate customer
     *
     * @param corporateCustomer the corporate customer
     *
     */
    void disableCorporateCustomer(Corporate corporateCustomer);





}
