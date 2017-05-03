package longbridge.services.implementations;

import longbridge.dtos.TransferRequestDTO;
import longbridge.models.Account;
import longbridge.models.FinancialInstitution;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {
    @Autowired
    TransferRequestRepo transferRequestRepo;

    @Autowired
    IntegrationService integrationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    FinancialInstitutionService financialInstitutionService;

    @Override
    public void makeTransfer(TransferRequest transferRequest) {
//        transfer.getAccount();
        integrationService.makeTransfer(transferRequest);
    }

    @Override
    public TransferRequest getTransfer(Long id) {
        return transferRequestRepo.findById(id);
    }

    @Override
    public Iterable<TransferRequest> getTransfers(User user) {
       // return transferRepo.getTransactions(user.getId());
        return  null;
    }

    @Override
    public void saveTransfer(TransferRequestDTO transferRequestDTO) {
      //  Account source = accountService.getAccountByAccountNumber(transferRequestDTO.getSource());
      //  transferRequestDTO.setSource(source.getAccountNumber());
        transferRequestDTO.setBeneficiaryAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
        transferRequestDTO.setRemarks(transferRequestDTO.getRemarks());
        transferRequestDTO.setAmount(transferRequestDTO.getAmount());
        FinancialInstitution financialInstitution=financialInstitutionService.getFinancialInstitutionByCode("CB");
        transferRequestDTO.setFinancialInstitution(financialInstitution);
        TransferRequest transferRequest=convertDTOToEntity(transferRequestDTO);
        transferRequestRepo.save(transferRequest);
    }

    @Override
    public void deleteTransfer(Long id) {
        TransferRequest transferRequest = transferRequestRepo.findById(id);
        if(transferRequest ==null){
 // todo           throw new Exception("No Transfer found");
            return;
        }
        transferRequest.setDelFlag("Y");
        transferRequestRepo.save(transferRequest);
    }

	@Override
	public Page<TransferRequest> getTransfers(User user, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

    public TransferRequestDTO convertEntityToDTO(TransferRequest transferRequest){
        return  modelMapper.map(transferRequest,TransferRequestDTO.class);
    }


    public TransferRequest convertDTOToEntity(TransferRequestDTO transferRequestDTO){
        return  modelMapper.map(transferRequestDTO,TransferRequest.class);
    }

    public List<TransferRequestDTO> convertEntitiesToDTOs(Iterable<TransferRequest> transferRequests){
        List<TransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for(TransferRequest transferRequest: transferRequests){
            TransferRequestDTO transferRequestDTO = convertEntityToDTO(transferRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }

}
