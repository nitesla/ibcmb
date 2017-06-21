package longbridge.services.implementations;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.repositories.BulkTransferRepo;
import longbridge.services.BulkTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Service
public class BulkTransferServiceImpl implements BulkTransferService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BulkTransferRepo bulkTransferRepo;
    @Autowired
    private MessageSource messageSource;


    @Autowired
    public BulkTransferServiceImpl(BulkTransferRepo bulkTransferRepo) {
        this.bulkTransferRepo = bulkTransferRepo;
    }


    @Override
    public String makeBulkTransferRequest(BulkTransfer bulkTransfer) {
        logger.trace("Transfer details valid {}", bulkTransfer);
        //validate bulk transfer
        bulkTransferRepo.save(bulkTransfer);

        return messageSource.getMessage("bulk.transfer.success",null,null);
    }



    @Override
    public Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details) {
        return bulkTransferRepo.findByCorporate(corporate,details);
    }

    @Override
    public String cancelBulkTransferRequest(Long id) {
        //cancelling bulk transaction request
        BulkTransfer one = bulkTransferRepo.getOne(id);
        one.setStatus("X");
        bulkTransferRepo.save(one);
        return null;
    }

    @Override
    public BulkTransfer getBulkTransferRequest(Long id) {
        return bulkTransferRepo.getOne(id);
    }
}
