package longbridge.services;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.CorpTransferRequest;
import longbridge.models.CorporateUser;

/**
 * Created by Fortune on 5/19/2017.
 */
public interface CorpTransferService {

    String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException;

    String makeTransfer(CorpTransferRequest transferRequest) throws InternetBankingTransferException;

    String authorizeTransfer(CorporateUser authorizer, Long authorizationId);

}
