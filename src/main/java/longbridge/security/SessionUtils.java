package longbridge.security;

import longbridge.dtos.SettingDTO;
import longbridge.models.User;
import longbridge.services.ConfigurationService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Created by ayoade_farooq@yahoo.com on 6/2/2017.
 */
@Service
public class SessionUtils {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private LocalDate today = LocalDate.now();
    @Autowired
    private ConfigurationService configService;


    public void setTimeout(HttpSession session){
        try{
            SettingDTO settingDTO=   configService.getSettingByName("SESSION_TIMEOUT");
            if (settingDTO.isEnabled()){
                int timeout= Integer.parseInt(settingDTO.getValue());
                session.setMaxInactiveInterval(timeout * 60);
            }
        }catch (Exception e){
            logger.error("EXCEPTION OCCURRED {}",e.getMessage());
        }

    }



}
