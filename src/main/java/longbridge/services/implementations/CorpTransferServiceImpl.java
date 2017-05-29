package longbridge.services.implementations;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.PendingAuthorizationRepo;
import longbridge.services.*;
import longbridge.utils.ResultType;
import longbridge.utils.TransferType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/20/2017.
 */

@Service
public class CorpTransferServiceImpl implements CorpTransferService {


    Locale locale = LocaleContextHolder.getLocale();
    private CorporateService corporateService;
    private CorpTransferRequestRepo corpTransferRequestRepo;
    private PendingAuthorizationRepo pendingAuthorizationRepo;
    private CorporateUserRepo corporateUserRepo;
    private MessageSource messageSource;
    private ModelMapper modelMapper;
    private IntegrationService integrationService;
    private AccountService accountService;
    private TransactionLimitService limitService;
    private CorporateRepo corporateRepo;
    private ConfigurationService configService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorpTransferServiceImpl(CorporateService corporateService, CorpTransferRequestRepo corpTransferRequestRepo, PendingAuthorizationRepo pendingAuthorizationRepo, CorporateUserRepo corporateUserRepo, MessageSource messageSource, ModelMapper modelMapper, IntegrationService integrationService, AccountService accountService, TransactionLimitService limitService, CorporateRepo corporateRepo, ConfigurationService configService) {
        this.corporateService = corporateService;
        this.corpTransferRequestRepo = corpTransferRequestRepo;
        this.pendingAuthorizationRepo = pendingAuthorizationRepo;
        this.corporateUserRepo = corporateUserRepo;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
        this.integrationService = integrationService;
        this.accountService = accountService;
        this.limitService = limitService;
        this.corporateRepo = corporateRepo;
        this.configService = configService;
    }

    @Override
    @Transactional
    public String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

        CorpTransRequest transferRequest = convertDTOToEntity(transferRequestDTO);

        if (transferRequest.getCorporate().getCorporateType().equals("SOLE")) {
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

        if (corporateService.getApplicableTransferRule(pendAuth.getCorpTransferRequest()) == null) {
            throw new TransferRuleException(messageSource.getMessage("rule.unavailable", null, locale));

        }
        try {
            CorpTransRequest transferRequest = pendAuth.getCorpTransferRequest();
            transferRequest.getPendAuths().remove(pendAuth);

            if (corporateService.getApplicableTransferRule(transferRequest).isAnyCanAuthorize()) {
                makeTransfer(transferRequest);
                transferRequest.getPendAuths().clear();
            } else if (transferRequest.getPendAuths().isEmpty()) {
                makeTransfer(transferRequest);
            }
            corpTransferRequestRepo.save(transferRequest);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }

        return messageSource.getMessage("transfer.auth.success", null, locale);

    }

    private CorpTransferRequestDTO makeTransfer(CorpTransRequest transferRequest) throws InternetBankingTransferException {

        validateTransfer(transferRequest);
        TransRequest transRequest = integrationService.makeTransfer(transferRequest);
        if (transRequest != null) {
            saveTransfer(transferRequest);
            if (transRequest.getStatus() != null && transferRequest.getStatus().equals(ResultType.SUCCESS)) {
                return convertEntityToDTO(transRequest);
            }
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException();
    }

    private boolean saveTransfer(CorpTransRequest transferRequest) throws InternetBankingTransferException {
        boolean result = false;
        try {
            corpTransferRequestRepo.save(transferRequest);
            result = true;
        } catch (Exception e) {
            logger.error("Exception occurred {}", e.getMessage());
        }
        return result;
    }

    private void validateBalance(CorpTransRequest transferRequest) throws InternetBankingTransferException {

        BigDecimal balance = integrationService.getAvailableBalance(transferRequest.getCustomerAccountNumber());
        if (balance != null) {
            if (!(balance.compareTo(transferRequest.getAmount()) == 0 || (balance.compareTo(transferRequest.getAmount()) == 1))) {
                throw new InternetBankingTransferException(TransferExceptions.BALANCE.toString());
            }
        }

    }


    private void validateTransfer(CorpTransRequest transferRequest) throws InternetBankingTransferException {
        if (transferRequest.getBeneficiaryAccountNumber().equalsIgnoreCase(transferRequest.getCustomerAccountNumber())) {
            throw new InternetBankingTransferException(TransferExceptions.SAME_ACCOUNT.toString());
        }
        validateAccounts(transferRequest);
        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                transferRequest.getTransferType(),
                UserType.CORPORATE,
                transferRequest.getCustomerAccountNumber(),
                transferRequest.getAmount()

        );
        if (limitExceeded) throw new InternetBankingTransferException(TransferExceptions.LIMIT_EXCEEDED.toString());

        String cif = accountService.getAccountByAccountNumber(transferRequest.getCustomerAccountNumber()).getCustomerId();
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
                .anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(transferRequest.getCustomerAccountNumber()));


        if (!acctPresent) {
            throw new InternetBankingTransferException(TransferExceptions.NO_DEBIT_ACCOUNT.toString());
        }
        if (validateBalance()) {
            validateBalance(transferRequest);
        }


    }

    private boolean validateBalance() {
        SettingDTO setting = configService.getSettingByName("BALANCE_VALIDATION");
        if (setting != null && setting.isEnabled()) {
            return ("1".equals(setting.getValue()));
        }
        return true;
    }

    private void validateAccounts(CorpTransRequest transferRequest) throws InternetBankingTransferException {
        if (transferRequest.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || transferRequest.getTransferType().equals(TransferType.CORONATION_BANK_TRANSFER)) {
            if (integrationService.viewAccountDetails(transferRequest.getBeneficiaryAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
            if (integrationService.viewAccountDetails(transferRequest.getCustomerAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
        }
    }

    @Override
    public List<CorpTransferRequestDTO> getTransfers(Corporate corporate) {
        List<CorpTransRequest> transRequests = corporate.getCorpTransferRequests();
        return convertEntitiesToDTOs(transRequests);
    }

    @Override
    public Page<CorpTransferRequestDTO> getTransfes(Corporate corporate, Pageable pageable) {
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporate(corporate, pageable);
        List<CorpTransferRequestDTO> dtos = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CorpTransferRequestDTO> pageImpl = new PageImpl<CorpTransferRequestDTO>(dtos, pageable, t);
        return pageImpl;
    }

    @Override
    public CorpTransferRequestDTO getTransfer(Long id) {
        CorpTransRequest transRequest = corpTransferRequestRepo.findOne(id);
        return convertEntityToDTO(transRequest);
    }

    public CorpTransferRequestDTO convertEntityToDTO(TransRequest transferRequest) {
        CorpTransferRequestDTO transferRequestDTO = modelMapper.map(transferRequest, CorpTransferRequestDTO.class);
        CorpTransRequest transRequest = (CorpTransRequest) transferRequest;
        transferRequestDTO.setCorporateId(transRequest.getCorporate().getId().toString());
        return transferRequestDTO;
    }


    public CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO transferRequestDTO) {
        CorpTransRequest transferRequest = modelMapper.map(transferRequestDTO, CorpTransRequest.class);
        transferRequest.setCorporate(corporateRepo.findOne(Long.parseLong(transferRequestDTO.getCorporateId())));
        return transferRequest;
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
