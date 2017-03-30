package longbridge.services;

import java.util.Date;

import longbridge.models.Corporate;
import longbridge.models.CorporateUser;


/**The {@code CorporateUserService} interface provides the methods for managing a corporate user
 * @author Fortunatus Ekenachi
 */
public interface CorporateUserService {

    /**
     * Returns a corporate user
     * @param id the corporate user's id
     * @return the user
     */
    CorporateUser getUser(Long id);

    /**
     * Returns all the corporate users for the corporate customer
     * @param Corporate  the corporate customer
     * @return a list of the corporate users
     */
    Iterable<CorporateUser> getUsers(Corporate Corporate);

    /**
     * Returns all the corporate users in the system
     * @return a list of the corporate users
     */
    Iterable<CorporateUser> getUsers();

    /**
     * Sets the password for the specified corporate user
     * The password must meet the organisation's password policy if any one is defined
     * It is important that the password is hashed before saving
     * @param user the corporate user
     * @param hashedPassword the hashed password
     */
    void setPassword(CorporateUser user, String hashedPassword);

    /**
     * Adds a corporate user to corporate customer
     * @param user the corporate user
     */
    void addUser(CorporateUser user);

    /**
     * resets the password for the specified corporate user
     * @param user the corporate user
     */
    void resetPassword(CorporateUser user);

    /**
     * Deletes the specified corporate user
     * @param user the corporate user
     */
    void deleteUser(CorporateUser user);

    /**
     * Enables the corporate user
     * @param user the corporate user
     */
    void enableUser(CorporateUser user);

    /**
     * Disables the corporate user
     * @param user disables the corporate user
     */
    void disableUser(CorporateUser user);
    
    /**
     * Temporarily Locks the corporate user
     * @param user disables the corporate user
     */
    void lockUser(CorporateUser user, Date unlockat);

    /**
     * Replaces the old password with the new password for the specified corporate user.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param user the corporate user
     * @param oldPassword the old password
     * @param newPassword the hashed new password
     */
    void changePassword(CorporateUser user, String oldPassword, String newPassword);

    /**
     * Generates and sends a password to the specified user
     * @param user the corporate user
     */
    void generateAndSendPassword(CorporateUser user);
}
