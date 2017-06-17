package longbridge.security.retailuser;

import longbridge.api.CustomerDetails;
import longbridge.dtos.SettingDTO;
import longbridge.models.RetailUser;
import longbridge.models.UserType;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.SessionUtils;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@Component("retailAuthenticationSuccessHandler")
public class RetailAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    IntegrationService integrationService;
    @Autowired
    MailService mailService;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private SessionUtils sessionUtils;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private Locale locale;

    public RetailAuthenticationSuccessHandler() {
        super();
        setUseReferer(true);
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        handle(request, response, authentication);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            sessionUtils.setTimeout(session);
            RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(authentication.getName());
            sessionUtils.validateExpiredPassword(user, session);
            //session.setAttribute("user",retailUserRepo.findFirstByUserName(authentication.getName()));
            retailUserRepo.updateUserAfterLogin(authentication.getName());
            sendAlert(user);

        }
        clearAuthenticationAttributes(request);
        //request.setAttribute("");
    }

    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        final String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isUser = retailUserRepo.findFirstByUserName(userDetails.getUsername()).getUserType().equals(UserType.RETAIL);

        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
        boolean tokenAuth = false;
        if (setting != null && setting.isEnabled()) {
            tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
        }

        if (tokenAuth) {
            return "/retail/token";
        }
        if (isUser) {
            return "/retail/dashboard";
        } else {
            throw new IllegalStateException();
        }
    }

//    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
//        final HttpSession session = request.getSession(false);
//        if (session == null) {
//            return;
//        }
//        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
//    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }


    private  void sendAlert(RetailUser user) {
        try {
            SettingDTO settingDTO = configService.getSettingByName("LOGIN_ALERT");
            if (settingDTO.isEnabled()) {
                String preference = user.getAlertPreference().getCode();
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(user.getCustomerId());
                String alertMessage = String.format(messageSource.getMessage("login.alert.message", null, locale),details.getCustomerName());

                String alertSubject = String.format(messageSource.getMessage("login.alert.subject", null, locale),details.getCustomerName());
                if (preference.equalsIgnoreCase("SMS")) {

                    integrationService.sendSMS(alertMessage,details.getPhone(),  alertSubject);

                } else if (preference.equalsIgnoreCase("EMAIL")) {
                    mailService.send(details.getEmail(),alertSubject,alertMessage);

                } else if (preference.equalsIgnoreCase("BOTH")) {
                    integrationService.sendSMS(alertMessage,details.getPhone(),  alertSubject);
                    mailService.send(details.getEmail(),alertSubject,alertMessage);
                }

            }
        } catch (Exception e) {
            logger.error("EXCEPTION OCCURRED {}", e.getMessage());
        }

    }
}