package longbridge.security.adminuser;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.stereotype.Component;

@Component
public class TestListener implements ApplicationListener<AuthenticationFailureCredentialsExpiredEvent> {


    @Override
    public void onApplicationEvent(AuthenticationFailureCredentialsExpiredEvent authenticationFailureCredentialsExpiredEvent) {


    }
}