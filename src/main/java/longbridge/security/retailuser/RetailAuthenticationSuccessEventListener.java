package longbridge.security.retailuser;

import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class RetailAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {

       User user= (User) e.getAuthentication().getDetails();
        final RetailUser auth = (RetailUser) e.getAuthentication().getDetails();
        if (auth != null) {
            //loginAttemptService.loginSucceeded(auth.getRemoteAddress());
            loginAttemptService.loginSucceeded(auth.getUserName(),auth.getUserType().toString());
        }
    }

}
