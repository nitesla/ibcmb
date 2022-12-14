package longbridge.services;

import longbridge.api.CustomerDetails;
import longbridge.api.omnichannel.dto.CustomerInfo;
import longbridge.api.omnichannel.dto.RetailUserCredentials;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.dtos.apidtos.MobileRetailUserDTO;
import longbridge.exception.PasswordException;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The {@code RetailUserService} interface provides the methods for managing retails users
 */
public interface RetailUserService {

    /**
     *Returns the specified retail user
     * @param id  the user's id
     */

    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    RetailUserDTO getUser(Long id);

    Long countUser();

    @PreAuthorize("hasAuthority('UNLOCK_RETAIL_USER')")
    String unlockUser(Long id) ;


    //    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    RetailUser getUserByName(String name);

    RetailUser getUserByEntrustId(String entrustId);

    RetailUser getUserByEmail(String email);

    //    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    RetailUserDTO getUserDTOByName(String name);

    //    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    RetailUser getUserByCustomerId(String custId);

    /**
     *Returns a list of retail users
     * @return the list of retail users
     */
    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    Iterable<RetailUserDTO> getUsers();

    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    Page<RetailUserDTO> getUsers(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_RETAIL_USER')")
    Page<RetailUserDTO> findUsers(String pattern,Pageable pageDetails);

    /**
     * Adds a new retail user to the system
     * @param user the retail user to be added
     */
    String addUser(RetailUserDTO user, CustomerDetails details) ;

    /**
     * Adds a new retail user to the system
     *
     * @param user the retail user to be added
     */
    String addUser(RetailUserDTO user) ;


    /**
     * Deletes a retail user to the system
     * @param userId the retail user's id
     */
    @PreAuthorize("hasAuthority('DELETE_RETAIL_USER')")
    String deleteUser(Long userId) ;


//    String setPassword(RetailUser user, String password) throws PasswordException;


    //@PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    String resetPassword(RetailUser user, CustResetPassword custResetPassword);

    /**
     * Updates the details of the specified retail user
     * @param user the retail user whose details are to be updated
     */
    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    boolean updateUser(RetailUserDTO user);

    /**
     * Adds the specified account to the user's list of accounts
     * @param user the specified user
     * @param account the account to be added
     * @return true if the account is successfully added for the customer
     */
    boolean AddAccount(RetailUser user, AccountDTO account);




    @PreAuthorize("hasAuthority('UPDATE_RETAIL_STATUS')")
    String changeActivationStatus(Long userId) ;


    void sendActivationCredentials(RetailUser user, String password);

    /**
     *Sets the password of the specified retail user
     * The password must meet the organisation's password policy if any one has been defined
     * It is important to hash the password before storing in the database
     * @param userId the user Id
     *
     */
    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    String resetPassword(Long userId) throws PasswordException;

    /*
    Mobile Users password reset
     */
    String resetPasswordMobileUser(RetailUser user, CustResetPassword custResetPassword);

    /**
     *Replaces the old password with the new password
     * The new password must meed the password policy of the organization if any one is defined
     * It is important that the password is hashed before storing it
     * @param retailUser  the retail user
     * @param custChangePassword the cahnge password DTO
     */
    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    String changePassword(RetailUser retailUser, CustChangePassword custChangePassword) throws PasswordException;


    void sendActivationMessage(RetailUser user );

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
    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    boolean changeAlertPreference(RetailUserDTO retailUser, AlertPref alertPreference);

//    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
//    String changeFeedBackStatus(RetailUserDTO retailUser);

    /** This tries to retrieve the username of a {@link RetailUser} using the specified information
     *
     * @param accountNumber a valid accountNumber representing one of the user's accounts
     * @param securityQuestion the user's security question
     * @param securityAnswer the answer to the user's security question
     * @return the username if the inputs are valid or an error message if the invalid
     */
    String retrieveUsername(String accountNumber, String securityQuestion, String securityAnswer);

    void increaseNoOfTokenAttempt(RetailUser user);
    void resetNoOfTokenAttempt(RetailUser user);
    String validateAccount();

    CustomerInfo getCustomerInfo(RetailUserCredentials userCredentials);


    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    String resetSecurityQuestion(Long userId);

    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    void setSecurityQuestion(Long userId);


    Page<RetailUserDTO> findUsers(RetailUserDTO user, Pageable pageDetails);
    MobileRetailUserDTO convertEntitiesToDTO(RetailUser retailUser);


    @PreAuthorize("hasAuthority('UPDATE_RETAIL_USER')")
    String changeFeedBackStatus(RetailUserDTO retailUser);


}