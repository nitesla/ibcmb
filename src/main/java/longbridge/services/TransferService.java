package longbridge.services;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.DirectDebit;
import longbridge.models.NeftTransfer;
import longbridge.models.TransRequest;
import longbridge.models.User;
import longbridge.utils.TransferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
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
    TransferRequestDTO saveTransfer(TransferRequestDTO transferRequestDTO) throws TransferException;
    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void validateTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException;

    Page<TransRequest> getCompletedTransfers(Pageable pageDetails);
    Page<TransferRequestDTO> getCompletedTransfer(Pageable pageDetails);

    Page<TransRequest> getCompletedTransfers(String pattern, Pageable pageDetails);
    Page<TransferRequestDTO> getCompletedTransfer(String pattern, Pageable pageDetails);

    List<TransRequest> getLastTenTransactionsForAccount(String s);

    TransRequest updateTransferStatus(TransferRequestDTO transferRequestDTO);
    boolean validateDirectDebitTransfer(TransferRequestDTO dto);
    TransRequest makeBackgroundTransfer(TransferRequestDTO transferRequest, DirectDebit directDebit);

    @PreAuthorize("hasAuthority('VIEW_TRANSACTIONS')")
    Page<TransferRequestDTO> getTransferReviews(TransferType transfertype, String accountNumber, Date startDate, Date endDate, Pageable pageDetails);



}
