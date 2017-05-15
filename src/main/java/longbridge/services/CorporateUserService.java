package longbridge.services;

import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;


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


    CorporateUser getUserByName(String username);

    /**
     * Returns all the corporate users for the corporate customer
     * @param Corporate  the corporate customer
     * @return a list of the corporate users
     */
    Iterable<CorporateUserDTO> getUsers(Corporate Corporate);
    
    Page<CorporateUserDTO> getUsers(Long corpId, Pageable pageDetails);

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
    void setPassword(CorporateUser user, String hashedPassword) throws InternetBankingException;

    /**
     * Updates the details of the specified corporate customer
     * @param user the corporate user
     */
    String updateUser(CorporateUserDTO user) throws InternetBankingException;

    /**
     * Adds a corporate user to a corporate customer
     * @param user the corporate user
     */
    String addUser(CorporateUserDTO user);

    /**
     * resets the password for the specified corporate user
     * @param user the corporate user
     */
    String resetPassword(CorporateUser user)throws InternetBankingException;

    /**
     * Deletes the specified corporate user
     * @param userId the corporate user's id
     */
    String deleteUser(Long userId) throws InternetBankingException;
    
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
    void changePassword(CorporateUser user, String oldPassword, String newPassword) throws InternetBankingException;

    /**
     * Generates and sends a password to the specified user
     * @param user the corporate user
     */
    void generateAndSendPassword(CorporateUser user);
}
