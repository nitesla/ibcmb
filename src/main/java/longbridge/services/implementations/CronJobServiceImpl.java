package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.CronJobExpression;
import longbridge.models.CronJobMonitor;
import longbridge.models.RetailUser;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.CronJobExpressionRepo;
import longbridge.repositories.CronJobMonitorRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.CronJobService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Longbridge on 6/24/2017.
 */
@Service
public class CronJobServiceImpl implements CronJobService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private RetailUserRepo retailUserRepo;
    @Autowired
    private CronJobExpressionRepo cronJobExpressionRepo;
    @Autowired
    private CronJobMonitorRepo cronJobMonitorRepo;
    @Override
    public void updateAllAccountName(Account account, AccountDetails accountDetails) throws InternetBankingException {
            if (!account.getAccountName().equalsIgnoreCase(accountDetails.getAcctName())) {
            account.setAccountName(accountDetails.getAcctName());
                    System.out.println("the account name after setting is" + account.getAccountName());
//            accountRepo.save(account);
            }
    }

    @Override
    public boolean updateAllBVN() throws InternetBankingException {
        List<RetailUser>retailUsers =  retailUserRepo.findAll();
        for (RetailUser retailUser:retailUsers) {
            try {
                logger.info("old bvn is {}",retailUser.getCustomerId());
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(retailUser.getCustomerId());
                String userBvn = retailUser.getBvn();
                if((userBvn == null)||userBvn.equalsIgnoreCase("")||(!userBvn.equalsIgnoreCase(details.getBvn()))){
                    retailUser.setBvn(details.getBvn());
                    logger.info("new bvn is {}",retailUser.getBvn());
                    retailUserRepo.save(retailUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
//        viewCustomerDetailsByCif();
        return false;
    }

    @Override
    public void keepCronJobEprsDetials(String username, String cronExpression) throws InternetBankingException {
            CronJobExpression cronJobExpression = new CronJobExpression();
            cronJobExpression.setUsername(username);
            cronJobExpression.setCreatedOn(new Date());
            cronJobExpression.setFlag("Y");
            cronJobExpression.setCronExpression(cronExpression);
            cronJobExpressionRepo.save(cronJobExpression);
    }

    @Override
    public void deleteRunningJob() throws InternetBankingException {
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findByFlag("Y");
        if (cronJobExpression != null){
            logger.info("about deleting");
            cronJobExpression.setFlag("N");
            cronJobExpressionRepo.save(cronJobExpression);
        }
    }

    @Override
    public void updateAllAccountCurrency(Account account, AccountDetails accountDetails) throws InternetBankingException {
//        logger.info("The account size {}",allAccounts.size());
            if ((account.getCurrencyCode()==null)||(!account.getCurrencyCode().equalsIgnoreCase(""))||(!accountDetails.getAcctCrncyCode().equalsIgnoreCase(account.getCurrencyCode()))) {
            account.setCurrencyCode(accountDetails.getAcctCrncyCode());
                logger.info("the new account currency {} and {}" , account.getCurrencyCode(),accountDetails.getAcctCrncyCode());
            accountRepo.save(account);
    }
    }

    @Override
    public void updateAccountStatus(Account account, AccountDetails accountDetails) throws InternetBankingException {
            if ((account.getStatus()==null)||(!account.getStatus().equalsIgnoreCase(""))||(!account.getStatus().equalsIgnoreCase(accountDetails.getAcctStatus()))) {
                account.setStatus(accountDetails.getAcctStatus());
                System.out.println("the account status after setting is" + account.getStatus());
            accountRepo.save(account);}
    }


    @Override
    public boolean updateAccountDetials() throws InternetBankingException {
        logger.info("updating status");

        List<Account> allAccounts = accountRepo.findAll();
        logger.info("The account size {}",allAccounts.size());
        if(allAccounts.size()>0){
        for (Account account : allAccounts) {
            AccountDetails accountDetails = integrationService.viewAccountDetails(account.getAccountNumber());
            logger.info("account details {}",accountDetails.getAcctStatus());

            try {
//                updateAllAccountName(account,accountDetails);
                updateAllAccountCurrency(account,accountDetails);
                updateAccountStatus(account,accountDetails);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        }
        return false;
    }

    @Override
    public void saveRunningJob(String jobCategory, String cronExpression) throws InternetBankingException {
        CronJobMonitor jobMonitor = new CronJobMonitor();
        jobMonitor.setExpression(cronExpression);
        jobMonitor.setJobStartTime(new Date());
        jobMonitor.setJobCategory(jobCategory);
        jobMonitor.setStillRunning(true);
        cronJobMonitorRepo.save(jobMonitor);
    }

    @Override
    public String getCurrentExpression() throws InternetBankingException {
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findByFlag("Y");

        return cronJobExpression.getCronExpression();
    }

    @Override
    public boolean updateRunningJob() throws InternetBankingException {
        List<CronJobMonitor> incompleteJobs = cronJobMonitorRepo.findLastByStillRunning(true);
        if(incompleteJobs.size()>0){
            CronJobMonitor jobMonitor = incompleteJobs.get(incompleteJobs.size()-1);
        if(incompleteJobs.get(incompleteJobs.size()-1) != null) {
            logger.info("monitor time is {}", jobMonitor.getJobStartTime());
            jobMonitor.setJobEndTime(new Date());
            jobMonitor.setStillRunning(false);
            cronJobMonitorRepo.save(jobMonitor);
            return true;
        }else {
            return false;
        }
        }
        return false;
    }


}
