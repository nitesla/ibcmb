package longbridge.services.bulkTransfers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Ayoade Farooq
 */
@Scope(value = "step", proxyMode = ScopedProxyMode.INTERFACES)
@Component
class TransferStatusReader implements ItemReader<TransactionStatus>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferStatusReader.class);

    @Value("${rest.api.to.database.job.api.url}")
    private String apiUrl;

    private RestTemplate restTemplate;
    private final String batchId;

    private int nextIndex;
    private List<TransactionStatus> data  = new ArrayList<>();

    @Autowired
    TransferStatusReader(@Value("#{jobParameters[batchId]}") final String batchId) {
        this.batchId=batchId;
        nextIndex = 0;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public TransactionStatus read() throws Exception {

//        LOGGER.info("Reading Status data");

//        if (!isInitialized()) {
//            data = fetchDataFromAPI();
//        }

        TransactionStatus status = null;

        if (nextIndex < data.size()) {
            status = data.get(nextIndex);
            nextIndex++;
        }

        return status;
    }

//    private boolean isInitialized() {
//        return this.data != null;
//    }

    List<TransactionStatus> fetchDataFromAPI() {

       try {
           LOGGER.debug("Fetching data from NAPS service via the url: {}", apiUrl);

           Map<String, String> request = new HashMap<>();
           request.put("batchId", batchId);
           LOGGER.debug("Request parameter: {}",request.toString());

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

    @Override
    public void afterPropertiesSet() throws Exception {
        data = fetchDataFromAPI();
    }
}
