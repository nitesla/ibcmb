package longbridge.services;

import longbridge.models.RetailUser;

/**
 * The {@code RetailUserService} interface provides the methods for managing retails users
 */
public interface RetailUserService {

    /**
     *Returns the specified retail user
     * @param id  the user's id
     */

    RetailUser getUser(Long id);

    /**
     *Returns a list of retail users
     * @return the list of retail users
     */
    Iterable<RetailUser> getUsers();

    /**
     *Sets the password of the specified retail user
     * The password must meet the organisation's password policy if any one has been defined
     * It is important to hash the password before storing in the database
     * @param user the user
     * @param password the password
     */
    boolean setPassword(RetailUser user, String password);

    boolean addUser(RetailUser user);

    /**
     * Resets the password of the specified Retail user
     *
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     *Replaces the old password with the new password
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    boolean changePassword(RetailUser user,String oldPassword, String newPassword);

    /**
     *Generates a password and send it to the specified user
     * @param user the user
     */
    boolean generateAndSendPassword(RetailUser user);
}
