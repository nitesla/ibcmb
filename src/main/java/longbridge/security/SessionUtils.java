package longbridge.security;

import longbridge.dtos.SettingDTO;
import longbridge.models.CorporateUser;
import longbridge.models.User;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Created by ayoade_farooq@yahoo.com on 6/2/2017.
 */
@Service
public class SessionUtils {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    IntegrationService integrationService;

    @Autowired
    MailService mailService;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    private ConfigurationService configService;

    public void setTimeout(HttpSession session) {
        try {
            SettingDTO settingDTO = configService.getSettingByName("SESSION_TIMEOUT");
            if ( settingDTO!=null && settingDTO.isEnabled()) {
                int timeout = Integer.parseInt(settingDTO.getValue());
                session.setMaxInactiveInterval(timeout * 60);
            }
        } catch (Exception e) {
            logger.error("EXCEPTION OCCURRED {}", e.getMessage());
        }

    }

    @Async
    public  void sendAlert(User user) {
        try {
            SettingDTO settingDTO = configService.getSettingByName("LOGIN_ALERT");
            if (settingDTO!=null && settingDTO.isEnabled()) {
                String preference = user.getAlertPreference().getCode();
                String firstName= user.getFirstName();
                String  lastName = user.getLastName();
                if (firstName==null)firstName="";
                if (lastName==null)lastName="";
                String name = firstName + " "+ lastName;
                if (name.isEmpty()) name=user.getUserName();

                String alertMessage = String.format(messageSource.getMessage("login.alert.message", null, locale),name);

                String alertSubject = String.format(messageSource.getMessage("login.alert.subject", null, locale));
                if ("SMS".equalsIgnoreCase(preference)) {

                    integrationService.sendSMS(alertMessage,user.getPhoneNumber(),  alertSubject);

                } else if ("EMAIL".equalsIgnoreCase(preference)) {
                    mailService.send(user.getEmail(),alertSubject,alertMessage);

                } else if ("BOTH".equalsIgnoreCase(preference)) {
                    integrationService.sendSMS(alertMessage,user.getPhoneNumber(),  alertSubject);
                    mailService.send(user.getEmail(),alertSubject,alertMessage);
                }

            }
        } catch (Exception e) {
            logger.error("EXCEPTION OCCURRED {}", e);
        }

    }

    public void validateExpiredPassword(User user, HttpSession session) {
        try {

            if (user.getExpiryDate() != null && session != null) {

                LocalDate date = new LocalDate(user.getExpiryDate());

                if (LocalDate.now().isAfter(date) || LocalDate.now().isEqual(date)) {

                    session.setAttribute("expired-password", "expired-password");
                 //   session.setMaxInactiveInterval(60);


                }


            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public boolean checkFirstTimeLogin(CorporateUser user, HttpSession session) {
        try {

            if (user.getIsFirstTimeLogon() != null && session != null) {

                if ("Y".equals(user.getIsFirstTimeLogon())) {
                    session.setAttribute("first-time-logon", "first-time-logon");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    public void clearSession() {
//        try {
//            ServletRequestAttributes attr = (ServletRequestAttributes)
//                    RequestContextHolder.currentRequestAttributes();
////            SecurityContextHolder.clearContext();
//
//            HttpSession session = attr.getRequest().getSession(false);
//            if (session != null)
//                session.invalidate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
