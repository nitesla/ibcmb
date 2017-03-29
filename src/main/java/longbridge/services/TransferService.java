package longbridge.services;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface TransferService {

    void makeTransfer();

    void getTransaction();

    void getTransactions();

    void saveTransaction();

    void cancelTransaction();

    void deleteTransaction();
}
