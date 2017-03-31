package longbridge.services;

import longbridge.models.Transfer;
import longbridge.models.User;

/**
<
 * The {@code TransferService} interface provides the methods for making transfers.
 * The various transfer types supported include own account transfer, coronation bank transfer, interbank transfer
 * and international transfer. The transfers can be carried via Finacle, NIP, NAPS and RTGS
 * @author Fortunatus Ekenachi
 * @see Transfer

 */
public interface TransferService {

    void makeTransfer(Transfer transfer);

    Transfer getTransfer(Long id);

    Iterable<Transfer> getTransfers(User user);

    Iterable<Transfer> getTransfers();

    void saveTransfer(Transfer transfer);

    void deleteTransfer(Long id);
}
