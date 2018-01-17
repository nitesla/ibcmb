package longbridge.validator;

import longbridge.dtos.SettingDTO;
import longbridge.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by Fortune on 9/7/2017.
 */

@Component
@Scope("singleton")
public class EmailValidator {

    @Autowired
    private ConfigurationService configService;

    public boolean isValid(String email) {

        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }


    public boolean isValid(String email, String domainName) {

        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            String domainPart = StringUtils.substringAfter(email, "@");
            if (domainPart.equalsIgnoreCase(domainName)) {
                return true;
            }
        } catch (AddressException e) {
            return false;
        }
        return false;
    }


    public  boolean validate(String email) {
        SettingDTO validateCorporateEmail = configService.getSettingByName("VALIDATE_CORPORATE_EMAIL");
        SettingDTO domainName = configService.getSettingByName("INTERNET_DOMAIN_NAME");

        if (validateCorporateEmail != null && validateCorporateEmail.isEnabled() && "YES".equalsIgnoreCase(validateCorporateEmail.getValue())) {

            if (domainName != null && domainName.isEnabled()) {
                if (domainName.getValue() != null && !domainName.getValue().isEmpty()) {
                    return isValid(email, domainName.getValue());
                }
            }
        }
        return isValid(email);
    }

}

