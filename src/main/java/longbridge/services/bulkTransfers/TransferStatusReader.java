package longbridge.services.bulkTransfers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Ayoade Farooq
 */
@Scope(value = "step", proxyMode = ScopedProxyMode.INTERFACES)
class TransferStatusReader implements ItemReader<TransactionStatus> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferStatusReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final String batchId;

    private int nextIndex;
    private List<TransactionStatus> data;

    TransferStatusReader(@Value("#{jobParameters[batchId]}") final String batchId, String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.batchId=batchId;
        this.restTemplate = restTemplate;
        nextIndex = 0;
    }

    @Override
    public TransactionStatus read() throws Exception {
        LOGGER.info("Reading the information of the next transaction");

        if (dataIsNotInitialized()) {
            data = fetchDataFromAPI();
        }

        TransactionStatus status = null;

        if (nextIndex < data.size()) {
            status = data.get(nextIndex);
            nextIndex++;
        }

        LOGGER.info("Found transaction: {}", status);

        return status;
    }

    private boolean dataIsNotInitialized() {
        return this.data == null;
    }

    private List<TransactionStatus> fetchDataFromAPI() {
        LOGGER.debug("Fetching  data from an external API by using the url: {}", apiUrl);

        Map<String,String> request = new HashMap<>();
        request.put("batchId" ,batchId);

        ResponseEntity<TransactionStatus[]> response = restTemplate.postForEntity(apiUrl,request, TransactionStatus[].class);
        TransactionStatus[] restData = response.getBody();
        LOGGER.debug("Found {} data", restData.length);

        return Arrays.asList(restData);
    }
}
