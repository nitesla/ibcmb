package longbridge.validator;

import longbridge.dtos.SettingDTO;
import longbridge.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fortune on 4/13/2017.
 */
@Component
@Scope("singleton")
public class PasswordValidator {

    @Autowired
    PasswordEncoder passwordEncoder;

    private Pattern digitPattern = Pattern.compile("[0-9]");
    private Pattern letterPattern = Pattern.compile("[a-zA-Z]");
    private Pattern specialCharsPattern;


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
    private String specialCharacters = "!@#$%^)(&";
    private int numOfChanges = 0;
    private boolean initialized = false;
    private StringBuilder errorMessage;
    private String message = "";


    @Autowired
    ConfigurationService configService;

    private void init() {
        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LEN");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LEN");
        specialChars = configService.getSettingByName("PASSWORD_SPECIAL_CHARS");
        numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");


        if (numOfPasswordDigits != null && numOfPasswordDigits.isEnabled()) {
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
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());

        }

        specialCharsPattern = Pattern.compile("[" + specialCharacters + "]");
        initialized = true;
    }

    public String validate(String password, String usedPasswords) {
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

        if (!isPasswordReuseable(password, usedPasswords)) {
            message = String.format(
                    "Previous password can only be reused after %d different passwords", numOfChanges);
            errorMessage.append(message);
            errorMessage.append(".\n");
        }

        return errorMessage.toString();
    }

    /**
     * Checks if the specified password can be reused by the user
     * The password policy allows a password to be reused after a certain number of different
     * passwords have been used by the user
     *
     * @param password      the password to be used
     * @param usedPasswords the comma-separated list of used passwords
     * @return true if the password can be reused
     */
    private boolean isPasswordReuseable(String password, String usedPasswords) {
        boolean isReusable = false;
        if (usedPasswords == null) {
            isReusable=true;
            return isReusable;
        }
        List<String> passwordHashes = Arrays.asList(StringUtils.split(usedPasswords, ","));
        for (String passwordHash : passwordHashes) {
            if (passwordEncoder.matches(password, passwordHash)) {
                if (passwordHashes.size() - getPasswordHashPosition(password, usedPasswords) > numOfChanges) {
                    isReusable = true;
                    return isReusable;
                }
            }
        }
        return isReusable;
    }

    /**
     * Gets the position of the passwordHash from the list of password hashes
     * The first entry in our list is given a value of 1 even though Java List starts with an index of 0.
     * If the password is not found, we return 0
     */
    private int getPasswordHashPosition(String password, String usedPasswords) {
        int position = 0;
        List<String> usedPasswordHashes = Arrays.asList(StringUtils.split(usedPasswords, ","));
        for (String passwordHash : usedPasswordHashes) {
            if (passwordEncoder.matches(password, passwordHash)) {
                position = usedPasswordHashes.lastIndexOf(passwordHash) + 1;
            }
        }
        return position;
    }
}