package longbridge.security;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = getRequestUrl(request);
        String refererUrl = request.getHeader("referer");
        String redirectUrl = null;
        if(request.getRequestURI().equalsIgnoreCase("/corporate/corppage1")){
            redirectUrl = "/login/corporate";
        }else {
             redirectUrl = "/login/corporate?s=timeout";
        }
        redirectStrategy.sendRedirect(request, response, redirectUrl);
        request.getSession(false);
    }

    private String getRequestUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            requestURL.append("?").append(queryString);
        }
        return requestURL.toString();
    }
}
