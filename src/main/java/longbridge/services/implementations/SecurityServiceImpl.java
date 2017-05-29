package longbridge.services.implementations;

import com.expertedge.entrustplugin.ws.*;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.RetailUser;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import longbridge.utils.ResultType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;


/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */

@Service
public class SecurityServiceImpl implements SecurityService {


    @Value("${ENTRUST.app.code}")
    private String appCode;
    @Value("${ENTRUST.app.group}")
    private String appGroup;
    @Value("${ENTRUST.app.desc}")
    private String appDesc;
    private EntrustMultiFactorAuthImpl port;

    private BCryptPasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    private IntegrationService integrationService;
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    public SecurityServiceImpl(BCryptPasswordEncoder passwordEncoder, IntegrationService integrationService, EntrustMultiFactorAuthImpl port, ModelMapper modelMapper) {
        this.passwordEncoder = passwordEncoder;
        this.integrationService = integrationService;
        this.port = port;
        this.modelMapper = modelMapper;
    }

    @Override
    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(0, 12);
    }

    @Override
    public List<String> getSecurityQuestions() {

        return null;
    }

    @Override
    public boolean validateSecurityQuestion(RetailUser retailUser, String securityQuestion, String securityAnswer) {
        //TODO implement this through entrust
        return true;
    }

    @Override
    public String performTokenValidation(String username, String tokenString) {
        String result = "";
        TokenAuthDTO tauth = new TokenAuthDTO();
        tauth.setAppCode(appCode);
        tauth.setAppDesc(appDesc);
        tauth.setGroup(appGroup);
        tauth.setTokenPin(tokenString);
        tauth.setUserName(username);
        logger.trace("Token validation parameters {}", tauth);
        logger.trace("******************BEGIN RESPONSE***********");
        AuthResponseDTO response = port.performTokenAuth(tauth);
        if (response != null) {
            logger.trace("Authentication status: " + response.isAuthenticationSuccessful());

            logger.trace("Authentication response code: " + response.getRespCode());
            if (response.isAuthenticationSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();

    }

    @Override
    public String performOtpValidation(String username, String otp) {
        String result = "";
        OtpAuthDTO tauth = new OtpAuthDTO();
        tauth.setAppCode(appCode);
        tauth.setAppDesc(appDesc);
        tauth.setGroup(appGroup);
        tauth.setOtpResponse(otp);
        tauth.setUserName(username);
        logger.trace("OTP validation parameters {}", tauth);
        logger.trace("******************BEGIN RESPONSE***********");
        AuthResponseDTO response = port.performOTPAuthentication(tauth);
        if (response != null) {
            logger.trace("Authentication status: " + response.isAuthenticationSuccessful());
            logger.trace("Authentication response code: " + response.getRespCode());
            logger.trace("Authentication Message: " + response.getRespMessage());
            if (response.isAuthenticationSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public void synchronizeToken(String username) {




    }

    @Override
    public String sendOtp(String username) {
        String result = "";
        OtpCreateSendDTO sendDTO = new OtpCreateSendDTO();
        sendDTO.setAppCode(appCode);
        sendDTO.setAppDesc(appDesc);
        sendDTO.setGroup(appGroup);
        sendDTO.setUserName(username);
        logger.trace("Perform OTP parameters {}", sendDTO);
        logger.trace("******************BEGIN RESPONSE***********");
        AuthResponseDTO response = port.performCreateSendOTP(sendDTO);
        if (response != null) {
            logger.trace(" OTP Authentication status: " + response.isAuthenticationSuccessful());
            logger.trace("Authentication response code: " + response.getRespCode());
            if (response.isAuthenticationSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public String createEntrustUser(String username, String fullName, boolean enableOtp) {
        String result = "";
        UserAdminDTO user = new UserAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setFullname(fullName);
        user.setEnableOTP(enableOtp);
        user.setUserName(username);
        logger.trace("User creation parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performCreateEntrustUser(user);
        if (response != null) {
            logger.trace("Creation status: " + response.isAdminSuccessful());
            logger.trace(" Creation response code: " + response.getRespCode());
            logger.trace(" Creation response message: " + response.getRespMessage());
            if (response.isAdminSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public String deleteEntrustUser(String username, String fullName, boolean enableOtp) {
        String result = "";
        UserDelAdminDTO user = new UserDelAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setUserName(username);
        logger.trace("User Delete parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performDeleteEntrustUser(user);
        if (response != null) {
            logger.trace("Delete status: " + response.isAdminSuccessful());
            logger.trace(" Delete response code: " + response.getRespCode());
            if (response.isAdminSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public String assignToken(String username, String serialNumber) {
        String result = "";
        TokenAdminDTO user = new TokenAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setSerialNumber(serialNumber);
        user.setUserName(username);
        logger.trace("Token assign parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performAssignToken(user);
        if (response != null) {
            logger.trace("Creation status: " + response.isAdminSuccessful());
            logger.trace(" Creation response code: " + response.getRespCode());
            if (response.isAdminSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public String activateToken(String username, String serialNumber) {
        String result = "";
        TokenAdminDTO user = new TokenAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setSerialNumber(serialNumber);
        user.setUserName(username);
        logger.trace("Token activation parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performActivateToken(user);
        if (response != null) {
            logger.trace("Activation status: " + response.isAdminSuccessful());
            logger.trace(" Activation response code: " + response.getRespCode());
            if (response.isAdminSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public String deActivateToken(String username, String serialNumber) {
        String result = "";
        TokenAdminDTO user = new TokenAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setSerialNumber(serialNumber);
        user.setUserName(username);
        logger.trace("Token de-activation parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performDeactivateToken(user);
        if (response != null) {
            logger.trace("deactivate status: " + response.isAdminSuccessful());
            logger.trace(" deactivate response code: " + response.getRespCode());
            if (response.isAdminSuccessful()) {
                result = ResultType.SUCCESS.toString();
                return result;
            }
            throw new InternetBankingSecurityException(response.getRespMessage());
        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public void setUserQA(String username, List<String> questions, List<String> answer) {
        QaSetDTO user = new QaSetDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setAnswers(answer);
        user.setQuestions(questions);
        user.setUserName(username);

        logger.trace("Token de-activation parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performSetQA(user);

        if (response != null) {
            logger.trace("Creation status: " + response.isAdminSuccessful());
            logger.trace(" Creation response code: " + response.getRespCode());
            if (!response.isAdminSuccessful()) {
                throw new InternetBankingSecurityException(response.getRespMessage());
            }

        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public void getUserQA(String username) {

    }

}
