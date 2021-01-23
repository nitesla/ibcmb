package longbridge.services;

import longbridge.dtos.OperationsUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.ChangePassword;
import longbridge.models.OperationsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The {@code OperationsUserService} provides the methods for managing operations users
 * @see OperationsUserDTO
 * @see OperationsUser
 * @author Fortunatus Ekenachi
 * Created on 3/29/2017.
 */
public interface OperationsUserService{

    Long countOps();
    /**
     *Returns the specified operations user
     * @param id the user's id
     * @return the Operations User
     */
    @PreAuthorize("hasAuthority('GET_OPS_USER')")
    OperationsUserDTO getUser(Long id);

//    @PreAuthorize("hasAuthority('GET_OPS_USER')")
//    OperationsUser getUser(Long id);

    /**Checks that the provided username is valid for use as any username already
     * existing will not be valid for another user
     * @param username the username
     * @return true if the username does not exist
     */
    boolean userExists(String username);


    @PreAuthorize("hasAuthority('GET_OPS_USER')")
    OperationsUser getUserByName(String name);



    /**
     * Returns all Operations users present in th system
     * @return list of user
     */
    @PreAuthorize("hasAuthority('GET_OPS_USER')")
    Iterable<OperationsUserDTO> getUsers();

    /**
     * Returns a paginated list of users
     * @param pageDetails the page details
     * @return list of users
     */
    @PreAuthorize("hasAuthority('GET_OPS_USER')")
    Page<OperationsUserDTO> getUsers(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_OPS_USER')")
    Page<OperationsUserDTO> findUsers(OperationsUserDTO example,Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_OPS_USER')")
    Page<OperationsUserDTO> findUsers(String pattern,Pageable pageDetails);
    /**
     * Sets the password for the specified Operations User.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param User the user
     * @param password the password
     */
    @PreAuthorize("hasAuthority('SET_OPS_PASSWORD')")
    String setPassword(OperationsUser User, String password) throws InternetBankingException;

    /**
     * Creates an Operations User
     * @param user the new OperationsUser
     */
    @PreAuthorize("hasAuthority('ADD_OPS_USER')")
    String addUser(OperationsUserDTO user) throws InternetBankingException;

    /**
     * Update the details of the Operations User
     * @param user the Operations User
     */
    @PreAuthorize("hasAuthority('UPDATE_OPS_USER')")
    String updateUser(OperationsUserDTO user) throws InternetBankingException;

    /**
     * Changes the activation status of the user
     * The user can have different status such as ACTIVE, INACTIVE and LOCK
     * On creation, the user has a null status until activated by the Admin
     * @param userId the user's Id
     */
    @PreAuthorize("hasAuthority('UPDATE_OPS_STATUS')")
    String changeActivationStatus(Long userId) throws InternetBankingException;

    /**
     * Deletes an Operations User
     * @param userId the  Operations user's id
     */
    @PreAuthorize("hasAuthority('DELETE_OPS_USER')")
    String deleteUser(Long userId) throws InternetBankingException;


    OperationsUser createUserOnEntrustAndSendCredentials(OperationsUser opsUser);

    /**
     * Resets the password of the specified Operations user
     * @param id of the user
     */
    @PreAuthorize("hasAuthority('RESET_OPS_PASSWORD')")
    String resetPassword(Long id) throws InternetBankingException;




    /**
     * Replaces the old password with the new password
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * * @param oldPassword the oldPassword
     * @param changePassword
     */
    @PreAuthorize("hasAuthority('OPERATOR')")
    String changePassword(OperationsUser user, ChangePassword changePassword) throws InternetBankingException;


    @PreAuthorize("hasAuthority('OPS_CHANGE_PASSWORD')")
    String changeDefaultPassword(OperationsUser user, ChangeDefaultPassword changePassword) throws PasswordException;

    String resetPassword(String username) throws InternetBankingException;


    void sendActivationCredentials(OperationsUser user, String password);
}
