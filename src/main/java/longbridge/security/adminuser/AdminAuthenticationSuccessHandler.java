package longbridge.security.adminuser;

import longbridge.models.UserType;
import longbridge.repositories.AdminUserRepo;
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

@Component("adminAuthenticationSuccessHandler")
public class AdminAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private  AdminUserRepo adminUserRepo;





    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session != null) {
        	session.setMaxInactiveInterval(30 *60); //TODO this cannot be static
        }
        adminUserRepo.updateUserAfterLogin(authentication.getName());

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
          boolean isAdmin= adminUserRepo.findFirstByUserName(userDetails.getUsername()).getUserType().equals(UserType.ADMIN);
        if (isAdmin) {
            return "/admin/dashboard";
        }  else {
            throw new IllegalStateException();
        }
    }

    
   
}