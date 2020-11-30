package longbridge.security.adminuser;

import longbridge.dtos.SettingDTO;
import longbridge.models.AdminUser;
import longbridge.models.UserType;
import longbridge.repositories.AdminUserRepo;
import longbridge.security.SessionUtils;
import longbridge.services.ConfigurationService;
import longbridge.services.LoggedUserService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;

@Component("adminAuthenticationSuccessHandler")
public class AdminAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    SessionUtils sessionUtils;
    private final LocalDate today = LocalDate.now();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ConfigurationService configService;

    public AdminAuthenticationSuccessHandler() {

    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        sessionUtils.setTimeout(session);

        AdminUser user = adminUserRepo.findFirstByUserNameIgnoreCase(authentication.getName());
        LocalDate date = new LocalDate(user.getExpiryDate());

        if (today.isAfter(date) || today.isEqual(date)) {
            session.setAttribute("expired-password", "expired-password");
        }

        logger.info("Admin user {} successfully passed first authentication",user.getUserName());
        user.setLastLoginDate(new Date());
        user.setNoOfLoginAttempts(0);
        user.setLockedUntilDate(null);
        user.setStatus("A");
        adminUserRepo.save(user);
//        adminUserRepo.updateUserAfterLogin(authentication.getName());



        sessionUtils.sendAlert(user);
        super.onAuthenticationSuccess(request, response, authentication);

    }

    @Override
    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }


        if (loggedUserService.sessionExists(request.getSession().getId())) {

            redirectStrategy.sendRedirect(request, response, "/login/admin");
        }


//        loggedUserService.loginUser(request.getSession().getId());

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }


    protected String determineTargetUrl(final Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        boolean isAdmin = adminUserRepo.findFirstByUserNameIgnoreCase(userDetails.getUsername()).getUserType().equals(UserType.ADMIN);
        SettingDTO setting = configService.getSettingByName("ENABLE_ADMIN_2FA");
        boolean tokenAuth = false;
        if (setting != null && setting.isEnabled()) {
            tokenAuth = ("YES".equalsIgnoreCase(setting.getValue()) ? true : false);
        }

        if (tokenAuth) {
            logger.debug("Redirecting user to token authentication page");
            return "/admin/token";
        }

        if (isAdmin) {
            return "/admin/dashboard";
        } else {
            throw new IllegalStateException();
        }
    }


    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}