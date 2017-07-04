package longbridge.services;

import longbridge.exception.InternetBankingException;
import longbridge.models.CronJob;

/**
 * Created by Longbridge on 6/24/2017.
 */
public interface CronJobService {
    Boolean updateAllAccountName() throws InternetBankingException;
    Boolean updateAllBVN() throws InternetBankingException;
    void keepJobDetial(String username, String cronExp) throws InternetBankingException;
    void deleteRunningJob() throws InternetBankingException;
    Boolean updateAllAccountCurrency() throws InternetBankingException;
}