package longbridge.services;

import longbridge.models.OperationsUser;

/**
 * The {@code OperationsUserService} provides the methods for managing operations users
 * @see OperationsUser
 * Created on 3/29/2017.
 */
public interface OperationsUserService {

    /**
     *Returns the specified operations user
     * @param id the user's id
     * @return the Operations User
     */
    OperationsUser getUser(Long id);


    /**
     * Returns all Operations users present in th system
     * @return list of user
     */
    Iterable<OperationsUser> getUsers();


    /**
     * Sets the password for the specified Operations User.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param User the user
     * @param password the password
     */
    void setPassword(OperationsUser User, String password);

    /**
     * Creates an Operations User
     * @param user the new OperationsUser
     */
    boolean addUser(OperationsUser user);

    /**
     * Resets the password of the specified Operations user
     * @param user the user
     */
    void resetPassword(OperationsUser user);

    /**
     * Change password get OldPassword from user and takes in the new password
     * @param oldPassword the oldPassword
     * @param newPassword the newPassword
     */
    void changePassword(String oldPassword, String newPassword);



    boolean changePassword(OperationsUser User, String oldPassword, String newPassword);

    void generateAndSendPassword();
}
