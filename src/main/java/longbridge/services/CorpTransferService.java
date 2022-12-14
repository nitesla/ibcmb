package longbridge.services;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.CorpTransReqEntry;
import longbridge.models.CorpTransRequest;
import longbridge.models.CorpTransferAuth;
import longbridge.models.TransRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by Fortune on 5/19/2017.
 */
public interface CorpTransferService {


    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    CorpTransRequest getTransfer(Long id);

    CorpTransferRequestDTO entityToDTO(CorpTransRequest corpTransRequest);

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    CorpTransferRequestDTO saveTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws TransferException;

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void validateTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException;

    Object addTransferRequest(CorpTransferRequestDTO corpTransferRequestDTO) ;

    CorpTransferRequestDTO makeNeftBulkTransfer(CorpTransferRequestDTO corpTransferRequestDTO) ;


    Page<CorpTransRequest> getTransferRequests(Pageable pageDetails);
    Page<CorpTransferRequestDTO> getTransferRequest(Pageable pageDetails);

    int countPendingRequest();

    CorpTransferAuth getAuthorizations(CorpTransRequest transRequest);

    Page<CorpTransRequest> getCompletedTransfers(Pageable pageDetails);
    Page<CorpTransferRequestDTO> getCompletedTransfer(Pageable pageDetails);

    Page<CorpTransRequest> getCompletedTransfers(String pattern,Pageable pageDetails);
    Page<CorpTransferRequestDTO> getCompletedTransfer(String pattern,Pageable pageDetails);

    String addAuthorization(CorpTransReqEntry transReqEntry);

    boolean userCanAuthorize(TransRequest transRequest);

    boolean validateDirectDebitAccountBalance(CorpTransferRequestDTO dto);
}
