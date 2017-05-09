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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

     @Autowired
    public TransferServiceImpl(TransferRequestRepo transferRequestRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService) {
        this.transferRequestRepo = transferRequestRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
    }

    @Override
    public boolean makeTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        TransferRequest transferRequest = convertDTOToEntity(transferRequestDTO);
        boolean ok =integrationService.makeTransfer(transferRequest);
        return  ok;


    }

    @Override
    public TransferRequest getTransfer(Long id) {
        return transferRequestRepo.findById(id);
    }

    @Override
    public Iterable<TransferRequest> getTransfers(User user) {
        // return transferRepo.getTransactions(user.getId());
        return null;
    }

    @Override
    public boolean saveTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
       boolean result=false;

       try{
           TransferRequest transferRequest = convertDTOToEntity(transferRequestDTO);
            transferRequestRepo.save(transferRequest);
            result=true;

       }catch (Exception e){

       }
       return  result;
    }

    @Override
    public void deleteTransfer(Long id) throws InternetBankingException {
        TransferRequest transferRequest = transferRequestRepo.findById(id);
        if (transferRequest != null) {
            transferRequestRepo.delete(transferRequest);
        }


    }

    @Override
    public boolean validateBalance(TransferRequestDTO transferRequest) {
       boolean ok =false;
       try{
           Map<String, BigDecimal> balanceDetails= integrationService.getBalance(transferRequest.getCustomerAccountNumber());
           BigDecimal balance = balanceDetails.get("AvailableBalance");
           if(balance!=null){
               if(balance.compareTo(transferRequest.getAmount())==0  ||(balance.compareTo(transferRequest.getAmount())==1)){
                   ok=true;
               }
           }
       }catch (Exception e){

       }
   return ok;
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
