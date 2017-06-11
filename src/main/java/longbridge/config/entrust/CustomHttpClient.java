package longbridge.config.entrust;


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;

//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;

/**
 * @author ayoade_farooq@yahoo.com
 */
@Service
public class CustomHttpClient {
    private final Logger LOGGER = LoggerFactory.getLogger(CustomHttpClient.class);
    //    private static final int CONNECTION_TIMEOUT = Integer.parseInt(getTimeOut());
//    private static  String FI_WSDL_URL = getServerUrl();
    private final int CONNECTION_TIMEOUT = 20000;//in milli seconds

    @Value("${ENTRUST.URL}")
    private String ENTRUST_WSDL_URL ;//="http://132.10.200.201:8080/cmb-entrust-webservicetest/ws?wsdl";


    public CustomHttpClient() {

    }

    public boolean stringIsNullOrEmpty(String arg) {
        return (arg == null) || ("".equals(arg)) || (arg.length() == 0);
    }


    public EntrustServiceResponse sendHttpRequest(String xmlFormatedMessage) throws IOException, MalformedURLException {

        String result = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        EntrustServiceResponse httpResponse = new EntrustServiceResponse();
        LOGGER.debug("\n ********************************************************************" + "\n");
        try {

            LOGGER.trace("sending http request with EndPoint URL : " + ENTRUST_WSDL_URL);
            LOGGER.trace("sending formatted message  : \n\n" + xmlFormatedMessage);
            LOGGER.trace("Timeout for plugin-adapter : " + CONNECTION_TIMEOUT / 1000 + "s");

            System.setProperty("javax.net.debug", "false");

            RequestConfig.Builder requestBuilder = RequestConfig.custom();
            requestBuilder = requestBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
            requestBuilder = requestBuilder.setConnectionRequestTimeout(CONNECTION_TIMEOUT);

            HttpClientBuilder builder = HttpClientBuilder.create();
            builder.setDefaultRequestConfig(requestBuilder.build());

            httpClient = builder.build();

            HttpPost httpPost = new HttpPost(ENTRUST_WSDL_URL);
            ByteArrayEntity postDataEntity = new ByteArrayEntity(xmlFormatedMessage.getBytes());
            httpPost.setEntity(postDataEntity);


            httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");

            response = httpClient.execute(httpPost);

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            httpResponse.setResponseCode(statusCode);

            // If the response does not enclose an entity, there is no need
            // to bother about connection release
            if (entity != null) {
                result = EntityUtils.toString(entity);
                //this is the util from apache -- we had to put this to print the raw xml response as is data
                result = StringEscapeUtils.unescapeXml(result);

                httpResponse.setResponseMessage(result);
                EntityUtils.consume(entity);
            }
            LOGGER.debug("\n the response from Service \n\n" + httpResponse.getResponseMessage() + "\n");
            LOGGER.debug("\n ********************************************************************" + "\n");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException ex) {
                LOGGER.error(" A fatal IOException has occurred {}", ex);
            }
        }
        return httpResponse;
    }
}
