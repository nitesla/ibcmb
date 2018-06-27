package longbridge.services;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingException;
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

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    CorpTransferRequestDTO saveTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws TransferException;

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void validateTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException;


    Object addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException;


    Page<CorpTransRequest> getTransferRequests(Pageable pageDetails);

    int countPendingRequest();

    CorpTransferAuth getAuthorizations(CorpTransRequest transRequest);

    Page<CorpTransRequest> getCompletedTransfers(Pageable pageDetails);

    String addAuthorization(CorpTransReqEntry transReqEntry);

    boolean userCanAuthorize(TransRequest transRequest);
}
