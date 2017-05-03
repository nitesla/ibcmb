package longbridge.services.implementations;

import longbridge.models.TransferRequest;
import longbridge.models.User;
import longbridge.repositories.TransferRequestRepo;
import longbridge.services.IntegrationService;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {
    @Autowired
    TransferRequestRepo transferRequestRepo;

    @Autowired
    IntegrationService integrationService;

    @Override
    public void makeTransfer(TransferRequest transferRequest) {
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
    public void saveTransfer(TransferRequest transferRequest) {
        transferRequestRepo.save(transferRequest);
    }

    @Override
    public void deleteTransfer(Long id) {
        TransferRequest transferRequest = transferRequestRepo.findById(id);
        if(transferRequest !=null){
            transferRequestRepo.delete(transferRequest);
        }


    }

	@Override
	public Page<TransferRequest> getTransfers(User user, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

}
