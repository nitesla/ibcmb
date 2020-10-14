package longbridge.services.bulkTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
@Component
public class TransferStatusProcessor implements ItemProcessor<TransactionStatus,TransactionStatus>{
private final Logger logger= LoggerFactory.getLogger(getClass());

    public TransferStatusProcessor() {
    }

    @Override
    public TransactionStatus process(TransactionStatus item) throws Exception {

        if(item.getTranxStatus()==null){
            item.setTranxStatus("PROCESSING");
        }
        logger.info("Processing transfer information: {}", item);
        return item;
    }
}
