package longbridge.services;

import longbridge.models.Account;
import longbridge.models.CorporateCustomer;
import longbridge.models.CorporateUser;
import longbridge.models.TransactionType;

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
     * @param transactionType   the transaction type
     * @param limitValue        the limit
     */
    void setLimit(CorporateCustomer corporateCustomer, TransactionType transactionType, double limitValue);

    /**
     * Updates the limit of transaction amount for the specified corporate customer
     *
     * @param corporateCustomer the corporate customer
     * @param transactionType   the transaction type
     * @param limitValue        the limit
     */
    void updateLimit(CorporateCustomer corporateCustomer, TransactionType transactionType, double limitValue);

    /**
     * Returns the transaction limit set for the specified customer
     *
     * @param corporateCustomer the corporate customer
     * @param transactionType   the transaction type
     */
    double getLimit(CorporateCustomer corporateCustomer, TransactionType transactionType);

    /**
     * Deletes the transaction limit set for the specified customer
     *
     * @param corporateCustomer the corporate customer
     * @param transactionType   the transaction type
     */
    void deleteLimit(CorporateCustomer corporateCustomer, TransactionType transactionType);


    /**
     * Sets a transaction limit for the specified corporate user
     *
     * @param corporateUser   the corporate user
     * @param transactionType the transaction type
     * @param limitValue      the limit
     */
    void setCorporateUserLimit(CorporateUser corporateUser, TransactionType transactionType, double limitValue);

    /**
     * Updates the set of the corporate user with the new limt
     *
     * @param corporateUser   the corporate user
     * @param transactionType the transaction type
     * @param limitValue      the limit
     */
    void updateCorporateUserLimit(CorporateUser corporateUser, TransactionType transactionType, double limitValue);

    /**
     * Returns the transaction limit set for the specified corporate user
     *
     * @param corporateUser   the corporate user
     * @param transactionType the transaction type
     */
    double getCorporateUserLimit(CorporateUser corporateUser, TransactionType transactionType);

    /**
     * Deletes the transaction limit set for the corporate user
     *
     * @param corporateUser   the corporate user
     * @param transactionType the transaction type
     */
    void deleteCorporateUserLimit(CorporateUser corporateUser, TransactionType transactionType);

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

<<<<<<< HEAD
    void deleteCorporate();

    void updateCorporate();

    void getCorporate();

    void getCorporates();

    /**
     *
     * @param transactionType
     * @param limitValue
     */
    void setLimit(TransactionType transactionType, double limitValue);

    void updateLimit();

    void getLimit();

    void deleteLimit();

    void setcorporateLimit(CorporateUser corporateUser, double limit);

    void setCorporateUserLimit(CorporateUser corporateUser, double limit);

    void setUserLimit();

    void updateUserLimits();

    void getUserLimits();

    void deleteUserLimits();

    void setTransactionLimit(TransactionType transactionType, double limit, String currency);

    void updateTransactionLimits();

    void getTransactionLimits();

    void deleteTransactionLimits();

    void addAccount();

    void addUser();

    void disableUser();
=======
>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae
}
