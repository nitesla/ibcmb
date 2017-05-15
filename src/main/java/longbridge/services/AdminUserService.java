package longbridge.services;

import longbridge.dtos.AdminUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**The {@code AdminUserService} interface provides the methods for managing an Admin user
 * @author Fortunatus Ekenachi
 * @see AdminUser
 * Created on 3/28/2017.
 */
public interface AdminUserService {

    /**
     * Returns an {@code AdminUser} having the specified userId
     * @param userId the user's Id
     * @return the admin user
     */
    AdminUser getUser(Long userId);

    /**
     * Returns an {@code AdminUser} having the specified name
     * @param name the name of the user
     * @return
     */
    AdminUser getUserByName(String name);

    /**
     * Returns a DTO of {@code AdminUserDTO} having the specified userId
     * @param userId the user's Id
     * @return the admin user
     */
    AdminUserDTO getAdminUser(Long userId);

    /**
     * Returns DTOs of admin users existing in the system
     * @return a list of the admin users
     */
    Iterable<AdminUserDTO> getUsers();

    Page<AdminUserDTO> getUsers(Pageable pageDetails);

//    /**
//     * Returns all admin users existing in the system
//     * @return a list of the admin users
//     */
//    Iterable<AdminUser> getAdminUsers();



    boolean isUsernameExist(String username) throws InternetBankingException;



    /**
     * Creates an Admin user
     * @param user the new admin user
     */
    String addUser(AdminUserDTO user) throws InternetBankingException;


    /**
     * * Deletes the admin user identified by the id
     * @param id the user's id
     */
    String deleteUser(Long id) throws InternetBankingException;


   String changeActivationStatus(Long userId) throws InternetBankingException;


    /**
     * Updates the details of the specified Admin user
     * @param user the admin user whose details are to be updated
     */
    String updateUser(AdminUserDTO user) throws InternetBankingException;

    /**
     * Resets the password of the specified Admin user
     * @param userId the admin user
     */
    String resetPassword(Long userId) throws PasswordException;

    /**
     * Replaces the old password of the admin user with the new password.
     * The new password must meet the organisation's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param changePassword
     */
    String changePassword(AdminUser user, ChangePassword changePassword) throws PasswordException;

    /**
     * Generates and sends password to an admin user
     * @param user the admin user
     */
    boolean generateAndSendPassword(AdminUser user);


    String changeDefaultPassword(AdminUser user, ChangeDefaultPassword changePassword) throws PasswordException;

}
