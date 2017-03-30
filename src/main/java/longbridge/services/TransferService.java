package longbridge.services;

/**
<
 * The {@code TransferService} interface provides the methods for making transfers.
 * The various transfer types supported include own account transfer, coronation bank transfer, interbank transfer
 * and international transfer. The transfers can be carried via Finacle, NIP, NAPS and RTGS
 * @author Fortune

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
