package longbridge.services;

import longbridge.models.RetailUser;

/**
 * the {@code RetailUserService}
 */
public interface RetailUserService {

    /**
     *return all the user for the retailuser
     */

    RetailUser getUser();

    /**
     *
     * @return the list of retailuser
     */
    Iterable<RetailUser> getUsers();

    /**
     *
     * @param User the user
     * @param password the password
     */
    void setPassword(RetailUser User, String password);

    void addUser();

    /**
     * Resets the password of the specified Retail user
     *
     */
    void resetPassword();

    /**
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    void changePassword(String oldPassword, String newPassword);

    /**
     *generate a password and send to user
     */
    void generateAndSendPassword();
}
