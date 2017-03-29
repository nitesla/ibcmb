package longbridge.services;

import longbridge.models.CorporateUser;
import longbridge.models.TransactionType;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface CorporateService {

    void addCorporate();

    void deleteCorporate();

    void updateCorporate();

    void getCorporate();

    void getCorporates();

    void setLimit(TransactionType transactionType, double limitValue);

    void updateLimit();

    void getLimit();

    void deleteLimit();

    void setcorporateLimit(CorporateUser corporateUser, double limit);

    void setCorporateUserLimit(CorporateUser corporateUser, double limit);

    void setUserLimit();

    void updateUserLimits();

    void getUserLimits();

    void deleteUserLimits();

    void setTransactionLimit(TransactionType transactionType, double limit, String currency);

    void updateTransactionLimits();

    void getTransactionLimits();

    void deleteTransactionLimits();

    void addAccount();

    void addUser();

    void disableUser();
}
