package longbridge.services.bulkTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
public class TransferStatusProcessor implements ItemProcessor<TransactionStatus,TransactionStatus>{
private Logger logger= LoggerFactory.getLogger(getClass());

    public TransferStatusProcessor() {
    }

    @Override
    public TransactionStatus process(TransactionStatus item) throws Exception {
        logger.info("Processing transfer information: {}", item);
        return item;
    }
}
