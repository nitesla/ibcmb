package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.*;
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
    private CorporateRepo corporateRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private CronJobExpressionRepo cronJobExpressionRepo;
    @Autowired
    private CronJobMonitorRepo cronJobMonitorRepo;
    @Override
    public void updateAllAccountName(Account account, AccountDetails accountDetails) throws InternetBankingException {
        if (!account.getAccountName().equalsIgnoreCase(accountDetails.getAcctName())) {
            account.setAccountName(accountDetails.getAcctName());
//            System.out.println("the account name after setting is" + account.getAccountName());
//            accountRepo.save(account);
        }
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
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findLastByFlag("Y");
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
//            logger.info("the new account currency {} and {}" , account.getCurrencyCode(),accountDetails.getAcctCrncyCode());
            accountRepo.save(account);
        }
    }
    @Override
    public void updateAccountStatus(Account account, AccountDetails accountDetails) throws InternetBankingException {
        if ((account.getStatus()==null)||(!account.getStatus().equalsIgnoreCase(""))||(!account.getStatus().equalsIgnoreCase(accountDetails.getAcctStatus()))) {
            account.setStatus(accountDetails.getAcctStatus());
//            System.out.println("the account status after setting is" + account.getStatus());
            accountRepo.save(account);
        }
    }


    @Override
    public boolean updateAccountDetials() throws InternetBankingException {
        List<Account> allAccounts = accountRepo.findAll();
        if(allAccounts.size()>0){
            for (Account account : allAccounts) {
                AccountDetails accountDetails = integrationService.viewAccountDetails(account.getAccountNumber());
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
    public boolean updateRetailUserDetails() throws InternetBankingException {
        List<RetailUser>retailUsers =  retailUserRepo.findAll();
        for (RetailUser retailUser:retailUsers) {
            try {
//                logger.info("old bvn is {}",retailUser.getCustomerId());
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(retailUser.getCustomerId());
                updateRetailUserBVN(retailUser,details);
                updateRetailUserPhoneNo(retailUser,details);
                updateRetailUserEmail(retailUser,details);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
//        viewCustomerDetailsByCif();
        return false;
    }

    @Override
    public void updateRetailUserBVN(RetailUser retailUser, CustomerDetails details) throws InternetBankingException {
        String userBvn = retailUser.getBvn();
        if((userBvn == null)||userBvn.equalsIgnoreCase("")||(!userBvn.equalsIgnoreCase(details.getBvn()))){
            retailUser.setBvn(details.getBvn());
//            logger.info("new bvn is {}",details.getBvn());
            retailUserRepo.save(retailUser);
        }
    }

    @Override
    public void updateRetailUserPhoneNo(RetailUser retailUser, CustomerDetails details) throws InternetBankingException {
        String retailUserPhoneNumber = retailUser.getPhoneNumber();
//        logger.info("new phone number is {}",retailUser.getPhoneNumber());
        if((retailUserPhoneNumber == null)||retailUserPhoneNumber.equalsIgnoreCase("")||(!retailUserPhoneNumber.equalsIgnoreCase(details.getPhone()))){
            retailUser.setPhoneNumber(details.getPhone());
//            logger.info("new phone number 2 is {}",details.getPhone());

            retailUserRepo.save(retailUser);
        }
    }

    @Override
    public void updateRetailUserEmail(RetailUser retailUser, CustomerDetails details) throws InternetBankingException {
        String retailUserEmail = retailUser.getEmail();
//        logger.info("new email is {}",retailUser.getEmail());
        if((retailUserEmail == null)||retailUserEmail.equalsIgnoreCase("")||(!retailUserEmail.equalsIgnoreCase(details.getEmail()))){
            retailUser.setEmail(details.getEmail());
//            logger.info("new email 2 is {}",details.getEmail());
            retailUserRepo.save(retailUser);
        }
    }
    @Override
    public boolean updateCorporateUserDetails() throws InternetBankingException {
        List<CorporateUser>corporateUsers =  corporateUserRepo.findAll();
        for (CorporateUser corporateUser:corporateUsers) {
            try {
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(corporateUser.getCorporate().getCustomerId());
                updateCorporateUserEmail(corporateUser,details);
                updateCorporateUserPhoneNo(corporateUser,details);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public boolean updateCorporateDetails() throws InternetBankingException {
        List<Corporate>corporates =  corporateRepo.findAll();
        for (Corporate corporate:corporates) {
            try {
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(corporate.getCustomerId());
                updateCorporateUserBVN(corporate,details);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    @Override
    public void updateCorporateUserBVN(Corporate corporate, CustomerDetails details) throws InternetBankingException {
        String corporateBvn = corporate.getBvn();
        if((corporateBvn == null)||corporateBvn.equalsIgnoreCase("")||(!corporateBvn.equalsIgnoreCase(details.getBvn()))){
            corporate.setBvn(details.getBvn());
//            logger.info("new corp bvn is {}",corporate.getBvn());
            try {
                corporateRepo.save(corporate);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateCorporateUserPhoneNo(CorporateUser corporateUser, CustomerDetails details) throws InternetBankingException {
        String corporateUserPhoneNumber = corporateUser.getPhoneNumber();
        if((corporateUserPhoneNumber == null)||corporateUserPhoneNumber.equalsIgnoreCase("")||(!details.getPhone().equalsIgnoreCase(corporateUserPhoneNumber))){
            corporateUser.setPhoneNumber(details.getPhone());
            logger.info("new corp phone is {}",details.getPhone());
            try {
                corporateUserRepo.save(corporateUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateCorporateUserEmail(CorporateUser corporateUser, CustomerDetails details) throws InternetBankingException {
        String corporateUserEmail = corporateUser.getEmail();
        if((corporateUserEmail == null)||corporateUserEmail.equalsIgnoreCase("")||(!corporateUserEmail.equalsIgnoreCase(details.getEmail()))){
            corporateUser.setEmail(details.getEmail());
//            logger.info("new corp email is {}",details.getEmail());
            try {
                corporateUserRepo.save(corporateUser);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
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
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findLastByFlag("Y");

        return cronJobExpression.getCronExpression();
    }

    @Override
    public boolean updateRunningJob() throws InternetBankingException {
        CronJobMonitor lastIncompleteJob = cronJobMonitorRepo.findLastByStillRunning(true);
//        logger.info("monitor is {}", lastIncompleteJob);
            if(lastIncompleteJob != null) {
//                logger.info("monitor time is {}", lastIncompleteJob.getJobStartTime());
                lastIncompleteJob.setJobEndTime(new Date());
                lastIncompleteJob.setStillRunning(false);
                cronJobMonitorRepo.save(lastIncompleteJob);
                return true;
            }else {
                return false;
            }
    }


}
