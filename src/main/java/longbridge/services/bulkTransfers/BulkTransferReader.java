package longbridge.services.bulkTransfers;

import longbridge.api.CustomerDetails;
import longbridge.dtos.CreditRequestDTO;
import longbridge.models.CreditRequest;
import longbridge.repositories.CreditRequestRepo;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class demonstrates how we can read the input of our batch job from
 * the database based on batchId
 *
 * @author ayoade_farooq@yahoo.com
 */
@Scope(value = "step", proxyMode = ScopedProxyMode.INTERFACES)
@Component
class BulkTransferReader implements ItemReader<TransferDTO>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferReader.class);
    private final List<TransferDTO> transferData = new ArrayList<>();
    private final String batchId;
    private CreditRequestRepo repo;
    private IntegrationService integrationService;
    private int nextIndex;


    @Autowired
    public BulkTransferReader(@Value("#{jobParameters[batchId]}") final String batchId) {
        this.batchId = batchId;
        nextIndex=0;
    }

    @Autowired
    void setFields(CreditRequestRepo repo, IntegrationService integrationService) {
        this.repo = repo;
        this.integrationService = integrationService;

    }

    @Override
    public TransferDTO read() throws Exception {
        LOGGER.info("Reading the information of the next transfer");


        TransferDTO nextRequest = null;

        if (nextIndex < transferData.size()) {
            nextRequest = transferData.get(nextIndex);
            nextIndex++;
        }

//      Iterator<CreditRequestDTO>  iterator =transferData.iterator();
//      while(iterator.hasNext()){
//
//          nextRequest= iterator.next();
//          transferData.remove(nextRequest);
//          return nextRequest;
//      }



        LOGGER.info("Found record: {}", nextRequest);


        return nextRequest;
    }


    TransferDTO map(CreditRequest request) {
        String payRef = "CORONATION/NAPS/" + (new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        CustomerDetails details = integrationService.viewCustomerDetails(request.getBulkTransfer().getDebitAccount());
        TransferDTO dto = new TransferDTO();
        dto.setAccountName(details.getCustomerName());
        dto.setAccountNumber(request.getBulkTransfer().getDebitAccount());
        dto.setBeneficiaryName(request.getAccountName());
        dto.setBeneficiaryAccountNumber(request.getAccountNumber());
        dto.setBatchId("" + request.getBulkTransfer().getId());
        dto.setEmail(details.getEmail());
        dto.setNarration(request.getNarration());
        dto.setPaymentReference(payRef);
        dto.setBeneficiaryBankCode(request.getSortCode());
        dto.setAmount(request.getAmount());
        dto.setPayerAccountNumber(request.getBulkTransfer().getDebitAccount());
        dto.setPayerName(details.getCustomerName());


        return dto;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        List<CreditRequest> creditRequests = repo.findByBulkTransfer_Id(Long.parseLong(batchId));
        creditRequests.stream().filter(Objects::nonNull)
                .forEach(
                        i -> {
                            transferData.add(map(i));
                        }

                );


    }
}

