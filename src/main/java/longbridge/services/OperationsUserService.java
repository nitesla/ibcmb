package longbridge.services;

import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.OperationsUserDTO;
import longbridge.models.OperationsUser;

/**
 * The {@code OperationsUserService} provides the methods for managing operations users
 * @see OperationsUserDTO
 * @see OperationsUser
 * @author Fortunatus Ekenachi
 * Created on 3/29/2017.
 */
public interface OperationsUserService{

    /**
     *Returns the specified operations user
     * @param id the user's id
     * @return the Operations User
     */
    OperationsUserDTO getUser(Long id);

    /**Checks that the provided username is valid for use as any username already
     * existing will not be valid for another user
     * @param username the username
     * @return true if the username does not exist
     */
    boolean isValidUsername(String username);


    OperationsUser getUserByName(String name);


    /**
     * Returns all Operations users present in th system
     * @return list of user
     */
    Iterable<OperationsUserDTO> getUsers();

    /**
     * Returns a paginated list of users
     * @param pageDetails the page details
     * @return list of users
     */
    Page<OperationsUserDTO> getUsers(Pageable pageDetails);

    Page<OperationsUserDTO> findUsers(OperationsUserDTO example,Pageable pageDetails);

    /**
     * Sets the password for the specified Operations User.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param User the user
     * @param password the password
     */
    String setPassword(OperationsUser User, String password) throws InternetBankingException;

    /**
     * Creates an Operations User
     * @param user the new OperationsUser
     */
    String addUser(OperationsUserDTO user) throws InternetBankingException;

    /**
     * Update the details of the Operations User
     * @param user the Operations User
     */
    String updateUser(OperationsUserDTO user) throws InternetBankingException;

    /**
     * Changes the activation status of the user
     * The user can have different status such as ACTIVE, INACTIVE and LOCK
     * On creation, the user has a null status until activated by the Admin
     * @param userId the user's Id
     */
    String changeActivationStatus(Long userId) throws InternetBankingException;

    /**
     * Deletes an Operations User
     * @param userId the  Operations user's id
     */
    String deleteUser(Long userId) throws InternetBankingException;

    /**
     * Resets the password of the specified Operations user
     * @param id of the user
     */

    String resetPassword(Long id) throws InternetBankingException;

    /**
     * Replaces the old password with the new password
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * * @param oldPassword the oldPassword
     * @param oldPassword the old password
     * @param newPassword the new Password
     */

    String changePassword(OperationsUser user, String oldPassword, String newPassword) throws InternetBankingException;


    /**
     * Generates and sends a password to the specified user
     * @param user the use that will receive the new pasword
     */
    String generateAndSendPassword(OperationsUser user) throws InternetBankingException;
}
