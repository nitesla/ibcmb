package longbridge.services.bulkTransfers;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
public class TransferStatusWritter implements ItemWriter<TransactionStatus> {


    public TransferStatusWritter() {
    }

    @Override
    public void write(List<? extends TransactionStatus> items) throws Exception {

    }
}
