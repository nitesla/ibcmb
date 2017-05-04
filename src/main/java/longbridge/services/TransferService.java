package longbridge.services;

import longbridge.dtos.TransferRequestDTO;
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

//    boolean makeLocalTransfer(TransferRequestDTO transferRequest);
    boolean makeTransfer(TransferRequestDTO transferRequest);

    TransferRequest getTransfer(Long id);

    Iterable<TransferRequest> getTransfers(User user);
    
    Page<TransferRequest> getTransfers(User user, Pageable pageDetails);

    boolean saveTransfer(TransferRequestDTO transferRequestDTO);

    void deleteTransfer(Long id);


}
