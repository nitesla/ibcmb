package longbridge.services;

import longbridge.dtos.CorpCorporateUserDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


/**The {@code CorporateUserService} interface provides the methods for managing a corporate user
 * @author Fortunatus Ekenachi
 * @see CorporateUser
 */
public interface CorporateUserService{

    /**
     * Returns a corporate user specified by the {@code id}
     * @param id the corporate user's id
     * @return the user
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    CorporateUserDTO getUser(Long id);

    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    CorporateUserDTO getUserDTOByName(String name);

    //@PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    CorporateUser getUserByName(String username);

    /*CorporateUser getUserByCustomerId(String custId);*/

    /**
     * Returns all the corporate users for the corporate customer
     * @param Corporate  the corporate customer
     * @return a list of the corporate users
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    Iterable<CorporateUserDTO> getUsers(Corporate Corporate);

    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    Page<CorporateUserDTO> getUsers(Long corpId, Pageable pageDetails);

    /**
     * Returns all the corporate users in the system
     * @return a list of the corporate users
     */
    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    Iterable<CorporateUser> getUsers();

    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    Iterable<CorporateUserDTO> getUsers(Long corpId);

    @PreAuthorize("hasAuthority('GET_CORPORATE_USER')")
    Page<CorporateUserDTO> getUsers(Pageable pageDetails);

    /**
     * Updates the details of the specified corporate customer
     * @param user the corporate user
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_USER')")
    String updateUser(CorporateUserDTO user) throws InternetBankingException;

    /**
     * Adds a corporate user to a corporate customer
     * @param user the corporate user
     */
    @PreAuthorize("hasAuthority('ADD_CORPORATE_USER')")
    String addUser(CorporateUserDTO user);

    @PreAuthorize("hasAuthority('CORP_USER_STATUS')")
    @Transactional
    String changeActivationStatus(Long userId) throws InternetBankingException;

    @PreAuthorize("hasAuthority('CORP_USER_STATUS')")
    String changeCorpActivationStatus(Long userId) throws InternetBankingException;

    /**
     * resets the password for the specified corporate user
     * @param userId
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_USER')")
    String resetPassword(Long userId) throws PasswordException;

    /**
     * Deletes the specified corporate user
     * @param userId the corporate user's id
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE_USER')")
    String deleteUser(Long userId) throws InternetBankingException;
    
    /**
     * Temporarily Locks the corporate user
     * @param user the corporate user
     */
    void lockUser(CorporateUser user, Date unlocked);

    /**
     * Replaces the old password with the new password for the specified corporate user.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param user the corporate user
     * @param changePassword the change password
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_USER')")
    String changePassword(CorporateUser user, CustChangePassword changePassword) throws PasswordException;

    /**
     * Generates and sends a password to the specified user
     * @param user the corporate user
     */
    void generateAndSendPassword(CorporateUser user);

    /** This sets the Alert preference of the specified user. Alert preference may
     * be SMS, EMAIL or BOTH
     * @param
     * @param alertPreference
     * @return
     */
    boolean changeAlertPreference(CorporateUserDTO corporateUser, AlertPref alertPreference);

    public String addUserFromCorporateAdmin(CorpCorporateUserDTO user) throws InternetBankingException;

    public String resetPassword(CorporateUser user, CustResetPassword changePassword);

    List<CorporateUserDTO> getUsersWithoutRole(Long corpId);


}
