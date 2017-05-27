package longbridge.services;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.CorpTransRequest;
import longbridge.models.CorporateUser;
import longbridge.models.TransRequest;

/**
 * Created by Fortune on 5/19/2017.
 */
public interface CorpTransferService {

    String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException;

    TransRequest makeTransfer(CorpTransRequest transferRequest) throws InternetBankingTransferException;

    String authorizeTransfer(CorporateUser authorizer, Long authorizationId);

}
