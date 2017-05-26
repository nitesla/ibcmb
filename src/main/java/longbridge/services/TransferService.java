package longbridge.services;

import longbridge.dtos.TransferRequestDTO;

import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.TransferRequest;
import longbridge.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
<
 * The {@code TransferService} interface provides the methods for making transfers.
 * The various transfer types supported include own account transfer, coronation bank transfer, interbank transfer
 * and international transfer. The transfers can be carried via Finacle, NIP, NAPS and RTGS
 * @author Fortunatus Ekenachi
 * @see TransferRequest

 */
public interface TransferService {


    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    TransferRequestDTO makeTransfer(TransferRequestDTO transferRequest) throws TransferException;


    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    TransferRequest getTransfer(Long id);

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    Iterable<TransferRequest> getTransfers(User user);

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    Page<TransferRequest> getTransfers(User user, Pageable pageDetails);

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    boolean saveTransfer(TransferRequestDTO transferRequestDTO) throws TransferException;


    void deleteTransfer(Long id) throws InternetBankingException;
    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void  validateBalance(TransferRequestDTO transferRequest) throws InternetBankingTransferException;
    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void validateTransfer(TransferRequestDTO transferRequest) throws InternetBankingTransferException;


}
