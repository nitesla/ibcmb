package longbridge.utils;

import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.CorporateUserService;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Longbridge on 8/1/2017.
 */
@Service
public class TokenUtils {

    private RetailUserService retailUserService;

    @Autowired
    public void setRetailUserService(RetailUserService retailUserService) {
        this.retailUserService = retailUserService;
    }

    @Autowired
    private CorporateUserService corporateUserService;

    public void resetTokenAttemptsForUser(String category, String username) {
        System.out.println("The category " + category);
        System.out.println("The username " + username);
        if (category.equalsIgnoreCase("RETAIL")) {
            RetailUser user = retailUserService.getUserByName(username);
            if (user != null) {
                System.out.println("the user setting");
                retailUserService.resetNoOfTokenAttempt(user);
            }
        } else if (category.equalsIgnoreCase("CORPORATE")) {
            CorporateUser user = corporateUserService.getUserByName(username);
            if (user != null) {
                corporateUserService.resetNoOfTokenAttempt(user);
            }
        }
    }
}
