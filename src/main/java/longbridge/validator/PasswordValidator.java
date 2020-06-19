package longbridge.validator;

import longbridge.dtos.SettingDTO;
import longbridge.models.*;
import longbridge.repositories.AdminPasswordRepo;
import longbridge.repositories.CorporatePasswordRepo;
import longbridge.repositories.OpsPasswordRepo;
import longbridge.repositories.RetailPasswordRepo;
import longbridge.services.ConfigurationService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fortune on 4/13/2017.
 */
@Component
@Scope("singleton")
public class PasswordValidator {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private AdminPasswordRepo adminPasswordRepo;

    @Autowired
    private RetailPasswordRepo retailPasswordRepo;

    @Autowired
    private OpsPasswordRepo opsPasswordRepo;

    @Autowired
    private CorporatePasswordRepo corporatePasswordRepo;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();


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
    private StringBuilder errorMessage;
    private String message = "";





    private void initializeSettings() {
        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LENGTH");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LENGTH");
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
    }

    public String validate(String password, User user) {

        initializeSettings();

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
            message = String.format(messageSource.getMessage("pass.num.digit",null,locale),
                    numOfDigits);
            errorMessage.append(message);
            errorMessage.append(".\n");
        }
        if (!specOK) {
            message = String.format(messageSource.getMessage("pass.num.spec.char",null,locale), noOfSpecial,
                    specialCharacters);
            errorMessage.append(message);
            errorMessage.append(".\n");

        }
        if (!noOK) {
            message = String.format(messageSource.getMessage(
                    "pass.length",null,locale), minLength, maxLength);
            errorMessage.append(message);
            errorMessage.append(".\n");
        }


            if (!isPasswordReuseable(password, user)) {
                message = String.format(messageSource.getMessage(
                        "pass.reuse",null,locale), numOfChanges);
                errorMessage.append(message);
                errorMessage.append(".\n");
            }


        return errorMessage.toString();
    }

    /**
     * Checks if the specified password can be reused by the user
     * The password policy allows a password to be reused after a certain number of different
     * passwords have been used.
     *
     * @param password      the password to be used
     * @param user the user whose passwords are to be checked
     * @return true if the password can be reused
     */
    private boolean isPasswordReuseable(String password, User user) {
        if (numOfChanges <= 0 || user == null) {
            return true;
        }

        switch (user.getUserType()){
            case ADMIN:
                List<AdminPassword> adminPasswords = adminPasswordRepo.findByUserId(user.getId());
                for(AdminPassword adminPassword: adminPasswords){
                    if(passwordEncoder.matches(password,adminPassword.getPassword())){
                        return false;
                    }
                }
            case OPERATIONS:
                List<OpsPassword> opsPasswords = opsPasswordRepo.findByUserId(user.getId());
                for(OpsPassword opsPassword: opsPasswords){
                    if(passwordEncoder.matches(password,opsPassword.getPassword())){
                        return false;
                    }
                }
            case RETAIL:
                List<RetailPassword> retailPasswords = retailPasswordRepo.findByUsername(user.getUserName());
                for(RetailPassword retailPassword: retailPasswords){
                    if(passwordEncoder.matches(password,retailPassword.getPassword())){
                        return false;
                    }
                }

            case CORPORATE:
                List<CorporatePassword> corporatePasswords = corporatePasswordRepo.findByUsername(user.getUserName());
                for(CorporatePassword corporatePassword: corporatePasswords){
                    if(passwordEncoder.matches(password,corporatePassword.getPassword())){
                        return false;
                    }
                }

        }

        return true;
    }


}