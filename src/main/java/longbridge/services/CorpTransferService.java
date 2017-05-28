package longbridge.services;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.CorpTransRequest;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.TransRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Fortune on 5/19/2017.
 */
public interface CorpTransferService {

    String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException;

    String authorizeTransfer(CorporateUser authorizer, Long authorizationId);

    List<CorpTransferRequestDTO> getTransfers(Corporate corporate);

    Page<CorpTransferRequestDTO> getTransfes(Corporate corporate, Pageable pageable);

    CorpTransferRequestDTO getTransfer(Long id);
}
