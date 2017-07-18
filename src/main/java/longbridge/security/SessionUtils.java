package longbridge.security;

import longbridge.api.CustomerDetails;
import longbridge.dtos.SettingDTO;
import longbridge.models.RetailUser;
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
import org.springframework.stereotype.Service;

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
            if (settingDTO.isEnabled()) {
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
            if (settingDTO.isEnabled()) {
                String preference = user.getAlertPreference().getCode();
                String alertMessage = String.format(messageSource.getMessage("login.alert.message", null, locale),user.getFirstName() + " "+ user.getLastName());

                String alertSubject = String.format(messageSource.getMessage("login.alert.subject", null, locale));
                if (preference.equalsIgnoreCase("SMS")) {

                    integrationService.sendSMS(alertMessage,user.getPhoneNumber(),  alertSubject);

                } else if (preference.equalsIgnoreCase("EMAIL")) {
                    mailService.send(user.getEmail(),alertSubject,alertMessage);

                } else if (preference.equalsIgnoreCase("BOTH")) {
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

}
