package longbridge.services;

import longbridge.api.CustomerDetails;
import longbridge.dtos.UserRegDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

public interface UserRegService {

    String validateDetails (UserRegDTO userRegDTO);

    String validateIfAcctExist(UserRegDTO userRegDTO);

    List<String> getSecQuestionFromNumber(@PathVariable String cifId);

    String getSecAns(UserRegDTO userRegDTO);

    String sendRegCode(UserRegDTO userRegDTO);

    String checkRegCode(UserRegDTO userRegDTO);

    String usernameCheck(UserRegDTO userRegDTO);

    String passwordCheck(UserRegDTO userRegDTO);

    ArrayList[] getSecurityQuestions(int noOfSecurityQs);

    ArrayList[] getSecurityQuestionsMobile(int noOfQuestions);

    CustomerDetails customerDetails(String accountNumber, String email, String birthDate);
}
