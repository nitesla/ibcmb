package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.models.*;

import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;

import longbridge.repositories.VerificationRepo;
import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
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

}
