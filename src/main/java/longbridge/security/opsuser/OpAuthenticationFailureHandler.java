package longbridge.security.opsuser;

import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.repositories.OperationsUserRepo;
import longbridge.security.AuthenticationErrorService;
import longbridge.security.FailedLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("opAuthenticationFailureHandler")
public class OpAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

   @Autowired
   AuthenticationErrorService errorService;
   @Autowired
    OperationsUserRepo service;
    @Autowired
    FailedLoginService failedLoginService;



    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login/ops?error=true");
        String userName = request.getParameter("username");
        OperationsUser user = service.findFirstByUserName(userName);
        if (user != null) {

            failedLoginService.loginFailed(user);

        }
        super.onAuthenticationFailure(request, response, exception);

       String errorMessage=errorService.getMessage(exception,request);

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}