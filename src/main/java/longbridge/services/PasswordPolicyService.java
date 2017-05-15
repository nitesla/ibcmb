package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.utils.PasswordCreator;
import longbridge.validator.PasswordValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    String ruleMessage = "";


    private SettingDTO numOfPasswordDigits;
    private SettingDTO minLengthOfPassword;
    private SettingDTO maxLengthOfPassword;
    private SettingDTO noSpecialChar;
    private SettingDTO specialChars;
    private SettingDTO numOfChangesBeforeReuse;


    private int numOfDigits = 0;
    private int noOfSpecial = 0;
    private int minLength = 8;
    private int maxLength = 255;
    private String specialCharacters = "~!@#$%^&*()+{};'?.";
    private int numOfChanges=0;
    private boolean initialized = false;


    private void init() {

        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LEN");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LEN");
        specialChars = configService.getSettingByName("PASSWORD_SPECIAL_CHARS");
        numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");



        if (numOfPasswordDigits != null && numOfPasswordDigits.isEnabled()) {
            numOfDigits = NumberUtils.toInt(numOfPasswordDigits.getValue());

            if(numOfDigits>0) {
                ruleMessage = String.format("Minimum number of digits required in password is %d", numOfDigits);
                passwordRules.add(ruleMessage);
            }

        }
        if (noSpecialChar != null && noSpecialChar.isEnabled()) {
            noOfSpecial = NumberUtils.toInt(noSpecialChar.getValue());

            if(noOfSpecial>0) {
                ruleMessage = String.format("Minimum number of special characters required is %d", noOfSpecial);
                passwordRules.add(ruleMessage);
            }

        }
        if (minLengthOfPassword != null && minLengthOfPassword.isEnabled()) {
            minLength = NumberUtils.toInt(minLengthOfPassword.getValue());

            ruleMessage = String.format("Minimum length of password required is %d", minLength);

            passwordRules.add(ruleMessage);

        }
        if (maxLengthOfPassword != null && maxLengthOfPassword.isEnabled()) {
            maxLength = NumberUtils.toInt(maxLengthOfPassword.getValue());

            ruleMessage = String.format("Maximum length of password is %d", maxLength);
            passwordRules.add(ruleMessage);
        }
        if (specialChars != null && specialChars.isEnabled()) {
            specialCharacters = specialChars.getValue();
            ruleMessage = String.format("Password must include any of these special characters: %s", specialCharacters);
            passwordRules.add(ruleMessage);

        }
        if(numOfChangesBeforeReuse!=null&&numOfChangesBeforeReuse.isEnabled()){
            numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());
            ruleMessage = String.format("Password reuse must be after %d usages of different passwords");
            passwordRules.add(ruleMessage);

        }
            initialized = true;

    }

    public String validate(String password,String usedPasswords) {
        return passwordValidator.validate(password, usedPasswords);
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
        return passwordCreator.generatePassword(minLength,numOfDigits,noOfSpecial,specialCharacters);
    }

    public Date getPasswordExpiryDate(){
        Calendar calendar = Calendar.getInstance();
        int days = 60;//default
        SettingDTO setting = configService.getSettingByName("PASSWORD_EXPIRY");
        if(setting!=null&setting.isEnabled() ){
            days = NumberUtils.toInt(setting.getValue());
        }
        calendar.add(Calendar.DAY_OF_YEAR,days);
        return  calendar.getTime();
    }

}
