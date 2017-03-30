package longbridge.services;

import longbridge.models.AdminUser;

import java.util.List;

/**The {@code AdminUserService} interface provides the methods for managing an Admin user
 * @author Fortunatus Ekenachi
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
     * Returns all admin users existing in the system
     * @return a list of the admin users
     */
    Iterable<AdminUser> getUsers();

    /**
     * Sets the password for the specified admin user.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param user the admin user
     * @param hashedPassword the hashed password
     */
    void setPassword(AdminUser user, String hashedPassword);

    /**
     * Creates an Admin user
     * @param user the new admin user
     */
    void addUser(AdminUser user);

    /**
     * Resets the password of the specified Admin user
     * @param user the admin user
     */
    void resetPassword(AdminUser user);

    /**
     * Replaces the old password of the admin user with the new password.
     * The new password must meet the organisation's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param oldPassword the old password
     * @param hashedNewPassword the new password
     */
    void changePassword(String oldPassword, String hashedNewPassword);

    /**
     * Generates and sends password to an admin user
     */
    void generateAndSendPassword(AdminUser user);


}
