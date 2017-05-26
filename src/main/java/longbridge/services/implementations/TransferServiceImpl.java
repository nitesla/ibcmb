package longbridge.services.implementations;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.TransRequest;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.repositories.TransferRequestRepo;
import longbridge.services.AccountService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.IntegrationService;
import longbridge.services.TransferService;
import longbridge.utils.ResultType;
import longbridge.exception.TransferExceptions;
import longbridge.utils.TransferType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {

    private TransferRequestRepo transferRequestRepo;
    private IntegrationService integrationService;
    private TransactionLimitServiceImpl limitService;

    private ModelMapper modelMapper;

    private AccountService accountService;

    private FinancialInstitutionService financialInstitutionService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TransferServiceImpl(TransferRequestRepo transferRequestRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService, TransactionLimitServiceImpl limitService) {
        this.transferRequestRepo = transferRequestRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
        this.limitService = limitService;
    }

    @Override

    public TransferRequestDTO makeTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {


        validateTransfer(transferRequestDTO);
        logger.trace("Transfer details valid {}", transferRequestDTO);



        TransRequest transRequest = integrationService.makeTransfer(convertDTOToEntity(transferRequestDTO));
        if (transRequest != null) {
            logger.trace("params {}", transRequest);
            saveTransfer(transferRequestDTO);
            if (transRequest.getStatus().equals(ResultType.SUCCESS)) return convertEntityToDTO(transRequest);
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException();
    }

    @Override
    public TransRequest getTransfer(Long id) {
        return transferRequestRepo.findById(id);
    }

    @Override
    public Iterable<TransRequest> getTransfers(User user) {
       return transferRequestRepo.findAll()
               .stream()
               .filter(i -> i.getUserReferenceNumber().equals(user.getId()))
               .collect(Collectors.toList());

    }

    @Override

    public boolean saveTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        boolean result = false;


        try {
            TransRequest transRequest = convertDTOToEntity(transferRequestDTO);
            transferRequestRepo.save(transRequest);
            result = true;

        } catch (Exception e) {
            logger.error("Exception occurred {}",e.getMessage());
        }
        return result;
    }

    @Override

    public void deleteTransfer(Long id) throws InternetBankingException {
        TransRequest transRequest = transferRequestRepo.findById(id);
        if (transRequest != null) {
            transferRequestRepo.delete(transRequest);
        }


    }

    @Override
    public void validateBalance(TransferRequestDTO transferRequest) throws InternetBankingTransferException {


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
        valdateAccounts(dto);
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
        validateBalance(dto);


    }

    @Override
    public Page<TransRequest> getTransfers(User user, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
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
   public void valdateAccounts(TransferRequestDTO dto) throws InternetBankingTransferException{
 if (dto.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || dto.getTransferType().equals(TransferType.CORONATION_BANK_TRANSFER))
    {
        if(integrationService.viewAccountDetails(dto.getBeneficiaryAccountNumber()) ==null) throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
        if(integrationService.viewAccountDetails(dto.getCustomerAccountNumber()) ==null) throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());

    }


}

}
