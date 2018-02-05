package longbridge.services.bulkTransfers;

import longbridge.api.TransferDetails;
import longbridge.dtos.CreditRequestDTO;
import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CreditRequestRepo;
import longbridge.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Service
public class BulkTransferWriter implements ItemWriter<TransferDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferWriter.class);
    @Value("${naps.url}")
    private String url;
    private String batchId;
    private RestTemplate template;
    @Autowired
    private BulkTransferRepo bulkTransferRepo;
    @Autowired
    private CreditRequestRepo creditRequestRepo;

    @Autowired
    public void setTemplate(RestTemplate template) {
        this.template = template;
    }

    @Override
    public void write(List<? extends TransferDTO> items) throws Exception {

        LOGGER.info("Received the information of {} transactions", items.size());
        if(!items.isEmpty()){
            batchId = items.get(0).getBatchId();
        }
        items.forEach(i -> LOGGER.debug("Received the information of a transaction: {}", i));

        List<TransferDTO> dtos = new ArrayList<>();
        dtos.addAll(items);

        BulkTransfer bulkTransfer = bulkTransferRepo.findFirstByRefCode(batchId);
        TransferDetails response = submitTransferRequests(dtos);
        LOGGER.info("Updating bulk transfer ID {} status  {}",batchId,response);
        if("000".equals(response.getResponseCode())){
            bulkTransfer.setStatus(StatusCode.PROCESSING.toString());
            bulkTransfer.setStatusDescription("Processing Transaction");
        }
        else {
            bulkTransfer.setStatus(StatusCode.FAILED.toString());
            bulkTransfer.setStatusDescription("Failed");
            List<CreditRequest> creditRequests = creditRequestRepo.findByBulkTransfer_Id(bulkTransfer.getId());
            creditRequests.stream().forEach(i -> {i.setStatus("FAILED"); creditRequestRepo.save(i);});

        }
        bulkTransferRepo.save(bulkTransfer);

    }


    private TransferDetails submitTransferRequests(List<TransferDTO> dtos) {

        try {
            LOGGER.info("Calling NAPS Web service via {}", url);
            TransferDetails details = template.postForObject(url, dtos, TransferDetails.class);
            LOGGER.debug("Response from service: {}",details);
            return details;
        } catch (Exception e) {
            LOGGER.error("Error making web service call",e);
            return new TransferDetails();
        }


    }
}

