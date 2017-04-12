package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.AdminUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.RetailUser;
import longbridge.models.Verifiable;

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

    /**
     * Sets the password for the specified admin user.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param user the admin user
     * @param hashedPassword the hashed password
     */
    boolean setPassword(AdminUser user, String hashedPassword);

    /**
     * Creates an Admin user
     * @param user the new admin user
     */
    boolean addUser(AdminUserDTO user);


    /**
     * * Deletes the admin user identified by the id
     * @param id the user's id
     */
    void deleteUser(Long id);


    /**
     * Updates the details of the specified Admin user
     * @param user the admin user whose details are to be updated
     */
    boolean updateUser(AdminUserDTO user);

    /**
     * Resets the password of the specified Admin user
     * @param userId the admin user
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * Replaces the old password of the admin user with the new password.
     * The new password must meet the organisation's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    boolean changePassword(AdminUser user, String oldPassword, String newPassword);

    /**
     * Generates and sends password to an admin user
     * @param user the admin user
     */
    boolean generateAndSendPassword(AdminUser user);


}
