package longbridge.services;

import longbridge.dtos.CorpCorporateUserDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.forms.AlertPref;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.models.Corporate;
import longbridge.models.CorporateRole;
import longbridge.models.CorporateUser;
import longbridge.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.transaction.Transactional;
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

    CorporateUser getUserByNameAndCorpCif(String username, String cif);

    CorporateUser getUserByNameAndCorporateId(String username, String corporateId);


    CorporateUserDTO convertEntityToDTO(CorporateUser corporateUser);

    CorporateUser convertDTOToEntity(CorporateUserDTO corporateUserDTO);

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


    void sendActivationCredentials(CorporateUser user, String password);

    /**
     * resets the password for the specified corporate user
     * @param userId
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_USER')")
    String resetPassword(Long userId) throws PasswordException;


    String resetCorpPassword(Long userId) throws PasswordException;

    /**
     * Deletes the specified corporate user
     * @param userId the corporate user's id
     */
    @PreAuthorize("hasAuthority('DELETE_CORPORATE_USER')")
    String deleteUser(Long userId) throws InternetBankingException;


    /**
     * Replaces the old password with the new password for the specified corporate user.
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.
     * @param user the corporate user
     * @param changePassword the change password
     */
    @PreAuthorize("hasAuthority('UPDATE_CORPORATE_USER')")
    String changePassword(CorporateUser user, CustChangePassword changePassword) throws PasswordException;


    boolean userExists(String username);

    /** This sets the Alert preference of the specified user. Alert preference may
     * be SMS, EMAIL or BOTH
     * @param
     * @param alertPreference
     * @return
     */
    boolean changeAlertPreference(CorporateUser corporateUser, AlertPref alertPreference);

    void addCorporateUserToAuthorizerRole(CorporateUser corporateUser, Long corpRoleId);

    void changeCorporateUserAuthorizerRole(CorporateUser corporateUser, CorporateRole role, Long newRoleId);

    void removeUserFromAuthorizerRole(CorporateUser corporateUser);

    String addCorpUserFromCorporateAdmin(CorpCorporateUserDTO user) throws InternetBankingException;

    String resetPassword(CorporateUser user, CustResetPassword changePassword);

    List<CorporateUserDTO> getUsersWithoutRole(Long corpId);

    @PreAuthorize("hasAuthority('UNLOCK_CORP_USER')")
    String unlockUser(Long id) throws InternetBankingException;



    void createUserOnEntrustAndSendCredentials(CorporateUser user);
    void increaseNoOfTokenAttempt(CorporateUser user);
    void resetNoOfTokenAttempt(CorporateUser user);
    CorporateUser getUserByCifAndEmailIgnoreCase(Corporate corporate,String email);


    /**
     * USER ADMIN OPERATIONS WITH VERIFICATION
     */
    String addAuthorizer(CorporateUserDTO user)throws InternetBankingException;

    String addInitiator(CorporateUserDTO user)throws InternetBankingException;
    String updateUserFromCorpAdmin(CorporateUserDTO user) throws InternetBankingException;
    String changeActivationStatusFromCorpAdmin(Long id) throws InternetBankingException;

//    String addUserFromCorporateAdmin(CorporateUserDTO user) throws InternetBankingException;
//
//    String updateUserFromCorporateAdmin(CorporateUserDTO user) throws InternetBankingException;
}
