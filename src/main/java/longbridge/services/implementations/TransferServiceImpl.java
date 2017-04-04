package longbridge.services.implementations;

import longbridge.models.TransferRequest;
import longbridge.models.User;
import longbridge.repositories.TransferRepo;
import longbridge.services.IntegrationService;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {
    @Autowired
    TransferRepo transferRepo;

    @Autowired
    IntegrationService integrationService;

    @Override
    public void makeTransfer(TransferRequest transferRequest) {
//        transfer.getAccount();
        integrationService.makeTransfer(transferRequest);
    }

    @Override
    public TransferRequest getTransfer(Long id) {
        return transferRepo.findById(id);
    }

    @Override
    public Iterable<TransferRequest> getTransfers(User user) {
        return transferRepo.getTransactions(user.getId());
    }

    @Override
    public void saveTransfer(TransferRequest transferRequest) {
        transferRepo.save(transferRequest);
    }

    @Override
    public void deleteTransfer(Long id) {
        TransferRequest transferRequest = transferRepo.findById(id);
        if(transferRequest ==null){
 // todo           throw new Exception("No Transfer found");
            return;
        }
        transferRequest.setDelFlag("Y");
        transferRepo.save(transferRequest);
    }

}
