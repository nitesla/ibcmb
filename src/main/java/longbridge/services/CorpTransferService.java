package longbridge.services;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by Fortune on 5/19/2017.
 */
public interface CorpTransferService {

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    CorpTransferRequestDTO makeTransfer(CorpTransferRequestDTO corpTransferRequest) throws TransferException;

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    CorpTransRequest getTransfer(Long id);

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    Iterable<CorpTransRequest> getTransfers(User user);

    @PreAuthorize("hasAuthority('GET_TRANSFER')")
    Page<CorpTransRequest> getTransfers(User user, Pageable pageDetails);

    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    CorpTransferRequestDTO saveTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws TransferException;
    @PreAuthorize("hasAuthority('MAKE_TRANSFER')")
    void validateTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException;

    String authorizeTransfer(CorporateUser user, Long authId) throws InternetBankingException;

    String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException;


}
