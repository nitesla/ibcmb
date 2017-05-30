package longbridge.security.retailuser;

import longbridge.dtos.SettingDTO;
import longbridge.models.RetailUser;
import longbridge.models.UserType;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.ConfigurationService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component("retailAuthenticationSuccessHandler")
public class RetailAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private LocalDate today = LocalDate.now();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private RetailUserRepo retailUserRepo;

    @Autowired
    private ConfigurationService configService;


    public RetailAuthenticationSuccessHandler() {
        super();
        setUseReferer(true);
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        handle(request, response, authentication);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60); //TODO this cannot be static
            RetailUser user = retailUserRepo.findFirstByUserName(authentication.getName());
            LocalDate date = new LocalDate(user.getExpiryDate());

            if (today.isAfter(date) || today.isEqual(date)) {
                session.setAttribute("expired-password", "expired-password");
            }
            //session.setAttribute("user",retailUserRepo.findFirstByUserName(authentication.getName()));
            retailUserRepo.updateUserAfterLogin(authentication.getName());

        }
        clearAuthenticationAttributes(request);
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
}