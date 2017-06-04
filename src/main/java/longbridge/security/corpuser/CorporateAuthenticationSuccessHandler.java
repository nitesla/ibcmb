package longbridge.security.corpuser;

import longbridge.dtos.SettingDTO;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.repositories.CorporateUserRepo;
import longbridge.security.SessionUtils;
import longbridge.services.ConfigurationService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component("corporateAuthenticationSuccessHandler")
public class CorporateAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private LocalDate today = LocalDate.now();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    SessionUtils sessionUtils;


    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        handle(request, response, authentication);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            sessionUtils.setTimeout(session);
         String s = authentication.getName();
            String userName = "";
            String corpId = "";
            if (s!=null){
                try{
                    userName = s.split(":")[0];
                    corpId = s.split(":")[1];

                }catch (Exception e){

                }
            }
            CorporateUser user = corporateUserRepo.findByUserNameAndCorporate_CustomerId(userName,corpId);
           sessionUtils.validateExpiredPassword(user,session);

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
          boolean isUser= corporateUserRepo.findFirstByUserName(userDetails.getUsername()).getUserType().equals(UserType.CORPORATE);

        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");
        boolean tokenAuth = false;
        if (setting != null && setting.isEnabled()) {
            tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
        }

        if (tokenAuth) {
            return "/corporate/token";
        }
        if (isUser) {
            return "/corporate/dashboard";
        }  else {
            throw new IllegalStateException();
        }
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}