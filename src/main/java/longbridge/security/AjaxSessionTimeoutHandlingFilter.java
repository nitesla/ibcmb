package longbridge.security;

/**
 * Created by Fortune on 5/9/2018.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.security.web.util.ThrowableCauseExtractor;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxSessionTimeoutHandlingFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(AjaxSessionTimeoutHandlingFilter.class);

    private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    private int customSessionExpiredErrorCode = 401;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try
        {
            chain.doFilter(request, response);

            logger.debug("Chain processed normally");
        }
        catch (IOException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
            RuntimeException ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);

            if (ase == null)
            {
                ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
            }

            if (ase != null)
            {
                if (ase instanceof AuthenticationException)
                {
                    throw ase;
                }
                else if (ase instanceof AccessDeniedException)
                {

                    if (authenticationTrustResolver.isAnonymous(SecurityContextHolder.getContext().getAuthentication()))
                    {
                        logger.info("User session expired or not logged in yet");
                        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");

                        if ("XMLHttpRequest".equals(ajaxHeader))
                        {
                            logger.info("Ajax call detected, send {} error code", this.customSessionExpiredErrorCode);
                            HttpServletResponse resp = (HttpServletResponse) response;
                            resp.sendError(this.customSessionExpiredErrorCode, "Session Timeout. Refresh your browser");
                        }
                        else
                        {
                            logger.info("Redirect to login page");
                            throw ase;
                        }
                    }
                    else
                    {
                        throw ase;
                    }
                }
            }

        }
    }

    private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer
    {
        /**
         * @see ThrowableAnalyzer#initExtractorMap()
         */
        protected void initExtractorMap()
        {
            super.initExtractorMap();

            registerExtractor(ServletException.class, throwable -> {
                ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
                return ((ServletException) throwable).getRootCause();
            });
        }

    }

    public void setCustomSessionExpiredErrorCode(int customSessionExpiredErrorCode)
    {
        this.customSessionExpiredErrorCode = customSessionExpiredErrorCode;
    }

}
