package longbridge.services;

import longbridge.api.AccountDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.CronJob;

/**
 * Created by Longbridge on 6/24/2017.
 */
public interface CronJobService {
    void updateAllAccountName(Account account, AccountDetails accountDetails) throws InternetBankingException;
    boolean updateAllBVN() throws InternetBankingException;
    void keepJobDetials(String username, String cronExp) throws InternetBankingException;
    void deleteRunningJob() throws InternetBankingException;
    void updateAllAccountCurrency(Account account, AccountDetails accountDetails) throws InternetBankingException;
    void updateAccountStatus(Account account, AccountDetails accountDetails) throws InternetBankingException;
    boolean updateAccountDetials() throws InternetBankingException;
}