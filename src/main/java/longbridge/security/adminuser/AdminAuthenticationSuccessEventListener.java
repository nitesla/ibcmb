package longbridge.security.adminuser;

import longbridge.models.AdminUser;
import longbridge.models.User;
import longbridge.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {


        final AdminUser user = (AdminUser) e.getAuthentication().getDetails();
        if (user != null) {
            //loginAttemptService.loginSucceeded(auth.getRemoteAddress());
            loginAttemptService.loginSucceeded(user.getUserName(),user.getUserType().name());
        }
    }

}
