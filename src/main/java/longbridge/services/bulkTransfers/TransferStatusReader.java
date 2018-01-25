package longbridge.services.bulkTransfers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

        if (dataIsNotInitialized()) {
            data = fetchDataFromAPI();
        }

        TransactionStatus status = null;

        if (nextIndex < data.size()) {
            status = data.get(nextIndex);
            nextIndex++;
        }

        return status;
    }

    private boolean dataIsNotInitialized() {
        return this.data == null;
    }

    private List<TransactionStatus> fetchDataFromAPI() {

       try {
           LOGGER.debug("Fetching  data from an external API by using the url: {}", apiUrl);

           Map<String, String> request = new HashMap<>();
           request.put("batchId", batchId);

           ResponseEntity<TransactionStatus[]> response = restTemplate.postForEntity(apiUrl, request, TransactionStatus[].class);
           TransactionStatus[] restData = response.getBody();
           LOGGER.debug("Transaction Status Response: {}", restData);

           return Arrays.asList(restData);
       }
       catch (Exception e){
           LOGGER.error("Error calling NAPS web service",e);
       }
       return new ArrayList<>();
    }
}
