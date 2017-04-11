package longbridge.security;

import longbridge.models.AdminUser;
import longbridge.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent e) {
//        final AdminUser user = (AdminUser) e.getAuthentication().getDetails();
//        if (user != null) {
//            loginAttemptService.loginFailed(user.getUserName(),user.getUserType().name());
//        }
   }

}