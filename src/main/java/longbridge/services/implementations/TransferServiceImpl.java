package longbridge.services.implementations;

import longbridge.models.Transfer;
import longbridge.models.User;
import longbridge.repositories.TransferRepo;
import longbridge.services.IntegrationService;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Fortune on 3/30/2017.
 */
public class TransferServiceImpl implements TransferService {
    @Autowired
    TransferRepo transferRepo;

    @Autowired
    IntegrationService integrationService;

    @Override
    public void makeTransfer(Transfer transfer) {
//        transfer.getAccount();
        integrationService.makeTransfer(transfer);
    }

    @Override
    public Transfer getTransfer(Long id) {
        return transferRepo.findById(id);
    }

    @Override
    public Iterable<Transfer> getTransfers(User user) {
        return transferRepo.getTransactions(user.getId());
    }

    @Override
    public void saveTransfer(Transfer transfer) {
        transferRepo.save(transfer);
    }


    @Override
    public void deleteTransfer(Long id) {
        Transfer transfer = transferRepo.findById(id);
        if(transfer==null){
 // todo           throw new Exception("No Transfer found");
            return;
        }
        transfer.setDelFlag("Y");
        transferRepo.save(transfer);
    }

}
