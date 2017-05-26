package longbridge.services.implementations;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.*;
import longbridge.models.CorpTransRequest;
import longbridge.models.CorporateUser;
import longbridge.models.PendAuth;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateUserRepo;
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
    CorporateUserRepo corporateUserRepo;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ModelMapper modelMapper;

    Locale locale = LocaleContextHolder.getLocale();

    @Override
    @Transactional
    public String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

        CorpTransRequest transferRequest = convertDTOToEntity(transferRequestDTO);

        if(transferRequest.getCorporate().getCorporateType().equals("SOLE")){
            makeTransfer(transferRequest);
            return messageSource.getMessage("transaction.success", null, locale);
        }

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
            List<PendAuth> pendAuths = new ArrayList<>();
            for (CorporateUser authorizer : authorizers) {
                PendAuth pendAuth = new PendAuth();
                pendAuth.setAuthorizer(authorizer);
                pendAuths.add(pendAuth);
            }

            transferRequest.setPendAuths(pendAuths);
            corpTransferRequestRepo.save(transferRequest);

        } catch (Exception e) {
            throw new InternetBankingTransferException();
        }
        return messageSource.getMessage("transfer.add.success", null, locale);
    }


    @Override
    @Transactional
    public String authorizeTransfer(CorporateUser authorizer, Long authorizationId) {
        PendAuth pendAuth = pendingAuthorizationRepo.findOne(authorizationId);
        if (!authorizer.getPendAuths().contains(pendAuth)) {
            throw new InvalidAuthorizationException(messageSource.getMessage("transfer.auth.invalid", null, locale));
        }
        try {
            CorpTransRequest transferRequest = pendAuth.getCorpTransferRequest();
            transferRequest.getPendAuths().remove(pendAuth);
            if (corporateService.getApplicableTransferRule(transferRequest).isAnyCanAuthorize()) {
                makeTransfer(transferRequest);
                transferRequest.getPendAuths().clear();
            }
            else if (transferRequest.getPendAuths().isEmpty()) {
                makeTransfer(transferRequest);
            }
            corpTransferRequestRepo.save(transferRequest);
            corporateUserRepo.save(authorizer);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }

        return messageSource.getMessage("transfer.auth.success", null, locale);

    }

    @Override
    public String makeTransfer(CorpTransRequest transferRequest) throws InternetBankingTransferException {
        return null; //TODO implement
    }

    public CorpTransferRequestDTO convertEntityToDTO(CorpTransRequest transferRequest) {
        return modelMapper.map(transferRequest, CorpTransferRequestDTO.class);
    }


    public CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO transferRequestDTO) {
        return modelMapper.map(transferRequestDTO, CorpTransRequest.class);
    }

    public List<CorpTransferRequestDTO> convertEntitiesToDTOs(Iterable<CorpTransRequest> transferRequests) {
        List<CorpTransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (CorpTransRequest transferRequest : transferRequests) {
            CorpTransferRequestDTO transferRequestDTO = convertEntityToDTO(transferRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }
}
