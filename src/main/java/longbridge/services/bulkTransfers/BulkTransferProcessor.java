package longbridge.services.bulkTransfers;


import longbridge.dtos.CreditRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Component
public class BulkTransferProcessor  implements ItemProcessor<TransferDTO,TransferDTO>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferProcessor.class);

    public BulkTransferProcessor() {
    }

    @Override
    public TransferDTO process(TransferDTO item) throws Exception {
        LOGGER.info("Processing transfer information: {}", item);
        return item;
    }
}

