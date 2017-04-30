package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.TransferRequest;
import longbridge.models.User;

/**
<
 * The {@code TransferService} interface provides the methods for making transfers.
 * The various transfer types supported include own account transfer, coronation bank transfer, interbank transfer
 * and international transfer. The transfers can be carried via Finacle, NIP, NAPS and RTGS
 * @author Fortunatus Ekenachi
 * @see TransferRequest

 */
public interface TransferService {

    void makeTransfer(TransferRequest transferRequest);

    TransferRequest getTransfer(Long id);

    Iterable<TransferRequest> getTransfers(User user);
    
    Page<TransferRequest> getTransfers(User user, Pageable pageDetails);

    void saveTransfer(TransferRequest transferRequest);

    void deleteTransfer(Long id);


}
