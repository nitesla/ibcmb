package longbridge.services;

import longbridge.dtos.PasswordStrengthDTO;
import longbridge.dtos.SettingDTO;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.utils.PasswordCreator;
import longbridge.validator.PasswordValidator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Fortune on 5/3/2017.
 */

@Service
public class PasswordPolicyService {

    private String ruleMessage = "";
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private PasswordValidator passwordValidator;
    @Autowired
    private PasswordCreator passwordCreator;
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

    private List<String> passwordRules;
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
    private String specialCharacters = "@#$%&";
    private int numOfChanges = 0;
    private boolean initialized = false;

    private Locale locale = LocaleContextHolder.getLocale();

    private void init() {

        passwordRules = new ArrayList<>();
        numOfPasswordDigits = configService.getSettingByName("PASSWORD_NUM_DIGITS");
        noSpecialChar = configService.getSettingByName("PASSWORD_NUM_SPECIAL_CHARS");
        minLengthOfPassword = configService.getSettingByName("PASSWORD_MIN_LENGTH");
        maxLengthOfPassword = configService.getSettingByName("PASSWORD_MAX_LENGTH");
        specialChars = configService.getSettingByName("PASSWORD_SPECIAL_CHARS");
        numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");


        if (numOfPasswordDigits != null && numOfPasswordDigits.isEnabled()) {
            numOfDigits = NumberUtils.toInt(numOfPasswordDigits.getValue());

            if (numOfDigits > 0) {
                ruleMessage = String.format(messageSource.getMessage("password.num.digit", null, locale), numOfDigits);
                passwordRules.add(ruleMessage);
            }

        }
        if (noSpecialChar != null && noSpecialChar.isEnabled()) {
            noOfSpecial = NumberUtils.toInt(noSpecialChar.getValue());

            if (noOfSpecial > 0) {
                ruleMessage = String.format(messageSource.getMessage("password.num.spec.char", null, locale), noOfSpecial);
                passwordRules.add(ruleMessage);
            }

        }
        if (minLengthOfPassword != null && minLengthOfPassword.isEnabled()) {
            minLength = NumberUtils.toInt(minLengthOfPassword.getValue());
            ruleMessage = String.format(messageSource.getMessage("password.min.len", null, locale), minLength);
            passwordRules.add(ruleMessage);

        }
        if (maxLengthOfPassword != null && maxLengthOfPassword.isEnabled()) {
            maxLength = NumberUtils.toInt(maxLengthOfPassword.getValue());
            ruleMessage = String.format(messageSource.getMessage("password.max.len", null, locale), maxLength);
            passwordRules.add(ruleMessage);
        }
        if (specialChars != null && specialChars.isEnabled()) {
            specialCharacters = StringEscapeUtils.escapeHtml4(specialChars.getValue());
            ruleMessage = String.format(messageSource.getMessage("password.spec.chars", null, locale), specialCharacters);
            passwordRules.add(ruleMessage);

        }
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());
            ruleMessage = String.format(messageSource.getMessage("password.reuse", null, locale), numOfChanges);
            passwordRules.add(ruleMessage);

        }
        initialized = true;

    }

    public String validate(String password, User user) {
        return passwordValidator.validate(password, user);
    }


    public List<String> getPasswordRules() {
        init();
        return passwordRules;
    }

    public String generatePassword() {
        init();
        return passwordCreator.generatePassword(minLength, numOfDigits, specialCharacters);
    }


    public void saveAdminPassword(AdminUser adminUser) {

        SettingDTO numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            int count = adminPasswordRepo.countByUserId(adminUser.getId());
            int numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());

            AdminPassword adminPassword = new AdminPassword();
            adminPassword.setUserId(adminUser.getId());
            adminPassword.setPassword(adminUser.getPassword());
            if (numOfChanges > 0) {
                if (count < numOfChanges) {
                    adminPasswordRepo.save(adminPassword);
                } else {
                    AdminPassword firstPassword = adminPasswordRepo.findFirstByUserId(adminUser.getId());
                    adminPasswordRepo.delete(firstPassword);
                    adminPasswordRepo.save(adminPassword);
                }
            }
        }
    }

    public void saveOpsPassword(OperationsUser operationsUser) {

        SettingDTO numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            int count = opsPasswordRepo.countByUserId(operationsUser.getId());
            int numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());

            OpsPassword opsPassword = new OpsPassword();
            opsPassword.setUserId(operationsUser.getId());
            opsPassword.setPassword(operationsUser.getPassword());
            if (numOfChanges > 0) {
                if (count < numOfChanges) {
                    opsPasswordRepo.save(opsPassword);
                } else {
                    OpsPassword firstPassword = opsPasswordRepo.findFirstByUserId(operationsUser.getId());
                    opsPasswordRepo.delete(firstPassword);
                    opsPasswordRepo.save(opsPassword);
                }
            }
        }
    }

    public void saveRetailPassword(RetailUser retailUser) {

        SettingDTO numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            int count = retailPasswordRepo.countByUsername(retailUser.getUserName());
            int numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());

            if (numOfChanges > 0) {
                RetailPassword retailPassword = new RetailPassword();
                retailPassword.setUsername(retailUser.getUserName());
                retailPassword.setPassword(retailUser.getPassword());
                if (count < numOfChanges) {
                    retailPasswordRepo.save(retailPassword);
                } else {
                    RetailPassword firstPassword = retailPasswordRepo.findFirstByUsername(retailUser.getUserName());
                    retailPasswordRepo.delete(firstPassword);
                    retailPasswordRepo.save(retailPassword);
                }
            }
        }
    }

    public void saveCorporatePassword(CorporateUser corporateUser) {

        SettingDTO numOfChangesBeforeReuse = configService.getSettingByName("PASSWORD_REUSE");
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            int count = corporatePasswordRepo.countByUsername(corporateUser.getUserName());
            int numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());


            CorporatePassword corporatePassword = new CorporatePassword();
            corporatePassword.setUsername(corporateUser.getUserName());
            corporatePassword.setPassword(corporateUser.getPassword());

            if (numOfChanges > 0) {
                if (count < numOfChanges) {
                    corporatePasswordRepo.save(corporatePassword);
                } else {
                    CorporatePassword firstPassword = corporatePasswordRepo.findFirstByUsername(corporateUser.getUserName());
                    corporatePasswordRepo.delete(firstPassword);
                    corporatePasswordRepo.save(corporatePassword);
                }
            }
        }
    }

    public Date getPasswordExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        SettingDTO setting = configService.getSettingByName("PASSWORD_EXPIRY");
        if (setting != null && setting.isEnabled()) {
            int days = NumberUtils.toInt(setting.getValue());
            calendar.add(Calendar.DAY_OF_YEAR, days);
            return calendar.getTime();
        }
        return null;
    }


    public boolean displayPasswordExpiryDate(Date expiryDate) {

        if (expiryDate == null) {
            return false;
        }
        SettingDTO setting = configService.getSettingByName("PASSWORD_AUTO_RESET");
        if (setting != null && setting.isEnabled()) {

            int days = NumberUtils.toInt(setting.getValue());
            LocalDateTime dateToExpire = LocalDateTime.fromDateFields(expiryDate);
            LocalDateTime dateToStartNotifying = dateToExpire.minusDays(days);
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(dateToStartNotifying) && !now.isAfter(dateToExpire)) {
                return true;
            }
        }
        return false;
    }


    public PasswordStrengthDTO getPasswordStrengthParams() {
        init();
        PasswordStrengthDTO passwordStrengthDTO = new PasswordStrengthDTO();
        passwordStrengthDTO.setNumOfdigits(numOfDigits);
        passwordStrengthDTO.setSpecialChars(specialCharacters);
        passwordStrengthDTO.setNumOfSpecChar(noOfSpecial);
        passwordStrengthDTO.setMinLength(minLength);
        return passwordStrengthDTO;
    }
}
