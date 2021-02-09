package longbridge.services;

import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Longbridge on 6/24/2017.
 */
@Transactional
public interface CronJobService {
    void updateAllAccountName(Account account, AccountDetails accountDetails) ;
    boolean updateRetailUserDetails() ;
    boolean updateCorporateUserDetails() ;
    boolean updateCorporateDetails() ;
    void keepCronJobEprsDetials(String username, String cronExp, String cronExprDesc, String category) ;
    void deleteRunningJob(String category) ;
    void updateAllAccountCurrency(Account account, AccountDetails accountDetails) ;
    void updateAccountStatus(Account account, AccountDetails accountDetails)  ;
    boolean updateAccountDetials() ;
    void updateRetailUserBVN(RetailUser retailUser, CustomerDetails details) ;
    void updateRetailUserPhoneNo(RetailUser retailUser, CustomerDetails details) ;
    void updateRetailUserEmail(RetailUser retailUser, CustomerDetails details) ;
    void updateCorporateUserBVN(Corporate corporate, CustomerDetails details) ;

    void updateCorporateUserTaxId(Corporate corporate, CustomerDetails details) ;
    void updateCorporateRCNum(Corporate corporate, CustomerDetails details) ;

    void updateCorporateUserPhoneNo(CorporateUser corporateUser, CustomerDetails details) ;
    void updateCorporateUserEmail(CorporateUser corporateUser, CustomerDetails details) ;
    void saveRunningJob(String jobCategory, String cronExpression) ;
    String getCurrentExpression(String category) ;
    String getCurrentJobDesc(String category) ;
    boolean updateRunningJob() ;
    boolean startCronJob() ;
    boolean stopJob() ;
    void addNewAccount() ;
    void executeAutoAdminDeactivation();
    void refreshPaymentBillers();

    void addCoverageForNewCodes();
    void neftSettlementFirstWindow();
    void neftSettlementSecondWindow();
    void neftSettlementThirdWindow();
    void neftSettlementFourthWindow();


}