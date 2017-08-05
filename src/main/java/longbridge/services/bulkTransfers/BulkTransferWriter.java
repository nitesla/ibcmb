package longbridge.services.bulkTransfers;

import longbridge.api.TransferDetails;
import longbridge.dtos.CreditRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Service
public class BulkTransferWriter implements ItemWriter<TransferDTO> {


    private RestTemplate template;

    @Autowired
    public void setTemplate(RestTemplate template) {
        this.template = template;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferWriter.class);


    @Override
    public void write(List<? extends TransferDTO> items)
            throws Exception {

        LOGGER.info("Received the information of {} transactions", items.size());
        items.forEach(i -> LOGGER.debug("Received the information of a transaction: {}", i));

        List<TransferDTO> dtos = new ArrayList<>();
        dtos.addAll(items);

        TransferDetails details= details(dtos);

    }



    private TransferDetails details( List<TransferDTO> dtos ){
        String uri =  "http://132.10.200.140:9292/service/outwardnapsTransfer";
        try {

            TransferDetails details = template.postForObject(uri, dtos, TransferDetails.class);
            return details;
        } catch (Exception e) {
         e.printStackTrace();
            return new TransferDetails();
        }


    }
}

