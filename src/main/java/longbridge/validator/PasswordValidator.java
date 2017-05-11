package longbridge.validator;

import longbridge.dtos.SettingDTO;
import longbridge.services.ConfigurationService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fortune on 4/13/2017.
 */
@Component
@Scope("singleton")
public class PasswordValidator {

    private Pattern digitPattern = Pattern.compile("[0-9]");
    private Pattern letterPattern = Pattern.compile("[a-zA-Z]");
    private Pattern specialCharsPattern;


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
    private StringBuilder errorMessage;
    private String message ="";



    @Autowired
    ConfigurationService configService;

    private void init() {
        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LEN");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LEN");
        specialChars = configService.getSettingByName("PASSWORD_SPECIAL_CHARS");

        if (numOfPasswordDigits != null && numOfPasswordDigits.isEnabled() ){
            numOfDigits = NumberUtils.toInt(numOfPasswordDigits.getValue());
        }
        if (noSpecialChar != null && noSpecialChar.isEnabled()) {
            noOfSpecial = NumberUtils.toInt(noSpecialChar.getValue());
        }

        if (minLengthOfPassword != null && minLengthOfPassword.isEnabled()) {
            minLength = NumberUtils.toInt(minLengthOfPassword.getValue());
        }
        if (maxLengthOfPassword != null && maxLengthOfPassword.isEnabled()) {
            maxLength = NumberUtils.toInt(maxLengthOfPassword.getValue());
        }
        if (specialChars != null && specialChars.isEnabled()) {
            specialCharacters = specialChars.getValue();
        }

        specialCharsPattern = Pattern.compile("[" + specialCharacters + "]");
        initialized = true;
    }

    public String validate(String password) {
        if (!initialized) {
            init();
        }

       errorMessage = new StringBuilder();

        Matcher digitMatcher = digitPattern.matcher(password);
        boolean digitOK = false;
        int digitCnt = 0;

        for (int i = 0; i < numOfDigits; i++) {
            digitCnt += digitMatcher.find() ? 1 : 0;
        }
        if (digitCnt >= numOfDigits)
            digitOK = true;

        Matcher specMatcher = specialCharsPattern.matcher(password);
        boolean specOK = false;
        int specCnt = 0;

        for (int i = 0; i < noOfSpecial; i++) {
            specCnt += specMatcher.find() ? 1 : 0;
        }

        if (specCnt >= noOfSpecial)
            specOK = true;

        boolean noOK = password.length() >= minLength && password.length() <= maxLength;

        if (!digitOK) {
            message = String.format("Your password must include at least %d digits",
                    numOfDigits);
            errorMessage.append(message);
            errorMessage.append(".\n");
        }
        if (!specOK) {
             message = String.format(
                    "Your password must include at least %d special characters from %s", noOfSpecial,
                    specialCharacters);
            errorMessage.append(message);
            errorMessage.append(".\n");

        }
        if (!noOK) {
            message = String.format(
                    "Your password must be between %d and %d characters", minLength, maxLength);
            errorMessage.append(message);
            errorMessage.append(".\n");
        }
        return errorMessage.toString();
    }

    public SettingDTO getNumOfPasswordDigits() {
        return numOfPasswordDigits;
    }

    public void setNumOfPasswordDigits(SettingDTO numOfPasswordDigits) {
        this.numOfPasswordDigits = numOfPasswordDigits;
    }

    public SettingDTO getMinLengthOfPassword() {
        return minLengthOfPassword;
    }

    public void setMinLengthOfPassword(SettingDTO minLengthOfPassword) {
        this.minLengthOfPassword = minLengthOfPassword;
    }

    public SettingDTO getMaxLengthOfPassword() {
        return maxLengthOfPassword;
    }

    public void setMaxLengthOfPassword(SettingDTO maxLengthOfPassword) {
        this.maxLengthOfPassword = maxLengthOfPassword;
    }

    public SettingDTO getNoSpecialChar() {
        return noSpecialChar;
    }

    public void setNoSpecialChar(SettingDTO noSpecialChar) {
        this.noSpecialChar = noSpecialChar;
    }

    public SettingDTO getSpecialChars() {
        return specialChars;
    }

    public void setSpecialChars(SettingDTO specialChars) {
        this.specialChars = specialChars;
    }

    public int getNumOfDigits() {
        return numOfDigits;
    }

    public void setNumOfDigits(int numOfDigits) {
        this.numOfDigits = numOfDigits;
    }

    public int getNoOfSpecial() {
        return noOfSpecial;
    }

    public void setNoOfSpecial(int noOfSpecial) {
        this.noOfSpecial = noOfSpecial;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getSpecialCharacters() {
        return specialCharacters;
    }

    public void setSpecialCharacters(String specialCharacters) {
        this.specialCharacters = specialCharacters;
    }
}