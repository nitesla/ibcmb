package longbridge.services;

import longbridge.api.CustomerDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.forms.AlertPref;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The {@code RetailUserService} interface provides the methods for managing retails users
 */
public interface RetailUserService {

    /**
     *Returns the specified retail user
     * @param id  the user's id
     */
    RetailUserDTO getUser(Long id);

    RetailUser getUserByName(String name);

    RetailUserDTO getUserDTOByName(String name);

    RetailUser getUserByCustomerId(String custId);

    /**
     *Returns a list of retail users
     * @return the list of retail users
     */
    Iterable<RetailUserDTO> getUsers();
    
    Page<RetailUserDTO> getUsers(Pageable pageDetails);




    /**
     * Adds a new retail user to the system
     * @param user the retail user to be added
     */

    String addUser(RetailUserDTO user, CustomerDetails details) throws InternetBankingException;

    /**
     * Deletes a retail user to the system
     * @param userId the retail user's id

     */

    String deleteUser(Long userId) throws InternetBankingException;


//    String setPassword(RetailUser user, String password) throws PasswordException;


    boolean resetPassword(RetailUser user, String newPassword);

    /**
     * Updates the details of the specified retail user
     * @param user the retail user whose details are to be updated
     */

    boolean updateUser(RetailUserDTO user);

    /**
     * Adds the specified account to the user's list of accounts
     * @param user the specified user
     * @param account the account to be added
     * @return true if the account is successfully added for the customer
     */
    boolean AddAccount(RetailUser user, AccountDTO account);





    String changeActivationStatus(Long userId) throws InternetBankingException;


    /**
     *Sets the password of the specified retail user
     * The password must meet the organisation's password policy if any one has been defined
     * It is important to hash the password before storing in the database
     * @param userId the user Id
     *
     */
    String resetPassword(Long userId) throws PasswordException;

    /**
     *Replaces the old password with the new password
     * The new password must meed the password policy of the organization if any one is defined
     * It is important that the password is hashed before storing it
     * @param retailUser  the retail user
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    boolean changePassword(RetailUserDTO retailUser, String oldPassword, String newPassword);


    /**
     *Generates a password and send it to the specified user
     * @param user the user
     */
//    boolean generateAndSendPassword(RetailUser user);

    /** This sets the Alert preference of the specified user. Alert preference may
     * be SMS, EMAIL or BOTH
     * @param retailUser
     * @param alertPreference
     * @return
     */
    boolean changeAlertPreference(RetailUserDTO retailUser, AlertPref alertPreference);

    /** This tries to retrieve the username of a {@link RetailUser} using the specified information
     * 
     * @param accountNumber a valid accountNumber representing one of the user's accounts
     * @param securityQuestion the user's security question
     * @param securityAnswer the answer to the user's security question
     * @return the username if the inputs are valid or an error message if the invalid
     */
    String retrieveUsername(String accountNumber, String securityQuestion, String securityAnswer);;
    
    
    
}
