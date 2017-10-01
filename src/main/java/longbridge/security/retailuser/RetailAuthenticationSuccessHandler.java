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
import org.springframework.context.i18n.LocaleContextHolder;
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
            logger.info("Retail user {} successfully passed first authentication",user.getUserName());

            retailUserRepo.updateUserAfterLogin(authentication.getName());
            sessionUtils.sendAlert(user);

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
            tokenAuth = ("YES".equalsIgnoreCase(setting.getValue()) ? true : false);
        }

        if (tokenAuth) {
            logger.trace("Redirecting user to token authentication page");
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



}