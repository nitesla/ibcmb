package longbridge.services.implementations;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import longbridge.exception.InternetBankingTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import longbridge.models.RetailUser;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */

@Service
public class SecurityServiceImpl implements SecurityService {

    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private IntegrationService integrationService;

    @Autowired
    public SecurityServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
	public boolean performTokenValidation(String username, String tokenString) throws InternetBankingTokenException {
		return integrationService.performTokenValidation(username, tokenString);
	}

	@Override
	public void synchronizeToken(String username) throws InternetBankingTokenException {
		integrationService.synchronizeToken(username);
	}
	

	
}
