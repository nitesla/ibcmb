package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CreditRequestRepo;
import longbridge.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
@Service
public class TransferStatusWritter implements ItemWriter<TransactionStatus> {
    private final Logger logger= LoggerFactory.getLogger(getClass());
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

        items.stream().filter(Objects::nonNull)
                .forEach(
                        i -> {
                            logger.info("batchId {}", i.getBatchId());

                            BulkTransfer bulkTransfer = bulkTransferRepo.findFirstByRefCode(i.getBatchId());
//                            CreditRequest creditRequest = creditRequestRepo.findAByAccountNumberAndBulkTransferId(i.getBeneficiaryAccountNumber(), bulkTransfer.getId());
                            CreditRequest creditRequest = creditRequestRepo.findByAccountNumberAndBulkTransferIdAndReferenceNumber(i.getBeneficiaryAccountNumber(),
                                    bulkTransfer.getId(), i.getPaymentReference());
                                  logger.info("creditreq {}",creditRequest);
                            //  i.setTranxStatus("PENDING");
                            //added by GB

                                creditRequest.setStatus(i.getTranxStatus());
                                creditRequestRepo.save(creditRequest);


                               /* if ("PROCESSING".equals(i.getTranxStatus()) || "PENDING".equals(i.getTranxStatus())) {
                                    bulkTransfer.setStatus(StatusCode.PROCESSING.toString());
                                    bulkTransfer.setStatusDescription("Processing Transaction");
                                    bulkTransferRepo.save(bulkTransfer);
                                }*/
                                //end
                        } );
//added GB
        BulkTransfer bulkTransfer  = bulkTransferRepo.findFirstByRefCode( items.stream().filter(Objects::nonNull)
                .collect(Collectors.toList()).get(0).getBatchId());
             try{
                 String pending="PENDING";
                 String processing="PROCESSING";
                 List<CreditRequest> creditRequestsPending=null;
                 List<CreditRequest> creditRequestsProcessing=null;
                 try {
                     creditRequestsPending = creditRequestRepo.findAllByStatusAndBulkTransferId(pending, bulkTransfer.getId());
                     creditRequestsProcessing = creditRequestRepo.findAllByStatusAndBulkTransferId(processing, bulkTransfer.getId());
                 }catch(Exception e){
                     logger.info("Error Retrieving Pending transfer {}",e);
                 }
                logger.info("creditreqCountPending {}",creditRequestsPending.size());
                logger.info("creditreqCountProcessing {}",creditRequestsProcessing.size());

                if(creditRequestsPending.size()==0 && creditRequestsProcessing.size()==0) {
                    bulkTransfer.setStatus("01");
                    bulkTransfer.setStatusDescription(StatusCode.COMPLETED.toString());
                    bulkTransferRepo.save(bulkTransfer);
                    logger.info("Batch completed");

                }
                 if(creditRequestsPending.size()>0 || creditRequestsProcessing.size()>0) {

                     bulkTransfer.setStatus(StatusCode.PROCESSING.toString());
                    bulkTransfer.setStatusDescription("Processing Transaction");
                    bulkTransferRepo.save(bulkTransfer);
                    logger.info("Batch not yet completed");

                }
            }catch(Exception e){
                 bulkTransfer.setStatus(StatusCode.PROCESSING.toString());
                 bulkTransfer.setStatusDescription("Processing Transaction");
                 bulkTransferRepo.save(bulkTransfer);
                 logger.info("Error processing Batch ");

             }
//added GB

    }
}
