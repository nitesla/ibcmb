package longbridge.services.implementations;

import longbridge.models.Transfer;
import longbridge.models.User;
import longbridge.services.TransferService;

/**
 * Created by Fortune on 3/30/2017.
 */
public class TransferServiceImpl implements TransferService {
    @Override
    public void makeTransfer(Transfer transfer) {

    }

    @Override
    public Transfer getTransaction(Long id) {
        return null;
    }

    @Override
    public Iterable<Transfer> getTransactions(User user) {
        return null;
    }

    @Override
    public Iterable<Transfer> getTransactions() {
        return null;
    }

    @Override
    public void saveTransaction(Transfer transfer) {

    }

    @Override
    public void cancelTransaction(Transfer transfer) {

    }

    @Override
    public void deleteTransaction(Long id) {

    }
}
