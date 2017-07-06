package longbridge.services;

import longbridge.api.AccountDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;

/**
 * Created by Longbridge on 6/24/2017.
 */
public interface CronJobService {
    void updateAllAccountName(Account account, AccountDetails accountDetails) throws InternetBankingException;
    boolean updateAllBVN() throws InternetBankingException;
    void keepCronJobEprsDetials(String username, String cronExp) throws InternetBankingException;
    void deleteRunningJob() throws InternetBankingException;
    void updateAllAccountCurrency(Account account, AccountDetails accountDetails) throws InternetBankingException;
    void updateAccountStatus(Account account, AccountDetails accountDetails) throws InternetBankingException ;
    boolean updateAccountDetials() throws InternetBankingException;
    void saveRunningJob(String jobCategory,String cronExpression) throws InternetBankingException;
    String getCurrentExpression() throws InternetBankingException;
    boolean updateRunningJob() throws InternetBankingException;
}