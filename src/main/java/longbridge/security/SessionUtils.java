package longbridge.security;

import longbridge.dtos.SettingDTO;
import longbridge.models.Code;
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
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ayoade_farooq@yahoo.com on 6/2/2017.
 */
@Service
public class SessionUtils {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private TemplateEngine templateEngine;


    @Autowired
    private ConfigurationService configService;

    public void setTimeout(HttpSession session) {
        try {
            SettingDTO settingDTO = configService.getSettingByName("SESSION_TIMEOUT");
            if (settingDTO != null && settingDTO.isEnabled()) {
                int timeout = Integer.parseInt(settingDTO.getValue());
                session.setMaxInactiveInterval(timeout * 60);
            }
        } catch (Exception e) {
            logger.error("EXCEPTION OCCURRED {}", e.getMessage());
        }

    }

    @Async
    public void sendAlert(User user) {
        try {
            SettingDTO settingDTO = configService.getSettingByName("LOGIN_ALERT");
            if (settingDTO != null && settingDTO.isEnabled()) {
                Code alertPreference = user.getAlertPreference();
                if (alertPreference == null) {
                    return;
                }
                String preference = alertPreference.getCode();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                if (firstName == null) firstName = "";
                if (lastName == null) lastName = "";
                String name = firstName + " " + lastName;
                if (name.isEmpty()) name = user.getUserName();



                String date = new SimpleDateFormat("MMM dd, yyyy ' at ' hh:mm:ss a").format(new Date());

                Context context = new Context();
                context.setVariable("customerName",name);
                context.setVariable("loginDate",new Date());

                String smsMessage = String.format(messageSource.getMessage("login.alert.message", null, locale), name, date);

                String alertSubject = String.format(messageSource.getMessage("login.alert.subject", null, locale));
                if ("SMS".equalsIgnoreCase(preference)) {

                    integrationService.sendSMS(smsMessage, user.getPhoneNumber(), alertSubject);

                } else if ("EMAIL".equalsIgnoreCase(preference)) {
                    String emailMessage = templateEngine.process("mail/login.html",context);
                    mailService.sendHtml(user.getEmail(), alertSubject, emailMessage);

                } else if ("BOTH".equalsIgnoreCase(preference)) {
                    String emailMessage = templateEngine.process("mail/login.html",context);

                    integrationService.sendSMS(smsMessage, user.getPhoneNumber(), alertSubject);
                    mailService.sendHtml(user.getEmail(), alertSubject, emailMessage);
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


    public void clearSession() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
//            SecurityContextHolder.clearContext();

            HttpSession session = attr.getRequest().getSession(false);
            if (session != null)
                session.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
