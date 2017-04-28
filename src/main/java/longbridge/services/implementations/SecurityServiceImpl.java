package longbridge.services.implementations;

import longbridge.dtos.PermissionDTO;
import longbridge.dtos.RoleDTO;
import longbridge.models.Permission;
import longbridge.models.RetailUser;
import longbridge.models.Role;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;
import longbridge.services.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private BCryptPasswordEncoder passwordEncoder;

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
}
