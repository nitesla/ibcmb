package longbridge.services;

import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;

/**
 * Created by Longbridge on 6/24/2017.
 */
public interface CronJobService {
    void updateAllAccountName(Account account, AccountDetails accountDetails) throws InternetBankingException;
    boolean updateRetailUserDetails() throws InternetBankingException;
    boolean updateCorporateUserDetails() throws InternetBankingException;
    boolean updateCorporateDetails() throws InternetBankingException;
    void keepCronJobEprsDetials(String username, String cronExp,String cronExprDesc) throws InternetBankingException;
    void deleteRunningJob() throws InternetBankingException;
    void updateAllAccountCurrency(Account account, AccountDetails accountDetails) throws InternetBankingException;
    void updateAccountStatus(Account account, AccountDetails accountDetails) throws InternetBankingException ;
    boolean updateAccountDetials() throws InternetBankingException;
    void updateRetailUserBVN(RetailUser retailUser,CustomerDetails details) throws InternetBankingException;
    void updateRetailUserPhoneNo(RetailUser retailUser,CustomerDetails details) throws InternetBankingException;
    void updateRetailUserEmail(RetailUser retailUser,CustomerDetails details) throws InternetBankingException;
    void updateCorporateUserBVN(Corporate corporate, CustomerDetails details) throws InternetBankingException;

    void updateCorporateUserTaxId(Corporate corporate, CustomerDetails details) throws InternetBankingException;
    void updateCorporateRCNum(Corporate corporate, CustomerDetails details) throws InternetBankingException;

    void updateCorporateUserPhoneNo(CorporateUser corporateUser, CustomerDetails details) throws InternetBankingException;
    void updateCorporateUserEmail(CorporateUser corporateUser,CustomerDetails details) throws InternetBankingException;
    void saveRunningJob(String jobCategory,String cronExpression) throws InternetBankingException;
    String getCurrentExpression() throws InternetBankingException;
    boolean updateRunningJob() throws InternetBankingException;
    boolean startCronJob() throws InternetBankingException;
    boolean stopJob() throws InternetBankingException;
    void addNewAccount() throws InternetBankingException;
}