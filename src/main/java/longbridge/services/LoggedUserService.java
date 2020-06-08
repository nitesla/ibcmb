package longbridge.services;

/**
 * Created by ayoade_farooq@yahoo.com on 8/30/2017.
 */
public interface LoggedUserService {

    boolean sessionExists(String sessionId);
    void loginUser(String sessionId);
    void logOutUser(String sessionId);

}
