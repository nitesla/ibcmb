package longbridge.services;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface PaymentService {

    void makePayment();

    void getTransaction();

    void getTransactions();

    void saveTransaction();

    void cancelTransaction();

    void deleteTransaction();
}
