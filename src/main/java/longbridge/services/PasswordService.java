package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.validator.PasswordValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Fortune on 5/3/2017.
 */

@Service
public class PasswordService {

    @Autowired
    ConfigurationService configService;

    @Autowired
    PasswordValidator passwordValidator;

     private List<String> passwordRules = new ArrayList<String>();
     String rule = "";


    private SettingDTO numOfPasswordDigits;
    private SettingDTO minLengthOfPassword;
    private SettingDTO maxLengthOfPassword;
    private SettingDTO noSpecialChar;
    private SettingDTO specialChars;


    private int numOfDigits = 0;
    private int noOfSpecial = 0;
    private int minLength = 8;
    private int maxLength = 255;
    private String specialCharacters = "!@#$%^)(&";
    private boolean initialized = false;



    private void init() {
        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LEN");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LEN");
        specialChars = configService.getSettingByName("PASSWORD_SPECIAL_CHARS");

        if (numOfPasswordDigits != null && numOfPasswordDigits.isEnabled() ){
            numOfDigits = NumberUtils.toInt(numOfPasswordDigits.getValue());
            rule = String.format("Number of password must not be less than %d",numOfDigits);
            passwordRules.add(rule);

        }
        if (noSpecialChar != null && noSpecialChar.isEnabled()) {
            noOfSpecial = NumberUtils.toInt(noSpecialChar.getValue());
            rule = String.format("Number of special characters allowed is %d",noOfSpecial);
            passwordRules.add(rule);

        }
        if (minLengthOfPassword != null && minLengthOfPassword.isEnabled()) {
            minLength = NumberUtils.toInt(minLengthOfPassword.getValue());
            rule = String.format("Password length must not be less than %d",minLength);
            passwordRules.add(rule);
        }
        if (maxLengthOfPassword != null && maxLengthOfPassword.isEnabled()) {
            maxLength = NumberUtils.toInt(maxLengthOfPassword.getValue());
            rule = String.format("Password length must not be greater than %d",maxLength);
            passwordRules.add(rule);
        }
        if (specialChars != null && specialChars.isEnabled()) {
            specialCharacters = specialChars.getValue();
            rule = String.format("Password must include any of these special characters: %s",specialCharacters);
            passwordRules.add(rule);
        }
        initialized = true;


    }

    public String validate(String password){
        return passwordValidator.validate(password);
    }


public List<String> getPasswordRules(){
        if(!initialized){
            init();
        }
        return passwordRules;
}

}
