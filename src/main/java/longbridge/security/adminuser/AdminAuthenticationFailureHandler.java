package longbridge.security.adminuser;

import longbridge.models.AdminUser;
import longbridge.repositories.AdminUserRepo;
import longbridge.security.AuthenticationErrorService;
import longbridge.security.FailedLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("adminAuthenticationFailureHandler")
public class AdminAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

   @Autowired
   AuthenticationErrorService errorService;
   @Autowired
    AdminUserRepo service;

    @Autowired
    FailedLoginService failedLoginService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login/admin?error=login_error");
        String userName = request.getParameter("username");
        AdminUser user = service.findFirstByUserNameIgnoreCase(userName);
        if (user != null) {

            failedLoginService.loginFailed(user);

        }

        logger.error("Failed login authentication using credentials -- Username: {}, Password: ********",userName);

        super.onAuthenticationFailure(request, response, exception);

       String errorMessage=errorService.getMessage(exception,request);

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}