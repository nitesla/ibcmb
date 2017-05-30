package longbridge.security.opsuser;

import longbridge.dtos.SettingDTO;
import longbridge.models.OperationsUser;
import longbridge.models.UserType;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.services.ConfigurationService;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;

@Component("opAuthenticationSuccessHandler")
public class OpAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private LocalDate today = LocalDate.now();

    public OpAuthenticationSuccessHandler() {
        setUseReferer(true);
    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private OperationsUserRepo operationsUserRepo;

    @Autowired
    private ConfigurationService configService;





    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session != null) {
            setUseReferer(true);
        	session.setMaxInactiveInterval(30 *60);
            OperationsUser user= operationsUserRepo.findFirstByUserName(authentication.getName());
            LocalDate date = new LocalDate(user.getExpiryDate());

            if (today.isAfter(date) || today.isEqual(date)) {
                session.setAttribute("expired-password","expired-password");
            }
        }

        operationsUserRepo.updateUserAfterLogin(authentication.getName());
        super.onAuthenticationSuccess(request, response, authentication);

    }

    @Override
    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {

         String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }


    protected String determineTargetUrl(final Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
          boolean isOp= operationsUserRepo.findFirstByUserName(userDetails.getUsername()).getUserType().equals(UserType.OPERATIONS);
        SettingDTO setting = configService.getSettingByName("ENABLE_OPS_2FA");
        boolean tokenAuth = false;
        if (setting != null && setting.isEnabled()) {
            tokenAuth = (setting.getValue().equalsIgnoreCase("yes") ? true : false);
        }
        if (tokenAuth) {
            return "/ops/token";
        }

        if (isOp) {
            return "/ops/dashboard";
        }  else {
            throw new IllegalStateException();
        }
    }

    
   
}