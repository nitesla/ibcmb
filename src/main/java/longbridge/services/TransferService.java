package longbridge.services;

/**
 * The {@code TransferService} interface provides the method to manage transfer and transaction for customer
 */
public interface TransferService {

    /**

     */
    void makeTransfer();

    /**
     *
     */
    void getTransaction();

    /**
     *
     */
    void getTransactions();

    /**
     *
     */
    void saveTransaction();

    /**
     *cancel transaction
     */
    void cancelTransaction();
/**
 * delete transaction
 */

    void deleteTransaction();
}
