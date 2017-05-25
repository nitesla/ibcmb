package longbridge.services.implementations;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.TransferRequest;
import longbridge.models.User;
import longbridge.repositories.TransferRequestRepo;
import longbridge.services.AccountService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.IntegrationService;
import longbridge.services.TransferService;
import longbridge.utils.ResultType;
import longbridge.exception.TransferExceptions;
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

    private ModelMapper modelMapper;

    private AccountService accountService;

    private FinancialInstitutionService financialInstitutionService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TransferServiceImpl(TransferRequestRepo transferRequestRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService) {
        this.transferRequestRepo = transferRequestRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
    }

    @Override

    public TransferRequestDTO makeTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {


        validateTransfer(transferRequestDTO);
        logger.trace("Transfer details valid {}", transferRequestDTO);



        TransferRequest    transferRequest = integrationService.makeTransfer(convertDTOToEntity(transferRequestDTO));
        if (transferRequest != null) {
            saveTransfer(transferRequestDTO);
            if (transferRequest.getStatus().equals(ResultType.SUCCESS)) return convertEntityToDTO(transferRequest);
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException();
    }

    @Override
    public TransferRequest getTransfer(Long id) {
        return transferRequestRepo.findById(id);
    }

    @Override
    public Iterable<TransferRequest> getTransfers(User user) {
       return transferRequestRepo.findAll()
               .stream()
               .filter(i -> i.getUserReferenceNumber().equals(user.getId()))
               .collect(Collectors.toList());

    }

    @Override

    public boolean saveTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        boolean result = false;


        try {
            TransferRequest transferRequest = convertDTOToEntity(transferRequestDTO);
            transferRequestRepo.save(transferRequest);
            result = true;

        } catch (Exception e) {
            logger.error("Exception occurred {}",e.getMessage());
        }
        return result;
    }

    @Override
    public void deleteTransfer(Long id) throws InternetBankingException {
        TransferRequest transferRequest = transferRequestRepo.findById(id);
        if (transferRequest != null) {
            transferRequestRepo.delete(transferRequest);
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
            throw new InternetBankingTransferException();
        }

        String cif = accountService.getAccountByAccountNumber(dto.getCustomerAccountNumber()).getCustomerId();
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
                .anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber()));


        if (!acctPresent) {
            throw new InternetBankingTransferException(TransferExceptions.NO_DEBIT_ACCOUNT.toString());
        }
        validateBalance(dto);


    }

    @Override
    public Page<TransferRequest> getTransfers(User user, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferRequestDTO convertEntityToDTO(TransferRequest transferRequest) {
        return modelMapper.map(transferRequest, TransferRequestDTO.class);
    }


    public TransferRequest convertDTOToEntity(TransferRequestDTO transferRequestDTO) {
        return modelMapper.map(transferRequestDTO, TransferRequest.class);
    }

    public List<TransferRequestDTO> convertEntitiesToDTOs(Iterable<TransferRequest> transferRequests) {
        List<TransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (TransferRequest transferRequest : transferRequests) {
            TransferRequestDTO transferRequestDTO = convertEntityToDTO(transferRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }


}
