package longbridge.services;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.CorporateUserDTO;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;


/**The {@code CorporateUserService} interface provides the methods for managing a corporate user
 * @author Fortunatus Ekenachi
 * @see CorporateUser
 */
public interface CorporateUserService{

    /**
     * Returns a corporate user specified by the {@code id}
     * @param id the corporate user's id
     * @return the user
     */
    CorporateUserDTO getUser(Long id);

    /**
     * Returns all the corporate users for the corporate customer
     * @param Corporate  the corporate customer
     * @return a list of the corporate users
     */
    Iterable<CorporateUserDTO> getUsers(Corporate Corporate);
    
    Page<CorporateUserDTO> getUsers(Corporate Corporate, Pageable pageDetails);

    /**
     * Returns all the corporate users in the system
     * @return a list of the corporate users
     */
    Iterable<CorporateUser> getUsers();

    Page<CorporateUserDTO> getUsers(Pageable pageDetails);
    /**
     * Sets the password for the specified corporate user
     * The password must meet the organisation's password policy if any one is defined
     * It is important that the password is hashed before saving
     * @param user the corporate user
     * @param hashedPassword the hashed password
     */
    void setPassword(CorporateUser user, String hashedPassword);

    /**
     * Updates the details of the specified corporate customer
     * @param user the corporate user
     */
    boolean updateUser(CorporateUser user);

    /**
     * Adds a corporate user to a corporate customer
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
     * @param userId the corporate user's id
     */
    void deleteUser(Long userId);

    /**
     * Enables the corporate user. This allows the corporate user to perform
     * operations with the required permissions
     * @param user the corporate user
     */
    void enableUser(CorporateUser user);

    /**
     * Disables the corporate user. A disabled user cannot access the system.
     * @param user disables the corporate user
     */
    void disableUser(CorporateUser user);
    
    /**
     * Temporarily Locks the corporate user
     * @param user the corporate user
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
