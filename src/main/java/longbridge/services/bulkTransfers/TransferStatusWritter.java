package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CreditRequestRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
@Service
public class TransferStatusWritter implements ItemWriter<TransactionStatus> {
    private Logger logger= LoggerFactory.getLogger(getClass());
    private CreditRequestRepo creditRequestRepo;
    private BulkTransferRepo bulkTransferRepo;

    public TransferStatusWritter() {
    }

    @Autowired
    public void setBulkTransferRepo(BulkTransferRepo bulkTransferRepo) {
        this.bulkTransferRepo = bulkTransferRepo;
    }

    @Autowired
    public void setCreditRequestRepo(CreditRequestRepo creditRequestRepo) {
        this.creditRequestRepo = creditRequestRepo;
    }

    @Override
    public void write(List<? extends TransactionStatus> items) throws Exception {
        logger.info("Received the information of {} transactions", items.size());
        items.forEach(i -> logger.debug("Received the information of a transaction: {}", i));
        items.stream()
                .forEach(
                        i -> {
                            CreditRequest creditRequest = creditRequestRepo.findByAccountNumberAndBulkTransfer_Id(i.getBeneficiaryAccountNumber(), Long.parseLong(i.getBatchId()));
                            creditRequest.setStatus(i.getTranxStatus());
                            creditRequestRepo.save(creditRequest);
                            BulkTransfer bulkTransfer = bulkTransferRepo.findOne(Long.parseLong(i.getBatchId()));
                            bulkTransfer.setStatus(i.getBatchProcessingStatus());
                            bulkTransferRepo.save(bulkTransfer);

                        }

                );

    }
}
