package longbridge.security.opsuser;

import longbridge.dtos.SettingDTO;
import longbridge.models.OperationsUser;
import longbridge.models.UserType;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.security.FailedLoginService;
import longbridge.security.SessionUtils;
import longbridge.services.ConfigurationService;
import longbridge.services.PasswordPolicyService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;

@Component("opAuthenticationSuccessHandler")
public class OpAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    SessionUtils sessionUtils;
    @Autowired
    FailedLoginService failedLoginService;
    private LocalDate today = LocalDate.now();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private OperationsUserRepo operationsUserRepo;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private ConfigurationService configService;
    public OpAuthenticationSuccessHandler() {
        setUseReferer(true);
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
            setUseReferer(true);
            sessionUtils.setTimeout(session);
            OperationsUser user = operationsUserRepo.findFirstByUserNameIgnoreCase(authentication.getName());
            sessionUtils.setTimeout(session);
            sessionUtils.validateExpiredPassword(user,session);
            failedLoginService.loginSucceeded(user);

        logger.info("Operations user {} successfully passed first authentication",user.getUserName());

        operationsUserRepo.updateUserAfterLogin(authentication.getName());
        super.onAuthenticationSuccess(request, response, authentication);

    }

    @Override
    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication ) throws IOException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }


    protected String determineTargetUrl(final Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isOp = operationsUserRepo.findFirstByUserNameIgnoreCase(userDetails.getUsername()).getUserType().equals(UserType.OPERATIONS);
        SettingDTO setting = configService.getSettingByName("ENABLE_OPS_2FA");
        boolean tokenAuth = false;
        if (setting != null && setting.isEnabled()) {
            tokenAuth = ("YES".equalsIgnoreCase(setting.getValue()) ? true : false);
        }
        if (tokenAuth) {
            logger.trace("Redirecting user to token authentication page");
            return "/ops/token";
        }

        if (isOp) {
            return "/ops/dashboard";
        } else {
            throw new IllegalStateException();
        }
    }


}