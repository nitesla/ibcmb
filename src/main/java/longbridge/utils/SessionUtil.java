package longbridge.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
@Service
public class SessionUtil {

    @Value("${app.NIP.bank.code}")
    String ADH_NIP_CODE;
    public  String generateSessionId() {
            System.out.println("here"+ADH_NIP_CODE);
            String id = ADH_NIP_CODE+""+(new SimpleDateFormat("yyMMddHHmmss").format(new Date()))+ RandomStringUtils.randomNumeric(12);

            return id;



    }



}
