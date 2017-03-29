package longbridge.services;

import longbridge.models.Account;
import longbridge.models.CorporateCustomer;
import longbridge.models.CorporateUser;


/**
 *The {@code CorporateService} interface provides the method managing corporate operations
 *These operations include adding corporate customer, setting corporate rules and transfer limits.
 */
public interface CorporateService {

    /**
     * Adds a corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void addCorporateCustomer(CorporateCustomer corporateCustomer);

    /**
     * Deletes a corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void deleteCorporateCustomer(CorporateCustomer corporateCustomer);

    /**
     * Updates the details of the corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void updateCorporateCustomer(CorporateCustomer corporateCustomer);

    /**
     * Returns a {@CorporateCustomer} object that has the details of the customer
     *
     * @param id the id of the corporate customer
     * @return the corporate customer
     */
    CorporateCustomer getCorporate(Long id);


    /**
     * Returns a list of all the corporate customers
     *
     * @return a list of the corporate customers
     */
    Iterable<CorporateCustomer> getCorporateCustomers();

    /**
     * Sets the limit of transaction amount for the corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param limitValue        the limit
     */
    void setLimit(CorporateCustomer corporateCustomer, double limitValue);

    /**
     * Updates the limit of transaction amount for the specified corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param limitValue        the limit
     */
    void updateLimit(CorporateCustomer corporateCustomer, double limitValue);

    /**
     * Returns the transaction limit set for the specified customer
     *
     * @param corporateCustomer the corporate customer
     */
    double getLimit(CorporateCustomer corporateCustomer);

    /**
     * Deletes the transaction limit set for the specified customer
     *
     * @param corporateCustomer the corporate customer
     */
    void deleteLimit(CorporateCustomer corporateCustomer);


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
     */
    double getCorporateUserLimit(CorporateUser corporateUser);

    /**
     * Deletes the transaction limit set for the corporate user
     *
     * @param corporateUser   the corporate user
     */
    void deleteCorporateUserLimit(CorporateUser corporateUser);

    /**
     * Adds an account to a corporate customer. This makes the added account to be available for transactions
     *
     * @param corporateCustomer the corporate customer
     * @param account           the account
     */
    void addAccount(CorporateCustomer corporateCustomer, Account account);

    /**
     * Adds a corporate user to a corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param corporateUser     the corporate user
     */
    void addCorporateUser(CorporateCustomer corporateCustomer, CorporateUser corporateUser);

    /**
     * Enables the corporate customer
     *
     * @param corporateCustomer the corporate customer
     */
    void enableCorporateCustomer(CorporateCustomer corporateCustomer);

    /**
     * Disables the corporate customer
     *
     * @param corporateCustomer the corporate customer
     *
     */
    void disableCorporateCustomer(CorporateCustomer corporateCustomer);

}
