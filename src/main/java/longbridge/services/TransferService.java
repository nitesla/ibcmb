package longbridge.services;

import longbridge.dtos.TransferRequestDTO;

import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.TransRequest;
import longbridge.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
<
 * The {@code TransferService} interface provides the methods for making transfers.
 * The various transfer types supported include own account transfer, coronation bank transfer, interbank transfer
 * and international transfer. The transfers can be carried via Finacle, NIP, NAPS and RTGS
 * @author Fortunatus Ekenachi
 * @see TransRequest

 */
public interface TransferService {


    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    TransferRequestDTO makeTransfer(TransferRequestDTO transferRequest) throws TransferException;

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    TransRequest getTransfer(Long id);

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    Iterable<TransRequest> getTransfers(User user);

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    Page<TransRequest> getTransfers(User user, Pageable pageDetails);

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    boolean saveTransfer(TransferRequestDTO transferRequestDTO) throws TransferException;
    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void validateTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException;

    List<TransRequest> getLastTenTransactionsForAccount(String s);


}
