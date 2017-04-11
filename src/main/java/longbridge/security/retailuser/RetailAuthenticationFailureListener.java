package longbridge.security.retailuser;

import longbridge.models.RetailUser;
import longbridge.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class RetailAuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent e) {
        final RetailUser auth = (RetailUser) e.getAuthentication().getDetails();
        if (auth != null) {
            loginAttemptService.loginFailed(auth.getUserName(),auth.getUserType().toString());
        }
    }

}