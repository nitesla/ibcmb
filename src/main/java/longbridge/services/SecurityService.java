package longbridge.services;

/**
 * The {@code SecurityService} interface provides the methods for managing roles, profiles and permissions
 * @author Fortunatus Ekenachi
 * Created on 3/28/2017.
 */
public interface SecurityService {


    /** Generates a random password of 12 characters
     *
     * @return a random password string
     */
    String generatePassword();


}
