package longbridge.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class RestTemplateResponseException implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (
                response.getStatusCode().series() ==HttpStatus.Series.CLIENT_ERROR
                        || response.getStatusCode().series() ==HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

         if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new InternetBankingException("Resources Not Available, check if customer have the resources requested for");
            }

    }
}
}
