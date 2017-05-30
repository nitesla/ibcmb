package longbridge.services.implementations;

import com.expertedge.entrustplugin.ws.*;

import longbridge.exception.EntrustConnectionException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public boolean performTokenValidation(String username, String tokenString) {
        boolean result = false;
        TokenAuthDTO tauth = new TokenAuthDTO();
        tauth.setAppCode(appCode);
        tauth.setAppDesc(appDesc);
        tauth.setGroup(appGroup);
        tauth.setTokenPin(tokenString);
        tauth.setUserName(username);
        logger.trace("Token validation parameters {}", tauth);
        logger.trace("******************BEGIN RESPONSE***********");

        try {
            AuthResponseDTO response = port.performTokenAuth(tauth);
            if (response != null) {
                logger.trace("Authentication status: " + response.isAuthenticationSuccessful());

                logger.trace("Authentication response code: " + response.getRespCode());

            result = response.isAuthenticationSuccessful();
            return result;

            }
            logger.trace("******************END RESPONSE***********");
        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;
    }

    @Override
    public boolean performOtpValidation(String username, String otp) {
        boolean result = false;
        OtpAuthDTO tauth = new OtpAuthDTO();
        tauth.setAppCode(appCode);
        tauth.setAppDesc(appDesc);
        tauth.setGroup(appGroup);
        tauth.setOtpResponse(otp);
        tauth.setUserName(username);
        logger.trace("OTP validation parameters {}", tauth);
        logger.trace("******************BEGIN RESPONSE***********");
        try{
        AuthResponseDTO response = port.performOTPAuthentication(tauth);
        if (response != null) {
            logger.trace("Authentication status: " + response.isAuthenticationSuccessful());
            logger.trace("Authentication response code: " + response.getRespCode());
            logger.trace("Authentication Message: " + response.getRespMessage());

            result = response.isAuthenticationSuccessful();


            return result;
        }
        logger.trace("******************END RESPONSE***********");


    }
        catch (EntrustConnectionException e){
        logger.error(e.getMessage(), e);
        throw new InternetBankingSecurityException(e.getMessage(),e);

    }
        return result;    }

    @Override
    public boolean synchronizeToken(String username, String sNo, String tokenResp1, String tokenResp2) {

        boolean result = false;
        TokenSynDTO sendDTO = new TokenSynDTO();
        sendDTO.setAppCode(appCode);
        sendDTO.setAppDesc(appDesc);
        sendDTO.setGroup(appGroup);
        sendDTO.setUserName(username);
        sendDTO.setSerialNumber(sNo);
        sendDTO.setTokenResponse1(tokenResp1);
        sendDTO.setTokenResponse2(tokenResp2);
        logger.trace("******************BEGIN RESPONSE***********");
        try{
        AdminResponseDTO response = port.performTokenSync(sendDTO);
        if (response != null) {
            logger.trace(" Synchronize status: " + response.isAdminSuccessful());
            logger.trace("Synchronize response code: " + response.getRespCode());
            logger.trace("Synchronize response message: " + response.getRespMessage());

            result = response.isAdminSuccessful();
            return result;
        }

        logger.trace("******************END RESPONSE***********");


        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;

    }

    @Override
    public boolean sendOtp(String username) {
        boolean result = false;
        OtpCreateSendDTO sendDTO = new OtpCreateSendDTO();
        sendDTO.setAppCode(appCode);
        sendDTO.setAppDesc(appDesc);
        sendDTO.setGroup(appGroup);
        sendDTO.setUserName(username);
        logger.trace("Perform OTP parameters {}", sendDTO);
        logger.trace("******************BEGIN RESPONSE***********");
        try{
        AuthResponseDTO response = port.performCreateSendOTP(sendDTO);
        if (response != null) {
            logger.trace(" OTP Authentication status: " + response.isAuthenticationSuccessful());
            logger.trace("Authentication response code: " + response.getRespCode());

            result = response.isAuthenticationSuccessful();
            return result;
        }

        logger.trace("******************END RESPONSE***********");


        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;    }

    @Override
    public boolean createEntrustUser(String username, String fullName, boolean enableOtp) {
        boolean result = false;
        UserAdminDTO user = new UserAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setFullname(fullName);
        user.setEnableOTP(enableOtp);
        user.setUserName(username);
        logger.info("User creation parameters {}", user);
        logger.info("******************BEGIN RESPONSE***********");
        try{
        AdminResponseDTO response = port.performCreateEntrustUser(user);
        if (response != null) {
            logger.info("Creation status: " + response.isAdminSuccessful());
            logger.info(" Creation response code: " + response.getRespCode());
            logger.info(" Creation response message: " + response.getRespMessage());

            result = response.isAdminSuccessful();
            return result;

        }
        logger.info("******************END RESPONSE***********");

        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;    }

    @Override
    public void deleteEntrustUser(String username, String fullName) {
        UserDelAdminDTO user = new UserDelAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setUserName(username);
        logger.trace("User Delete parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        try{
        AdminResponseDTO response = port.performDeleteEntrustUser(user);
        if (response != null) {
            logger.trace("Delete status: " + response.isAdminSuccessful());
            logger.trace(" Delete response code: " + response.getRespCode());
            logger.trace(" Delete response message: " + response.getRespMessage());
            if (!response.isAdminSuccessful()) {

                throw new InternetBankingSecurityException("Unable to delete user");

            }

        } else {
            throw new InternetBankingSecurityException();
        }
        logger.trace("******************END RESPONSE***********");

        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }

    }

    @Override
    public boolean assignToken(String username, String serialNumber) {
        boolean result = false;
        TokenAdminDTO user = new TokenAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setSerialNumber(serialNumber);
        user.setUserName(username);
        logger.trace("Token assign parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        try{
        AdminResponseDTO response = port.performAssignToken(user);
        if (response != null) {
            logger.trace("Creation status: " + response.isAdminSuccessful());
            logger.trace(" Creation response code: " + response.getRespCode());
            logger.trace(" Creation response message: " + response.getRespCode());

            result = response.isAdminSuccessful();
            return result;
        }


        logger.trace("******************END RESPONSE***********");


        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;    }

    @Override
    public boolean activateToken(String username, String serialNumber) {
        boolean result = false;
        TokenAdminDTO user = new TokenAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setSerialNumber(serialNumber);
        user.setUserName(username);
        logger.trace("Token activation parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performActivateToken(user);
        try{
        if (response != null) {
            logger.trace("Activation status: " + response.isAdminSuccessful());
            logger.trace(" Activation response code: " + response.getRespCode());

            result = response.isAdminSuccessful();
            return result;
        }


        logger.trace("******************END RESPONSE***********");


        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;    }

    @Override
    public boolean deActivateToken(String username, String serialNumber) {
        boolean result = false;
        TokenAdminDTO user = new TokenAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setSerialNumber(serialNumber);
        user.setUserName(username);
        logger.trace("Token de-activation parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        try{
        AdminResponseDTO response = port.performDeactivateToken(user);
        if (response != null) {
            logger.trace("deactivate status: " + response.isAdminSuccessful());
            logger.trace(" deactivate response code: " + response.getRespCode());

            result = response.isAdminSuccessful();
            return result;
        }


        logger.trace("******************END RESPONSE***********");


        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        return result;    }

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

        try {
        AdminResponseDTO response = port.performSetQA(user);

        if (response != null) {
            logger.trace("Creation status: " + response.isAdminSuccessful());
            logger.trace(" Creation response code: " + response.getRespCode());
            logger.trace(" Creation response Message: " + response.getRespMessage());
            if (!response.isAdminSuccessful()) {
                throw new InternetBankingSecurityException(response.getRespMessage());
            }

        }
        logger.trace("******************END RESPONSE***********");


        }
        catch (EntrustConnectionException e){
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(),e);

        }
        }

    @Override
    public Map<List<String>, List<String>> getUserQA(String username) {
        Map<List<String>, List<String>> list = new HashMap<>();

        QaGetDTO user = new QaGetDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setUserName(username);

        logger.trace("QA parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        QaGetResponseDTO response = port.performGetQA(user);

        if (response != null) {
            logger.trace("QA status: " + response.isAdminSuccessful());
            logger.trace(" QA response code: " + response.getRespCode());
            logger.trace(" QA response Message: " + response.getRespMessage());
            if (!response.isAdminSuccessful()) {
                throw new InternetBankingSecurityException(response.getRespMessage());
            }

        }
        logger.trace("******************END RESPONSE***********");

        List<String> questions = response.getQuestions();
        List<String> answers = response.getAnswers();

        list.put(questions, answers);


        return list;


    }

    @Override
    public Map<List<String>, List<String>> getMutualAuth(String username) throws InternetBankingTransferException {
        Map<List<String>, List<String>> list = new HashMap<>();

        MauthDTO user = new MauthDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setUserName(username);

        logger.trace("QA parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        ImageCaptionResponseDTO response = port.performMutualAuth(user);

        if (response != null) {
            logger.trace("MUTUAL AUTH status: " + response.isSuccessful());
            logger.trace("MUTUAL AUTH response code: " + response.getRespCode());
            logger.trace(" MUTUAL AUTH response Message: " + response.getRespMessage());
            if (!response.isSuccessful()) {
                throw new InternetBankingSecurityException(response.getRespMessage());
            }

        }
        logger.trace("******************END RESPONSE***********");

        List<String> captionSecret = response.getCaptionSecret();
        List<String> imageSecret = response.getImageSecret();

        list.put(captionSecret, imageSecret);


        return list;

    }

    @Override
    public String getTokenSerials(String username) {
        String result = "";
        TokenSerialDTO user = new TokenSerialDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);

        user.setUserName(username);
        logger.trace("User  parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        TokenSerialResponseDTO response = port.performGetTokenSerial(user);
        if (response != null) {
            logger.trace("status: " + response.isAuthenticationSuccessful());
            logger.trace("  response code: " + response.getRespCode());
            logger.trace("  response message: " + response.getRespMessage());

            result = response.getTokenSerials();
            return result;

        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public boolean unLockUser(String username) {
        boolean result = false;
        UserUnLockDTO user = new UserUnLockDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setGroup(appGroup);
        user.setUserName(username);
        logger.trace("User Unlock parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performUnLockUser(user);
        if (response != null) {
            logger.trace("Unlock status: " + response.isAdminSuccessful());
            logger.trace(" Unlock response code: " + response.getRespCode());
            logger.trace(" Unlock response message: " + response.getRespMessage());

            result = response.isAdminSuccessful();
            return result;

        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }

    @Override
    public boolean updateUser(String username, String fullName, boolean enableOtp) {
        boolean result = false;
        UserAdminDTO user = new UserAdminDTO();
        user.setAppCode(appCode);
        user.setAppDesc(appDesc);
        user.setFullname(fullName);
        user.setGroup(appGroup);

        user.setEnableOTP(enableOtp);
        user.setUserName(username);
        logger.trace("User Update parameters {}", user);
        logger.trace("******************BEGIN RESPONSE***********");
        AdminResponseDTO response = port.performCreateEntrustUser(user);
        if (response != null) {
            logger.trace("Update status: " + response.isAdminSuccessful());
            logger.trace(" Update response code: " + response.getRespCode());
            logger.trace(" Update response message: " + response.getRespMessage());

            result = response.isAdminSuccessful();
            return result;

        }
        logger.trace("******************END RESPONSE***********");


        throw new InternetBankingSecurityException();
    }
}


