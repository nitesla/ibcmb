package longbridge.services;

import longbridge.exception.InternetBankingSecurityException;
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
    boolean performTokenValidation(String username,String group, String tokenString) throws InternetBankingTransferException;

    boolean performOtpValidation(String username ,String group, String otp) throws InternetBankingTransferException;


    /**
     * This sends a request to synchronize the token attached to the user
     * with the specified username.
     *
     * @param username the username of the required user
     */
    boolean synchronizeToken(String username,String group, String sNo, String tokenResp1, String tokenResp2) throws InternetBankingSecurityException;

    boolean sendOtp(String username,String group) throws InternetBankingSecurityException;

    boolean createEntrustUser(String username,String group, String fullName, boolean enableOtp) throws InternetBankingSecurityException;

    void deleteEntrustUser(String username,String group) throws InternetBankingSecurityException;

    boolean assignToken(String username,String group, String serialNumber) throws InternetBankingSecurityException;

    boolean activateToken(String username,String group, String serialNumber) throws InternetBankingSecurityException;

    boolean deActivateToken(String username,String group, String serialNumber) throws InternetBankingSecurityException;

    void setUserQA(String username,String group, String question, String answer) throws InternetBankingSecurityException;
    void setUserQA(String username,String group, List<String> questions, List<String> answers) throws InternetBankingSecurityException;

    Map<String, List<String>> getUserQA(String username,String group) throws InternetBankingSecurityException;
     Integer getMinUserQA(String username,String group);
     Integer getMinUserQA();
     Integer geUserQASize(String username,String group);

    Map<String, List<String>> getMutualAuth(String username,String group) throws InternetBankingSecurityException;


    void setMutualAuth(String username,String group, List<String> mutualCaption, List<String> mutualImage);

    void setMutualAuth(String username,String group, String mutualCaption, String mutualImagePath);

    void setMutualAuth(String username,String group, List<String> mutualCaption, List<String> mutualImage, String token);

    String getTokenSerials(String username,String group);

    boolean unLockUser(String username,String group);

    boolean updateUser(String username,String group, String fullName, boolean enableOtp);

    boolean addUserContacts(String email, String phone, boolean phoneDefault, String userName,String group);


}
