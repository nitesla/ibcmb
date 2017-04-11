package longbridge.services;

import longbridge.models.AdminUser;
import longbridge.models.Permission;
import longbridge.models.Role;
import longbridge.models.Verifiable;

import java.math.BigInteger;
import java.security.SecureRandom;

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
