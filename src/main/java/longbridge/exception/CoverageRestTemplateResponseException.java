package longbridge.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CoverageRestTemplateResponseException implements ResponseErrorHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

         if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {

                logger.info("Requested resources not available confirm");
            }

    }
}
}
