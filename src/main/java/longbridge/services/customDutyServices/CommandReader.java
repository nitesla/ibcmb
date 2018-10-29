package longbridge.services.customDutyServices;

import longbridge.models.CustomsAreaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class CommandReader implements ItemReader<CustomsAreaCommand> , InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandReader.class);

    private RestTemplate restTemplate;
    @Value("http://132.10.200.201:8083/customduty/getncscommand")
    private String apiUrl;
    private String batchId = null;

    private int nextIndex;
    private List<CustomsAreaCommand> data  = new ArrayList<>();


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CustomsAreaCommand read() throws Exception {
        CustomsAreaCommand command = null;

        if (nextIndex < data.size()) {
            command = data.get(nextIndex);
            nextIndex++;
        }
        return command;
    }

    private List<CustomsAreaCommand> fetchDataFromAPI() {

        try {
            LOGGER.debug("Fetching data from coronation rest service via the url: {}", apiUrl);

            Map<String, String> request = new HashMap<>();
            request.put("batchId", batchId);
            LOGGER.debug("Request parameter: {}",request.toString());

            ResponseEntity<CustomsAreaCommand[]> response = restTemplate.postForEntity(apiUrl, request, CustomsAreaCommand[].class);
            CustomsAreaCommand[] restData = response.getBody();
            LOGGER.debug("Transaction Status Response: {}", restData);

            return Arrays.asList(restData);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return new ArrayList<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        data = fetchDataFromAPI();
    }
}
