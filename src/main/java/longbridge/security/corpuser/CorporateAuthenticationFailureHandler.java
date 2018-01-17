package longbridge.security.corpuser;

import longbridge.models.CorporateUser;
import longbridge.repositories.CorporateUserRepo;
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

@Component("corporateAuthenticationFailureHandler")
public class CorporateAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    AuthenticationErrorService errorService;
    @Autowired
    CorporateUserRepo corporateUserRepo;
    @Autowired
    FailedLoginService failedLoginService;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login/corporate?error=true");
        String userName = request.getParameter("username");
        String corpId= request.getParameter("corporateId");

        CorporateUser user = corporateUserRepo.findFirstByUserNameIgnoreCaseAndCorporate_CustomerIdIgnoreCase(userName,corpId);
        if (user != null) {

            failedLoginService.loginFailed(user);
        }

        logger.error("Failed login authentication using credentials -- Username: {}, Corporate ID: {}, Password: ********",userName,corpId);

        super.onAuthenticationFailure(request, response, exception);


        String errorMessage=errorService.getMessage(exception,request);


        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}