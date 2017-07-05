package longbridge.services;

import longbridge.exception.InternetBankingTransferException;
import longbridge.models.RetailUser;

import java.util.List;
import java.util.Map;

/**
 * The {@code SecurityService} interface provides the methods for managing roles, profiles and permissions
 *
 * @author Fortunatus Ekenachi
 *         Created on 3/28/2017.
 */
public interface SecurityService {


    /**
     * Generates a random password of 12 characters
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
    boolean validateSecurityQuestion(RetailUser retailUser, String securityQuestion, String securityAnswer);


    /**
     * This validates the token string passed in.
     *
     * @param username    The username of the user
     * @param tokenString the inputted token string
     */
    boolean performTokenValidation(String username, String tokenString) throws InternetBankingTransferException;

    boolean performOtpValidation(String username, String otp) throws InternetBankingTransferException;


    /**
     * This sends a request to synchronize the token attached to the user
     * with the specified username.
     *
     * @param username the username of the required user
     */
    boolean synchronizeToken(String username, String sNo, String tokenResp1, String tokenResp2) throws InternetBankingTransferException;

    boolean sendOtp(String username) throws InternetBankingTransferException;

    boolean createEntrustUser(String username, String fullName, boolean enableOtp) throws InternetBankingTransferException;

    void deleteEntrustUser(String username) throws InternetBankingTransferException;

    boolean assignToken(String username, String serialNumber) throws InternetBankingTransferException;

    boolean activateToken(String username, String serialNumber) throws InternetBankingTransferException;

    boolean deActivateToken(String username, String serialNumber) throws InternetBankingTransferException;

    void setUserQA(String username, String question, String answer) throws InternetBankingTransferException;
    void setUserQA(String username, List<String> questions, List<String> answers) throws InternetBankingTransferException;

    Map<String, List<String>> getUserQA(String username) throws InternetBankingTransferException;
     Integer getMinUserQA(String username);
     Integer getMinUserQA();
     Integer geUserQASize(String username);

    Map<String, List<String>> getMutualAuth(String username) throws InternetBankingTransferException;


    void setMutualAuth(String username, List<String> mutualCaption, List<String> mutualImage);

    void setMutualAuth(String username, String mutualCaption, String mutualImagePath);

    void setMutualAuth(String username, List<String> mutualCaption, List<String> mutualImage, String token);

    String getTokenSerials(String username);

    boolean unLockUser(String username);

    boolean updateUser(String username, String fullName, boolean enableOtp);

    boolean addUserContacts(String email, String phone, boolean phoneDefault, String userName);


}
