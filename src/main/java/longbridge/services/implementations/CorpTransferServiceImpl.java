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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/20/2017.
 */

@Service
public class CorpTransferServiceImpl implements CorpTransferService {

    private CorpTransferRequestRepo corpTransferRequestRepo;
    private IntegrationService integrationService;
    private TransactionLimitServiceImpl limitService;
    private ModelMapper modelMapper;
    private AccountService accountService;
    private FinancialInstitutionService financialInstitutionService;
    private ConfigurationService configService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private PendingAuthorizationRepo pendAuthRepo;
    @Autowired
    private CorporateService corporateService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    public CorpTransferServiceImpl(CorpTransferRequestRepo corpTransferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService, ConfigurationService configService) {
        this.corpTransferRequestRepo = corpTransferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
        this.configService = configService;
    }
    public CorpTransferRequestDTO makeTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(corpTransferRequestDTO);
        logger.trace("Transfer details valid {}", corpTransferRequestDTO);
        CorpTransRequest corpTransRequest = (CorpTransRequest) integrationService.makeTransfer(convertDTOToEntity(corpTransferRequestDTO));
        if (corpTransRequest != null) {
            logger.trace("params {}", corpTransRequest);
            saveTransfer(convertEntityToDTO(corpTransRequest));
            if (corpTransRequest.getStatus().equals("000")||corpTransRequest.getStatus().equals("00")) return convertEntityToDTO(corpTransRequest);
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException();
    }


    @Override
    public CorpTransRequest getTransfer(Long id) {
        return corpTransferRequestRepo.findById(id);
    }

    @Override
    public Iterable<CorpTransRequest> getTransfers(User user) {
        return corpTransferRequestRepo.findAll()
                .stream()
                .filter(i -> i.getUserReferenceNumber().equals(user.getId()))
                .collect(Collectors.toList());

    }

    @Override
    public boolean saveTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        boolean result = false;
        try {
            CorpTransRequest transRequest = convertDTOToEntity(corpTransferRequestDTO);
            corpTransferRequestRepo.save(transRequest);
            result = true;

        } catch (Exception e) {
            logger.error("Exception occurred {}", e.getMessage());
        }
        return result;
    }


    @Override
    @Transactional
    public String authorizeTransfer(CorporateRole role, Long authId) throws InternetBankingException {
        PendAuth pendAuth = pendAuthRepo.findOne(authId);
        if (!role.getPendAuths().contains(pendAuth)) {
            throw new InvalidAuthorizationException(messageSource.getMessage("transfer.auth.invalid", null, locale));
        }
        try {
            CorpTransRequest transferRequest = pendAuth.getCorpTransferRequest();
            transferRequest.getPendAuths().remove(transferRequest);
            if (corporateService.getApplicableTransferRule(transferRequest).isAnyCanAuthorize()) {
                makeTransfer(convertEntityToDTO(transferRequest));
                transferRequest.getPendAuths().clear();
            }
            else if (transferRequest.getPendAuths().isEmpty()) {
                makeTransfer(convertEntityToDTO(transferRequest));
            }
            corpTransferRequestRepo.save(transferRequest);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }

        return messageSource.getMessage("transfer.auth.success", null, locale);

    }

    private void validateBalance(CorpTransferRequestDTO corpTransferRequest) throws InternetBankingTransferException {

        BigDecimal balance = integrationService.getAvailableBalance(corpTransferRequest.getCustomerAccountNumber());
        if (balance != null) {
            if (!(balance.compareTo(corpTransferRequest.getAmount()) == 0 || (balance.compareTo(corpTransferRequest.getAmount()) == 1))) {
                throw new InternetBankingTransferException(TransferExceptions.BALANCE.toString());
            }
        }

    }

    @Override
    public void validateTransfer(CorpTransferRequestDTO dto) throws InternetBankingTransferException {
        if (dto.getBeneficiaryAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber())) {
            throw new InternetBankingTransferException(TransferExceptions.SAME_ACCOUNT.toString());
        }
        validateAccounts(dto);
        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                dto.getTransferType(),
                UserType.CORPORATE,
                dto.getCustomerAccountNumber(),
                dto.getAmount()

        );
        if (limitExceeded) throw new InternetBankingTransferException(TransferExceptions.LIMIT_EXCEEDED.toString());

        String cif = accountService.getAccountByAccountNumber(dto.getCustomerAccountNumber()).getCustomerId();
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
                .anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber()));


        if (!acctPresent) {
            throw new InternetBankingTransferException(TransferExceptions.NO_DEBIT_ACCOUNT.toString());
        }
        if(validateBalance()) {
            validateBalance(dto);
        }


    }
    private boolean validateBalance() {
        SettingDTO setting = configService.getSettingByName("BALANCE_VALIDATION");
        if(setting!=null&&setting.isEnabled()) {
            return ("1".equals(setting.getValue()));
        }
        return true;
    }

    @Override
    public Page<CorpTransRequest> getTransfers(User user, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
    }

    public CorpTransferRequestDTO convertEntityToDTO(CorpTransRequest corpTransRequest) {
        return modelMapper.map(corpTransRequest, CorpTransferRequestDTO.class);
    }


    public CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO corpTransferRequestDTO) {
        return modelMapper.map(corpTransferRequestDTO, CorpTransRequest.class);
    }

    public List<CorpTransferRequestDTO> convertEntitiesToDTOs(Iterable<CorpTransRequest> corpTransferRequests) {
        List<CorpTransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (CorpTransRequest transRequest : corpTransferRequests) {
            CorpTransferRequestDTO transferRequestDTO = convertEntityToDTO(transRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }

    private void validateAccounts(CorpTransferRequestDTO dto) throws InternetBankingTransferException {
        if (dto.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || dto.getTransferType().equals(TransferType.CORONATION_BANK_TRANSFER)) {
            if (integrationService.viewAccountDetails(dto.getBeneficiaryAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
            if (integrationService.viewAccountDetails(dto.getCustomerAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());

        }
    }
}
