package longbridge.security.retailuser;

import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.SessionUtils;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;

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
            user.setLastLoginDate(new Date());
            user.setNoOfLoginAttempts(0);
            user.setLockedUntilDate(null);
            user.setStatus("A");
            retailUserRepo.save(user);
            sessionUtils.sendAlert(user);

            logger.info("Retail user {} successfully passed first authentication", user.getUserName());

        }
        clearAuthenticationAttributes(request);
    }

    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        final String targetUrl = determineTargetUrl(authentication, request);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final Authentication authentication, HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        RetailUser retailUser = retailUserRepo.findFirstByUserNameIgnoreCase(userDetails.getUsername());
//        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA"); //by GB
        boolean tokenAuth = false;
      //by GB
       /* if (setting != null && setting.isEnabled()) {
            tokenAuth = ("YES".equalsIgnoreCase(setting.getValue()) ? true : false);
        }*/

        if (sessionUtils.passwordExpired(retailUser)) {
            logger.debug("Redirecting user to reset password");
            return "/retail/reset_password";
        } else if ("Y".equals(retailUser.getResetSecurityQuestion())) {
            logger.debug("Redirecting user to change security question");
            return "/retail/reset/securityquestion";
        } else if (tokenAuth) {
            logger.debug("Redirecting user to token authentication page");
            return "/retail/token";
        } else {
            logger.debug("Redirecting user to dashboard");
            return "/retail/dashboard";
        }

    }


    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }


}