package longbridge.config;

import com.expertedge.entrustplugin.ws.EntrustMultiFactorAuthImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ayoade_farooq@yahoo.com on 5/28/2017.
 */
@Configuration
public class EntrustConfig {
    @Value("${ENTRUST.URL}")
    private String entrustUrl;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Bean
    public   EntrustMultiFactorAuthImpl port (){
        URL url = null;
        EntrustMultiFactorAuthImpl port  =null;
        try {
            url= new URL(entrustUrl);
            QName qname = new QName("http://ws.entrustplugin.expertedge.com/", "EntrustMultiFactorAuthImplService");


            javax.xml.ws.Service     service = 	javax.xml.ws.Service.create(url, qname);
            port=    service .getPort(EntrustMultiFactorAuthImpl.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception occurred with entrust config {}",e.getMessage() );
        }
        return port;
    }
}
