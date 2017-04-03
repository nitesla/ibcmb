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
     * @param User the user
     * @param password the password
     */
    void setPassword(RetailUser User, String password);

    /**
     * Adds a new retail user to the system
     * @param retailUser the retail user to be added
     */
    void addUser(RetailUser retailUser);

    /**
     * Resets the password of the specified Retail user
     *@param retailUser the retail user
     */
    void resetPassword(RetailUser retailUser);

    /**
     *Replaces the old password with the new password
     * The new password must meed the password policy of the organization if any one is defined
     * It is important that the password is hashed before storing it
     * @param retailUser  the retail user
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    void changePassword(RetailUser retailUser, String oldPassword, String newPassword);

    /**
     *Generates a password and sends it to the specified retail user
     * @param retailUser the user
     */
    void generateAndSendPassword(RetailUser retailUser);
}
