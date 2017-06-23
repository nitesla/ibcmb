package longbridge.services.bulkTransfers;


import longbridge.dtos.CreditRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
public class BulkTransferProcessor  implements ItemProcessor<CreditRequestDTO,CreditRequestDTO>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferProcessor.class);

    public BulkTransferProcessor() {
    }

    @Override
    public CreditRequestDTO process(CreditRequestDTO item) throws Exception {
        LOGGER.info("Processing payroll information: {}", item);
        return item;
    }
}
