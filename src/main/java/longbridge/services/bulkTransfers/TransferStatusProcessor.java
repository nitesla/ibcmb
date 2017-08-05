package longbridge.services.bulkTransfers;

import org.springframework.batch.item.ItemProcessor;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
public class TransferStatusProcessor implements ItemProcessor<TransactionStatus,TransactionStatus>{


    public TransferStatusProcessor() {
    }

    @Override
    public TransactionStatus process(TransactionStatus item) throws Exception {
        return null;
    }
}
