package longbridge.services;

import longbridge.models.OperationsUser;

/**
 * The {@code OperationsUserService} i
 *
 * Created on 3/29/2017.
 */
public interface OperationsUserService {

<<<<<<< HEAD
    /**
     *
     * @param id the userid
     * @return the OperationsUser
     */
    OperationsUser getUser(Long id);

    /**
     * Returns all Operation user present in th system
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
     * Create an OperationsUser
     * @param user the new OperationsUser
     */
    void addUser(OperationsUser user);

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
=======
    OperationsUser getUser(Long id);

    Iterable<OperationsUser> getUsers();



    boolean  addUser(OperationsUser User);

    void resetPassword(OperationsUser User,String newPassword);

    boolean changePassword(OperationsUser User,String oldPassword, String newPassword);
>>>>>>> OLUGINGIN

    void generateAndSendPassword();
}
