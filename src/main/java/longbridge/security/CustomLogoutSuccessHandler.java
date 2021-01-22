package longbridge.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


public class CustomLogoutSuccessHandler extends
        SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String refererUrl = request.getHeader("Referer");
        logger.info("Logout from:{}",refererUrl);

        response.setHeader("pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Expires", "0");
        response.setHeader("Last-Modified", new Date().toString());
        authentication.setAuthenticated(false);

        super.onLogoutSuccess(request, response, authentication);
    }
}

