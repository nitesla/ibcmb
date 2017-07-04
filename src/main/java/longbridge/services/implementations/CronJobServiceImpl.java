package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.CronJob;
import longbridge.models.RetailUser;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.CronJobRepo;
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
    private CronJobRepo cronJobRepo;
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
    public void keepJobDetials(String username,String cronExpression) throws InternetBankingException {
            CronJob cronJob = new CronJob();
            cronJob.setUsername(username);
            cronJob.setCreatedOn(new Date());
            cronJob.setFlag("Y");
            cronJob.setCronExpression(cronExpression);
            cronJobRepo.save(cronJob);
    }

    @Override
    public void deleteRunningJob() throws InternetBankingException {
        CronJob cronJob = cronJobRepo.findByFlag("Y");
        if (cronJob != null){
            logger.info("about deleting");
            cronJob.setFlag("N");
            cronJobRepo.save(cronJob);
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
            if (!account.getAccountName().equalsIgnoreCase(accountDetails.getAcctName())) {
                account.setAccountName(accountDetails.getAcctName());
                System.out.println("the account name after setting is" + account.getAccountName());
//            accountRepo.save(account);
    }
    }

    @Override
    public boolean updateAccountDetials() throws InternetBankingException {
        List<Account> allAccounts = accountRepo.findAll();
//        logger.info("The account size {}",allAccounts.size());
        for (Account account : allAccounts) {
            AccountDetails accountDetails = integrationService.viewAccountDetails(account.getAccountNumber());
//            updateAllAccountName(account,accountDetails);
//            updateAllAccountCurrency(account,accountDetails);
//            updateAccountStatus(account,accountDetails);
        }
        return false;
    }

}
