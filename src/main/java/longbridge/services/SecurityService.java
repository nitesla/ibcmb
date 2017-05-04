package longbridge.services;

import java.util.List;

import longbridge.models.RetailUser;

/**
 * The {@code SecurityService} interface provides the methods for managing roles, profiles and permissions
 * @author Fortunatus Ekenachi
 * Created on 3/28/2017.
 */
public interface SecurityService {


    /** Generates a random password of 12 characters
     *
     * @return a random password string
     */
    String generatePassword();

   /** 
    * Fetches the list of security questions available
    * to users
    */
    List<String> getSecurityQuestions();
    
    
    /**
     * This validates the answer provided for a 
     * security question for the specified user
     */
    boolean validateSecurityQuestion(RetailUser retailUser,String securityQuestion,String securityAnswer);
    
    
    /** This validates the token string passed in.
     * 
     * @param username The username of the user
     * @param tokenString the inputted token string
     */
    boolean performTokenValidation(String username, String tokenString);
    
    
    /** This sends a request to synchronize the token attached to the user
     * with the specified username.
     * @param username the username of the required user
     */
    void synchronizeToken(String username);
}
