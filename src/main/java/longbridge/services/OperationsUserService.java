package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.OperationsUserDTO;
import longbridge.models.OperationsUser;

/**
 * The {@code OperationsUserService} provides the methods for managing operations users
 * @see OperationsUserDTO
 * Created on 3/29/2017.
 */
public interface OperationsUserService{

    /**
     *Returns the specified operations user
     * @param id the user's id
     * @return the Operations User
     */
    OperationsUserDTO getUser(Long id);


    /**
     * Returns all Operations users present in th system
     * @return list of user
     */
    Iterable<OperationsUserDTO> getUsers();
    
    Page<OperationsUserDTO> getUsers(Pageable pageDetails);


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
     * @param user the new OperationsUserDTO
     */
    boolean addUser(OperationsUserDTO user);

    /**
     * Update the details of the Operations User
     * @param user the Operations User
     */
    boolean updateUser(OperationsUserDTO user);



    /**
     * Deletes an Operations User
     * @param userId the  Operations user's id
     */
    void deleteUser(Long userId);

    /**
     * Resets the password of the specified Operations user
     * @param user the user
     */

    void resetPassword(OperationsUser user);

    /**
     * Replaces the old password with the new password
     * Also, the password must meet the organization's password policy if any one has been defined
     * It is important that the password is hashed before storing it in the database.     * @param oldPassword the oldPassword
     * @param oldPassword the old password
     * @param newPassword the new Password
     */
    boolean changePassword(OperationsUserDTO User, String oldPassword, String newPassword);
     
        
    void generateAndSendPassword();
}
