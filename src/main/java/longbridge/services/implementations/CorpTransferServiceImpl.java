package longbridge.services.implementations;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.*;
import longbridge.models.CorpTransferRequest;
import longbridge.models.CorporateUser;
import longbridge.models.PendingAuthorization;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.PendingAuthorizationRepo;
import longbridge.services.CorpTransferService;
import longbridge.services.CorporateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 5/20/2017.
 */
public class CorpTransferServiceImpl implements CorpTransferService {

    @Autowired
    CorporateService corporateService;

    @Autowired
    CorpTransferRequestRepo corpTransferRequestRepo;

    @Autowired
    PendingAuthorizationRepo pendingAuthorizationRepo;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ModelMapper modelMapper;

    Locale locale = LocaleContextHolder.getLocale();

    @Override
    @Transactional
    public String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

        CorpTransferRequest transferRequest = convertDTOToEntity(transferRequestDTO);

        if (corporateService.getCorporateRules(transferRequest.getCorporate().getId()).isEmpty()) {
            throw new TransferRuleException(messageSource.getMessage("rule.unavailable", null, locale));
        }
        if (corporateService.getQualifiedAuthorizers(transferRequest).isEmpty()) {
            throw new NoAuthorizerException(transferRequestDTO.getAmount());
        }

        try {

            transferRequest.setStatus("PENDING");
            //TODO do some other validations before saving

            List<CorporateUser> authorizers = corporateService.getQualifiedAuthorizers(transferRequest);
            PendingAuthorization pendingAuthorization = new PendingAuthorization();
            List<PendingAuthorization> pendingAuthorizations = new ArrayList<>();
            for (CorporateUser authorizer : authorizers) {
                pendingAuthorization.setAuthorizer(authorizer);
                pendingAuthorizations.add(pendingAuthorization);
            }
            transferRequest.setPendingAuthorizations(pendingAuthorizations);
            corpTransferRequestRepo.save(transferRequest);

        } catch (Exception e) {
            throw new TransferException(messageSource.getMessage("transfer.add.failure", null, locale), e);
        }
        return messageSource.getMessage("transfer.add.success", null, locale);
    }


    @Override
    @Transactional
    public String authorizeTransfer(CorporateUser authorizer, Long authorizationId) {
        PendingAuthorization pendingAuthorization = pendingAuthorizationRepo.findOne(authorizationId);
        if (!authorizer.getPendingAuthorizations().contains(pendingAuthorization)) {
            throw new InvalidAuthorizationexception(messageSource.getMessage("transfer.auth.invalid", null, locale));
        }
        try {
            CorpTransferRequest transferRequest = pendingAuthorization.getCorpTransferRequest();
            transferRequest.getPendingAuthorizations().remove(pendingAuthorization);
            if(transferRequest.getPendingAuthorizations().isEmpty()){
                makeTransfer(transferRequest);
            }
            corpTransferRequestRepo.save(transferRequest);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }

        return messageSource.getMessage("transfer.auth.success", null, locale);

    }

    @Override
    public String makeTransfer(CorpTransferRequest transferRequest) throws TransferException {
        return null; //TODO implement
    }

    public CorpTransferRequestDTO convertEntityToDTO(CorpTransferRequest transferRequest) {
        return modelMapper.map(transferRequest, CorpTransferRequestDTO.class);
    }


    public CorpTransferRequest convertDTOToEntity(CorpTransferRequestDTO transferRequestDTO) {
        return modelMapper.map(transferRequestDTO, CorpTransferRequest.class);
    }

    public List<CorpTransferRequestDTO> convertEntitiesToDTOs(Iterable<CorpTransferRequest> transferRequests) {
        List<CorpTransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (CorpTransferRequest transferRequest : transferRequests) {
            CorpTransferRequestDTO transferRequestDTO = convertEntityToDTO(transferRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }
}
