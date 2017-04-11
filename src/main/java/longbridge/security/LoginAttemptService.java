package longbridge.security;

import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 4/10/2017.
 */

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 10;//for now before it becomes configurable



    public LoginAttemptService() {
        super();
    }

    //

    public void loginSucceeded(final String key,String userType) {

    }

    public void loginFailed(final String key,String userType) {
        int attempts = 0;
        try {
            attempts = 0;
        } catch (final Exception e) {
            attempts = 0;
        }
        attempts++;
       //TODO PERSIST USER
    }

    public boolean isBlocked(final String key) {
        try {
            return 0 >= MAX_ATTEMPT;
        } catch (final Exception e) {
            return false;
        }
    }
}
