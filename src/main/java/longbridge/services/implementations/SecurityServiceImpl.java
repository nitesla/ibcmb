package longbridge.services.implementations;

import com.expertedge.entrustplugin.ws.*;

import longbridge.models.RetailUser;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import javax.xml.namespace.QName;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */

@Service
public class SecurityServiceImpl implements SecurityService {

	@Value("${ENTRUST.URL}")
	private String entrustUrl;
	@Value("${ENTRUST.app.code}")
	private String appCode;
	@Value("${ENTRUST.app.group}")
	private String appGroup;
	@Value("${ENTRUST.app.desc}")
	private String appDesc;

    private BCryptPasswordEncoder passwordEncoder;

    private IntegrationService integrationService;
	QName qname = new QName("http://ws.entrustplugin.expertedge.com/", "EntrustMultiFactorAuthImplService");
    private Logger logger = LoggerFactory.getLogger(getClass());
	EntrustMultiFactorAuthImpl port = getService().getPort(EntrustMultiFactorAuthImpl.class);


    @Autowired
    public SecurityServiceImpl(BCryptPasswordEncoder passwordEncoder, IntegrationService integrationService) {
        this.passwordEncoder = passwordEncoder;
        this.integrationService=integrationService;
    }

    @Override
    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(0, 12);
    }

	@Override
	public List<String> getSecurityQuestions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateSecurityQuestion(RetailUser retailUser, String securityQuestion, String securityAnswer) {
		//TODO implement this through entrust
		return true;
	}

	@Override
	public boolean performTokenValidation(String username, String tokenString) {
		boolean ok =false;
		TokenAuthDTO tauth = new TokenAuthDTO();
		tauth.setAppCode(appCode);
		tauth.setAppDesc(appDesc);
		tauth.setGroup(appGroup);
		tauth.setTokenPin(tokenString);
		tauth.setUserName(username);
		logger.trace("Token validation parameters {}",tauth);
		logger.trace("******************BEGIN RESPONSE***********");
		AuthResponseDTO response = port.performTokenAuth(tauth);
		if(response != null) {
			logger.trace("Authentication status: " + response.isAuthenticationSuccessful());
			logger.trace("Authentication response code: " + response.getRespCode());
			ok = response.isAuthenticationSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;


	}

	@Override
	public boolean performOtpValidation(String username, String otp) {
		boolean ok =false;
		OtpAuthDTO tauth = new OtpAuthDTO();
		tauth.setAppCode(appCode);
		tauth.setAppDesc(appDesc);
		tauth.setGroup(appGroup);
		tauth.setOtpResponse(otp);
		tauth.setUserName(username);
		logger.trace("OTP validation parameters {}",tauth);
		logger.trace("******************BEGIN RESPONSE***********");
		AuthResponseDTO response = port.performOTPAuthentication(tauth);
		if(response != null) {
			logger.trace("Authentication status: " + response.isAuthenticationSuccessful());
			logger.trace("Authentication response code: " + response.getRespCode());
			ok = response.isAuthenticationSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;

	}

	@Override
	public void synchronizeToken(String username) {

	}

	@Override
	public boolean sendOtp(String username) {
    	boolean ok =false;
		OtpCreateSendDTO sendDTO= new OtpCreateSendDTO();
		sendDTO.setAppCode(appCode);
		sendDTO.setAppDesc(appDesc);
		sendDTO.setGroup(appGroup);
		sendDTO.setUserName(username);
		logger.trace("Perform OTP parameters {}",sendDTO);
		logger.trace("******************BEGIN RESPONSE***********");
		AuthResponseDTO  response=port.performCreateSendOTP(sendDTO);
		if(response != null) {
			logger.trace("Authentication status: " + response.isAuthenticationSuccessful());
			logger.trace("Authentication response code: " + response.getRespCode());
			ok = response.isAuthenticationSuccessful();
		}
		logger.trace("******************END RESPONSE***********");
      return ok;
	}

	@Override
	public boolean createEntrustUser(String username ,String fullName,boolean enableOtp) {
		boolean ok =false;
		UserAdminDTO user = new UserAdminDTO();
		user.setAppCode(appCode);
		user.setAppDesc(appDesc);
		user.setGroup(appGroup);
		user.setFullname(fullName);
		user.setEnableOTP(enableOtp);
		user.setUserName(username);
		logger.trace("User creation parameters {}",user);
		logger.trace("******************BEGIN RESPONSE***********");
		AdminResponseDTO response = port.performCreateEntrustUser(user);
		if(response != null) {
			logger.trace("Creation status: " + response.isAdminSuccessful());
			logger.trace(" Creation response code: " + response.getRespCode());
			ok = response.isAdminSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;

	}

	@Override
	public boolean deleteEntrustUser(String username, String fullName, boolean enableOtp) {
		boolean ok =false;
		UserDelAdminDTO user = new UserDelAdminDTO();
		user.setAppCode(appCode);
		user.setAppDesc(appDesc);
		user.setGroup(appGroup);
		user.setUserName(username);
		logger.trace("User Delete parameters {}",user);
		logger.trace("******************BEGIN RESPONSE***********");
		AdminResponseDTO response = port.performDeleteEntrustUser(user);
		if(response != null) {
			logger.trace("Creation status: " + response.isAdminSuccessful());
			logger.trace(" Creation response code: " + response.getRespCode());
			ok = response.isAdminSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;
	}

	@Override
	public boolean assignToken(String username,String serialNumber) {
		boolean ok =false;
		TokenAdminDTO user = new TokenAdminDTO();
		user.setAppCode(appCode);
		user.setAppDesc(appDesc);
		user.setGroup(appGroup);
		user.setSerialNumber(serialNumber);
		user.setUserName(username);
		logger.trace("Token assign parameters {}",user);
		logger.trace("******************BEGIN RESPONSE***********");
		AdminResponseDTO response = port.performAssignToken(user);
		if(response != null) {
			logger.trace("Creation status: " + response.isAdminSuccessful());
			logger.trace(" Creation response code: " + response.getRespCode());
			ok = response.isAdminSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;
	}

	@Override
	public boolean activateToken(String username, String serialNumber) {
		boolean ok =false;
		TokenAdminDTO user = new TokenAdminDTO();
		user.setAppCode(appCode);
		user.setAppDesc(appDesc);
		user.setGroup(appGroup);
		user.setSerialNumber(serialNumber);
		user.setUserName(username);
		logger.trace("Token activation parameters {}",user);
		logger.trace("******************BEGIN RESPONSE***********");
		AdminResponseDTO response = port.performActivateToken(user);
		if(response != null) {
			logger.trace("Creation status: " + response.isAdminSuccessful());
			logger.trace(" Creation response code: " + response.getRespCode());
			ok = response.isAdminSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;
	}

	@Override
	public boolean deActivateToken(String username, String serialNumber) {
		boolean ok =false;
		TokenAdminDTO user = new TokenAdminDTO();
		user.setAppCode(appCode);
		user.setAppDesc(appDesc);
		user.setGroup(appGroup);
		user.setSerialNumber(serialNumber);
		user.setUserName(username);
		logger.trace("Token de-activation parameters {}",user);
		logger.trace("******************BEGIN RESPONSE***********");
		AdminResponseDTO response = port.performDeactivateToken(user);
		if(response != null) {
			logger.trace("Creation status: " + response.isAdminSuccessful());
			logger.trace(" Creation response code: " + response.getRespCode());
			ok = response.isAdminSuccessful();
		}
		logger.trace("******************END RESPONSE***********");



		return ok;
	}


	private javax.xml.ws.Service getService(){
		URL url = null;
		javax.xml.ws.Service service=null;
		try {
			url= new URL(entrustUrl);
			service = 	javax.xml.ws.Service.create(url, qname);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return service;
	}
}
