package longbridge.services.bulkTransfers;

import longbridge.api.CustomerDetails;
import longbridge.models.AntiFraudData;
import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CreditRequestRepo;
import longbridge.services.IntegrationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private BulkTransferRepo transferRepo;
    private IntegrationService integrationService;
    private int nextIndex;


    @Autowired
    public BulkTransferReader(@Value("#{jobParameters[batchId]}") final String batchId) {
        this.batchId = batchId;
        nextIndex=0;
    }

    @Autowired
    void setFields(CreditRequestRepo repo, BulkTransferRepo transferRepo, IntegrationService integrationService) {
        this.repo = repo;
        this.integrationService = integrationService;
        this.transferRepo = transferRepo;

    }

    @Override
    public TransferDTO read() throws Exception {
        LOGGER.info("Reading the information of the next transfer");


        TransferDTO nextRequest = null;

        if (nextIndex < transferData.size()) {
            nextRequest = transferData.get(nextIndex);
            nextIndex++;
        }


        LOGGER.info("Found record: {}", nextRequest);


        return nextRequest;
    }


    TransferDTO map(CreditRequest request) {
        CustomerDetails details = integrationService.viewCustomerDetails(request.getBulkTransfer().getCustomerAccountNumber());
        TransferDTO dto = new TransferDTO();
        dto.setAccountName(details.getCustomerName());
        dto.setAccountNumber(request.getBulkTransfer().getCustomerAccountNumber());
        dto.setBeneficiaryName(request.getAccountName());
        dto.setBeneficiaryAccountNumber(request.getAccountNumber());
        dto.setBatchId("" + request.getBulkTransfer().getRefCode());
        dto.setEmail(details.getEmail());
        dto.setNarration(request.getNarration());
        dto.setDescription(request.getNarration());
        dto.setPaymentReference(request.getReferenceNumber());
        dto.setBeneficiaryBankCode(request.getSortCode());
        dto.setAmount(request.getAmount().toString());
        dto.setPayerAccountNumber(request.getBulkTransfer().getCustomerAccountNumber());
        dto.setPayerName(details.getCustomerName());
        dto.setAntiFraudData(new ModelMapper().map(request.getNapsAntiFraudData(), AntiFraudData.class));


        return dto;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BulkTransfer bulkTransfer = transferRepo.findFirstByRefCode(batchId);
        List<CreditRequest> creditRequests = repo.findByBulkTransfer_Id(bulkTransfer.getId());
        creditRequests.stream().filter(Objects::nonNull)
                .forEach(
                        i -> transferData.add(map(i))

                );


    }
}

