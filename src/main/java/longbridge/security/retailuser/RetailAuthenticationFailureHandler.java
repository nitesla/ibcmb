package longbridge.security.retailuser;

import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.AuthenticationErrorService;
import longbridge.security.FailedLoginService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("retailAuthenticationFailureHandler")
public class RetailAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    FailedLoginService failedLoginService;
    @Autowired
    private AuthenticationErrorService errorService;
    @Autowired
    private RetailUserRepo service;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login/retail?error=true");
        String userName = request.getParameter("username");
        RetailUser user = service.findFirstByUserNameIgnoreCase(userName);

        if (user != null && exception.getMessage().equalsIgnoreCase("Bad credentials")){

            failedLoginService.loginFailed(user);
    }

        logger.error("Failed login authentication using credentials -- Username: {}, Password: ********",userName);

        super.onAuthenticationFailure(request, response, exception);


        String errorMessage = errorService.getMessage(exception, request);


        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}