package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferExceptions;
import longbridge.models.*;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.TransferRequestRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.TransferType;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {
    private RetailUserRepo retailUserRepo;
    private TransferRequestRepo transferRequestRepo;
    private IntegrationService integrationService;
    private TransactionLimitServiceImpl limitService;
    private ModelMapper modelMapper;
    private AccountService accountService;
    private FinancialInstitutionService financialInstitutionService;
    private ConfigurationService configService;
    private MessageSource messages;
    private Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public TransferServiceImpl(TransferRequestRepo transferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService, ConfigurationService configurationService
            , RetailUserRepo retailUserRepo, MessageSource messages) {
        this.transferRequestRepo = transferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
        this.configService = configurationService;
        this.retailUserRepo = retailUserRepo;
        this.messages = messages;
    }


    public TransferRequestDTO makeTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(transferRequestDTO);
        logger.info("Initiating a Transfer", transferRequestDTO);
        TransRequest transRequest = integrationService.makeTransfer(convertDTOToEntity(transferRequestDTO));

        logger.trace("Transfer Details: ", transRequest);

        if (transRequest != null) {

            transferRequestDTO = saveTransfer(convertEntityToDTO(transRequest));

            if (transRequest.getStatus() != null) {
                if (transRequest.getStatus().equalsIgnoreCase("000") || transRequest.getStatus().equalsIgnoreCase("00"))
                    return transferRequestDTO;

                throw new InternetBankingTransferException(transRequest.getStatus());
            }


            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException(messages.getMessage("transfer.failed",null,locale));
    }

    @Override
    public TransRequest getTransfer(Long id) {
        return transferRequestRepo.findById(id);
    }

    @Override
    public Iterable<TransRequest> getTransfers(User user) {
        return transferRequestRepo.findByUserReferenceNumber("RET_" + user.getId());

    }

    @Override
    public TransferRequestDTO saveTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {

        try {
            TransRequest transRequest = convertDTOToEntity(transferRequestDTO);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                RetailUser user = retailUserRepo.findFirstByUserName(currentUserName);
                transRequest.setUserReferenceNumber("RET_" + user.getId());
            }
            transferRequestDTO = convertEntityToDTO(transferRequestRepo.save(transRequest));


        } catch (Exception e) {
            logger.error("Exception occurred saving transfer request", e);
        }
        return transferRequestDTO;
    }


    private void validateBalance(TransferRequestDTO transferRequest) throws InternetBankingTransferException {


        BigDecimal balance = integrationService.getAvailableBalance(transferRequest.getCustomerAccountNumber());
        if (balance != null) {

            if (!(balance.compareTo(transferRequest.getAmount()) == 0 || (balance.compareTo(transferRequest.getAmount()) == 1))) {
                throw new InternetBankingTransferException(TransferExceptions.BALANCE.toString());
            }
        }

    }

    @Override
    public void validateTransfer(TransferRequestDTO dto) throws InternetBankingTransferException {


        if (dto.getBeneficiaryAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber())) {
            throw new InternetBankingTransferException(TransferExceptions.SAME_ACCOUNT.toString());
        }
        validateAccounts(dto);
        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                dto.getTransferType(),
                UserType.RETAIL,
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
        if (validateBalance()) {
            validateBalance(dto);
        }

    }

    @Override
    public Page<TransRequest> getCompletedTransfers(Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
        RetailUser user = getCurrentUser();
        Page<TransRequest> page = transferRequestRepo.findByUserReferenceNumberAndStatusInAndTranDateNotNullOrderByTranDateDesc("RET_" + user.getId(), Arrays.asList("00","000"), pageDetails);
        logger.info("Completed transfers content" + page.getContent());
        return page;
    }

    @Override
    public Page<TransferRequestDTO> getCompletedTransfers(String pattern, Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
        Page<TransRequest> page = transferRequestRepo.findUsingPattern(pattern, pageDetails);
        List<TransferRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        logger.trace("Completed transfers", dtOs);
        long t = page.getTotalElements();
        Page<TransferRequestDTO> pageImpl = new PageImpl<TransferRequestDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public List<TransRequest> getLastTenTransactionsForAccount(String s) {
        return transferRequestRepo.findTop10ByCustomerAccountNumberOrderByTranDateDesc(s);
    }

    private boolean validateBalance() {
        SettingDTO setting = configService.getSettingByName("ACCOUNT_BALANCE_VALIDATION");
        if (setting != null && setting.isEnabled()) {

            return ("YES".equals(setting.getValue()));
        }
        return true;
    }

    @Override
    public Page<TransRequest> getTransfers(User user, Pageable pageDetails) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public TransferRequestDTO convertEntityToDTO(TransRequest transRequest) {
        return modelMapper.map(transRequest, TransferRequestDTO.class);
    }


    public TransRequest convertDTOToEntity(TransferRequestDTO transferRequestDTO) {
        return modelMapper.map(transferRequestDTO, TransRequest.class);
    }

    public List<TransferRequestDTO> convertEntitiesToDTOs(Iterable<TransRequest> transferRequests) {
        List<TransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (TransRequest transRequest : transferRequests) {
            TransferRequestDTO transferRequestDTO = convertEntityToDTO(transRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }

    private void validateAccounts(TransferRequestDTO dto) throws InternetBankingTransferException {

        String bvn = integrationService.viewCustomerDetails(dto.getCustomerAccountNumber()).getBvn();
        if (bvn == null || bvn.isEmpty() || bvn.equalsIgnoreCase(""))
            throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());


        if (!integrationService.viewAccountDetails(dto.getCustomerAccountNumber()).getAcctStatus().equalsIgnoreCase("A"))
            throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());


        if (!NumberUtils.isNumber(dto.getAmount().toString()) || dto.getAmount().compareTo(new BigDecimal(0)) <= 0)
            throw new InternetBankingTransferException(TransferExceptions.INVALID_AMOUNT.toString());


        if (dto.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || dto.getTransferType().equals(TransferType.CORONATION_BANK_TRANSFER)) {
            AccountDetails sourceAccount = integrationService.viewAccountDetails(dto.getCustomerAccountNumber());
            AccountDetails destAccount = integrationService.viewAccountDetails(dto.getBeneficiaryAccountNumber());
            if (sourceAccount == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
            if (destAccount == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
            if ((("NGN").equalsIgnoreCase(sourceAccount.getAcctCrncyCode())) && !("NGN").equalsIgnoreCase(destAccount.getAcctCrncyCode()))

                throw new InternetBankingTransferException(TransferExceptions.NOT_ALLOWED.toString());

        }
        if (dto.getTransferType().equals(TransferType.INTER_BANK_TRANSFER)) {
            NEnquiryDetails details = integrationService.doNameEnquiry(dto.getFinancialInstitution().getInstitutionCode(), dto.getBeneficiaryAccountNumber());
          /*  if (details != null && details.getAccountName() == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());*/

            if (integrationService.viewAccountDetails(dto.getCustomerAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
        }

    }

    private RetailUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RetailUser retailUser = (RetailUser) principal.getUser();
        return retailUser;
    }

}
