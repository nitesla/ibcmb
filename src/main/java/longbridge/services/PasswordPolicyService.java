package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.utils.PasswordCreator;
import longbridge.validator.PasswordValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 5/3/2017.
 */

@Service
public class PasswordPolicyService {

    @Autowired
    ConfigurationService configService;

    @Autowired
    PasswordValidator passwordValidator;

    @Autowired
    PasswordCreator passwordCreator;

    private List<String> passwordRules = new ArrayList<String>();
    private List<CharacterRule> characterRules = new ArrayList<CharacterRule>();
    String ruleMessage = "";


    private SettingDTO numOfPasswordDigits;
    private SettingDTO minLengthOfPassword;
    private SettingDTO maxLengthOfPassword;
    private SettingDTO noSpecialChar;
    private SettingDTO specialChars;


    private int numOfDigits = 0;
    private int noOfSpecial = 0;
    private int minLength = 8;
    private int maxLength = 255;
    private String specialCharacters = "~!@#$%^&*()+{};'?.";
    private boolean initialized = false;


    private void init() {
        characterRules = new ArrayList<CharacterRule>();

        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LEN");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LEN");
        specialChars = configService.getSettingByName("PASSWORD_SPECIAL_CHARS");

        characterRules.add(new CharacterRule(EnglishCharacterData.UpperCase,1 ));
        characterRules.add(new CharacterRule(EnglishCharacterData.LowerCase,1 ));


        if (numOfPasswordDigits != null && numOfPasswordDigits.isEnabled()) {
            numOfDigits = NumberUtils.toInt(numOfPasswordDigits.getValue());

            ruleMessage = String.format("Number of password must not be less than %d", numOfDigits);

            passwordRules.add(ruleMessage);
            characterRules.add(new CharacterRule(EnglishCharacterData.Digit, numOfDigits));

        }
        if (noSpecialChar != null && noSpecialChar.isEnabled()) {
            noOfSpecial = NumberUtils.toInt(noSpecialChar.getValue());

            ruleMessage = String.format("Number of special characters allowed is %d", noOfSpecial);
            passwordRules.add(ruleMessage);
            characterRules.add(new CharacterRule(EnglishCharacterData.Special, noOfSpecial));



        }
        if (minLengthOfPassword != null && minLengthOfPassword.isEnabled()) {
            minLength = NumberUtils.toInt(minLengthOfPassword.getValue());

            ruleMessage = String.format("Password length must not be less than %d", minLength);

            passwordRules.add(ruleMessage);

        }
        if (maxLengthOfPassword != null && maxLengthOfPassword.isEnabled()) {
            maxLength = NumberUtils.toInt(maxLengthOfPassword.getValue());

            ruleMessage = String.format("Password length must not be greater than %d", maxLength);

            passwordRules.add(ruleMessage);
        }
        if (specialChars != null && specialChars.isEnabled()) {
            specialCharacters = specialChars.getValue();
            ruleMessage = String.format("Password must include any of these special characters: %s", specialCharacters);
            passwordRules.add(ruleMessage);
//            characterRules.add(new CharacterRule(new CharacterData() {
//                @Override
//                public String getErrorCode() {
//                    return "ERR_SPACE";
//                }
//
//                @Override
//                public String getCharacters() {
//                    return specialCharacters;
//                }
//            }, 1));

        }

        initialized = true;


    }

    public String validate(String password) {
        return passwordValidator.validate(password);
    }


    public List<String> getPasswordRules() {
        if (!initialized) {
            init();
        }
        return passwordRules;
    }

    public String generatePassword(){
        if (!initialized) {
            init();
        }
        return passwordCreator.generatePassword(minLength,specialCharacters);
    }

}
