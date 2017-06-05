package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.models.*;
import longbridge.repositories.*;
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
    private String specialCharacters = "~!@#$%^&;'?.";
    private int numOfChanges = 0;
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

            if (numOfDigits > 0) {
                ruleMessage = String.format("Minimum number of digits required in password is %d", numOfDigits);
                passwordRules.add(ruleMessage);
            }

        }
        if (noSpecialChar != null && noSpecialChar.isEnabled()) {
            noOfSpecial = NumberUtils.toInt(noSpecialChar.getValue());

            if (noOfSpecial > 0) {
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
        if (numOfChangesBeforeReuse != null && numOfChangesBeforeReuse.isEnabled()) {
            numOfChanges = NumberUtils.toInt(numOfChangesBeforeReuse.getValue());
            ruleMessage = String.format("Password reuse must be after %d usages of different passwords", numOfChanges);
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

        return passwordCreator.generatePassword(minLength, numOfDigits, noOfSpecial, specialCharacters);
    }


    public void saveAdminPassword(AdminUser adminUser){
        int count = adminPasswordRepo.countByAdminUser(adminUser);

        AdminPassword adminPassword = new AdminPassword();
        adminPassword.setAdminUser(adminUser);
        adminPassword.setPassword(adminUser.getPassword());
        if(count<numOfChanges) {
            adminPasswordRepo.save(adminPassword);
        }
        else {
            AdminPassword firstPassword = adminPasswordRepo.findFirstByAdminUser(adminPassword.getAdminUser());
            adminPasswordRepo.delete(firstPassword);
            adminPasswordRepo.save(adminPassword);
        }
    }

    public void saveOpsPassword(OperationsUser operationsUser){
        int count = opsPasswordRepo.countByOperationsUser(operationsUser);

        OpsPassword opsPassword = new OpsPassword();
        opsPassword.setOperationsUser(operationsUser);
        opsPassword.setPassword(operationsUser.getPassword());
        if(count<numOfChanges) {
            opsPasswordRepo.save(opsPassword);
        }
        else {
            OpsPassword firstPassword = opsPasswordRepo.findFirstByOperationsUser(opsPassword.getOperationsUser());
            opsPasswordRepo.delete(firstPassword);
            opsPasswordRepo.save(opsPassword);
        }
    }

    public void saveRetailPassword(RetailUser retailUser){
        int count = retailPasswordRepo.countByRetailUser(retailUser);

        RetailPassword retailPassword = new RetailPassword();
        retailPassword.setRetailUser(retailUser);
        retailPassword.setPassword(retailUser.getPassword());
        if(count<numOfChanges) {
            retailPasswordRepo.save(retailPassword);
        }
        else {
            RetailPassword firstPassword = retailPasswordRepo.findFirstByRetailUser(retailPassword.getRetailUser());
            retailPasswordRepo.delete(firstPassword);
            retailPasswordRepo.save(retailPassword);
        }
    }

    public void saveCorporatePassword(CorporateUser corporateUser){
        int count = corporatePasswordRepo.countByCorporateUser(corporateUser);

        CorporatePassword corporatePassword = new CorporatePassword();
        corporatePassword.setCorporateUser(corporateUser);
        corporatePassword.setPassword(corporateUser.getPassword());
        if(count<numOfChanges) {
            corporatePasswordRepo.save(corporatePassword);
        }
        else {
            CorporatePassword firstPassword = corporatePasswordRepo.findFirstByCorporateUser(corporatePassword.getCorporateUser());
            corporatePasswordRepo.delete(firstPassword);
            corporatePasswordRepo.save(corporatePassword);
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
}
