package longbridge.security;

import longbridge.dtos.SettingDTO;
import longbridge.models.CorporateUser;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import longbridge.services.SettingsService;
import org.apache.commons.lang3.StringUtils;
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

    private final Locale locale = LocaleContextHolder.getLocale();



    @Autowired
    private SettingsService configService;

    public void setTimeout(HttpSession session) {
        try {
            SettingDTO settingDTO = configService.getSettingByName("SESSION_TIMEOUT");
            if (settingDTO != null && settingDTO.isEnabled()) {
                int timeout = Integer.parseInt(settingDTO.getValue());
                session.setMaxInactiveInterval(timeout * 60);
            }
        } catch (Exception e) {
            logger.error("EXCEPTION OCCURRED ", e);
        }

    }

    @Async
    public void sendAlert(User user) {
        logger.info("TEMPLATE2 {}", user.getEmailTemplate());

        try {
            SettingDTO settingDTO = configService.getSettingByName("LOGIN_ALERT");
            if (settingDTO != null && settingDTO.isEnabled()) {
                String alertPreference = user.getAlertPreference();
                logger.info("alertPref {}", user.getAlertPreference());
                if (alertPreference == null) {
                    return;
                }
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                if (firstName == null) firstName = "";
                if (lastName == null) lastName = "";
                String name = StringUtils.capitalize(firstName.toLowerCase());
                if (name.isEmpty()) name = user.getUserName();

                String date = new SimpleDateFormat("MMM dd, yyyy ' at ' hh:mm:ss a").format(new Date());

               String alertSub="login.alert.subject";
                if(user.getEmailTemplate()!=null)
                {
                    alertSub="login.alert.subject.Mobile";
                }


                String smsMessage = String.format(messageSource.getMessage("login.alert.message", null, locale), name, date);

                String alertSubject = messageSource.getMessage(alertSub, null, locale);
                logger.info("alertBen {}",alertSubject);

                if ("SMS".equalsIgnoreCase(alertPreference)) {
                    integrationService.sendSMS(smsMessage, user.getPhoneNumber(), alertSubject);

                } else if ("EMAIL".equalsIgnoreCase(alertPreference)) {
                    sendMail(user, alertSubject);

                } else if ("BOTH".equalsIgnoreCase(alertPreference)) {
                    integrationService.sendSMS(smsMessage, user.getPhoneNumber(), alertSubject);
                    sendMail(user, alertSubject);
                }

            }
        } catch (Exception e) {
            logger.error("EXCEPTION OCCURRED {}", e);
        }

    }

    private void sendMail(User user, String subject){

        String fullName = user.getFirstName()+" "+user.getLastName();
        logger.info("emailTemplate {}",user.getEmailTemplate());
        if(user.getEmailTemplate()==null)user.setEmailTemplate("mail/login.html");
        logger.info("the user full name {}",fullName);
        Context context = new Context();
        context.setVariable("fullName",fullName);
        context.setVariable("loginDate",new Date());

        Email email = new Email.Builder().setRecipient(user.getEmail())
                                         .setSubject(subject)
                                         .setTemplate(user.getEmailTemplate())
                                         .build();
        mailService.sendMail(email,context);
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

    public boolean passwordExpired(User user) {

            if (user.getExpiryDate() != null) {

                LocalDate date = new LocalDate(user.getExpiryDate());
                return LocalDate.now().isAfter(date) || LocalDate.now().isEqual(date);
            }
            return false;
    }

    public void checkSecurityQuestionReset(User user, HttpSession session) {
        try {
            String resetFlag;

            if(user instanceof RetailUser){
                RetailUser retailUser = (RetailUser)user;
                resetFlag = retailUser.getResetSecurityQuestion();
                if("Y".equals(resetFlag)){
                    session.setAttribute("reset-security-question", "reset-security-question");

                }
            }
            if(user instanceof CorporateUser){
                CorporateUser corporateUser = (CorporateUser)user;
                resetFlag = corporateUser.getResetSecurityQuestion();
                if("Y".equals(resetFlag)){
                    session.setAttribute("reset-security-question", "reset-security-question");

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
