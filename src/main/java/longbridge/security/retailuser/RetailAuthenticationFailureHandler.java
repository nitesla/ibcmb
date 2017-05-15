package longbridge.security.retailuser;

import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.AuthenticationErrorService;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
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
   private  AuthenticationErrorService errorService;
    @Autowired
   private RetailUserRepo service;




    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        setDefaultFailureUrl("/login/retail?error=true");
        String userName= request.getParameter("username");
        RetailUser user= service.findFirstByUserName(userName);
        if (user!=null){
            int numOfLoginAttempts= user.getNoOfLoginAttempts();
            numOfLoginAttempts++;
            user.setNoOfLoginAttempts(numOfLoginAttempts);
            service.save(user);
        }


        super.onAuthenticationFailure(request, response, exception);


        String errorMessage=errorService.getMessage(exception,request);


        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}