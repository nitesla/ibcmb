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

    /**
     * Adds a new retail user to the system
     * @param user the retail user to be added
     */

    boolean addUser(RetailUser user);

    /**
     * Deletes a retail user to the system
     * @param userId the retail user's id
     */

    void deleteUser(Long userId);

    /**
     * Updates the details of the specified retail user
     * @param user the retail user whose details are to be updated
     */

    boolean updateUser(RetailUser user);

    /**
     * Resets the password of the specified Retail user
     *@param user the retail user
     */

    boolean resetPassword(RetailUser user, String newPassword);

    /**
     *Replaces the old password with the new password
     * The new password must meed the password policy of the organization if any one is defined
     * It is important that the password is hashed before storing it
     * @param retailUser  the retail user
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    boolean changePassword(RetailUser retailUser, String oldPassword, String newPassword);


    /**
     *Generates a password and send it to the specified user
     * @param user the user
     */
    boolean generateAndSendPassword(RetailUser user);
}
