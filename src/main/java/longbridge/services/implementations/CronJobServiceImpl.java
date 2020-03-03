package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;
import longbridge.api.CustomerDetails;
import longbridge.api.TransferDetails;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.services.AccountService;
import longbridge.services.ConfigurationService;
import longbridge.services.CronJobService;
import longbridge.services.IntegrationService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Longbridge on 6/24/2017.
 */
@Service
@Transactional
public class CronJobServiceImpl implements CronJobService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Autowired
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
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransferRequestRepo transferRequestRepo;

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    public CronJobServiceImpl(IntegrationService integrationService){
    this.integrationService=integrationService;
    }

    @Override
    public void updateAllAccountName(Account account, AccountDetails accountDetails) throws InternetBankingException {
        if (!account.getAccountName().equalsIgnoreCase(accountDetails.getAcctName())) {
            account.setAccountName(accountDetails.getAcctName());
//            System.out.println("the account name after setting is" + account.getAccountName());
            accountRepo.save(account);
        }
    }

    @Override
    public void keepCronJobEprsDetials(String username, String cronExpression,String cronExprDesc,String category) throws InternetBankingException {
        CronJobExpression cronJobExpression = new CronJobExpression();
        cronJobExpression.setUsername(username);
        cronJobExpression.setCreatedOn(new Date());
        cronJobExpression.setFlag("Y");
        cronJobExpression.setCronExpression(cronExpression);
        cronJobExpression.setCronExpressionDesc(cronExprDesc);
        cronJobExpression.setCategory(category);
        cronJobExpressionRepo.save(cronJobExpression);
    }

    @Override
    public void deleteRunningJob(String category) throws InternetBankingException {
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findLastByFlagAndCategory("Y",category);
        logger.info("the job to be deleted {}",cronJobExpression);
        if (cronJobExpression != null){
//            logger.info("about deleting");
            cronJobExpression.setFlag("N");
            cronJobExpressionRepo.save(cronJobExpression);
        }
    }

    @Override
    public void updateAllAccountCurrency(Account account, AccountDetails accountDetails) throws InternetBankingException {
//        logger.info("The account size {}",allAccounts.size());
        if (accountDetails.getAcctCrncyCode() !=null && (!accountDetails.getAcctCrncyCode().equalsIgnoreCase(account.getCurrencyCode()))) {
            account.setCurrencyCode(accountDetails.getAcctCrncyCode());
            logger.trace("the new account currency {} and {}" , account.getCurrencyCode(),accountDetails.getAcctCrncyCode());
            accountRepo.save(account);
        }
    }
    @Override
    public void updateAccountStatus(Account account, AccountDetails accountDetails) throws InternetBankingException {
//        logger.info("the account status after setting is {} and number {}", account.getStatus(),account.getAccountNumber(),accountDetails.getAcctStatus());

        if ((account.getStatus()==null)||(account.getStatus().equalsIgnoreCase(""))||(!account.getStatus().equalsIgnoreCase(accountDetails.getAcctStatus()))) {
            account.setStatus(accountDetails.getAcctStatus());
            logger.trace("the account status after setting is {} and number {}", account.getStatus(),account.getAccountNumber(),accountDetails.getAcctStatus());
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
                    if(accountDetails  != null) {
//                updateAllAccountName(account,accountDetails);
                        updateAllAccountCurrency(account, accountDetails);
                        updateAccountStatus(account, accountDetails);
                    }
//                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
//                    return false;
                }
            }
        }
        return false;
    }
    @Override
    public boolean updateRetailUserDetails() throws InternetBankingException {
        List<RetailUser>retailUsers =  retailUserRepo.findAll();
//        logger.info("the users gotten {}",retailUsers.size());
        for (RetailUser retailUser:retailUsers) {
            try {
//                logger.info("old bvn is {}",retailUser.getCustomerId());
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(retailUser.getCustomerId());

                if(details != null) {
                    logger.trace("updating details {}",details.getPhone());
                    updateRetailUserBVN(retailUser, details);
                    updateRetailUserPhoneNo(retailUser, details);
                    updateRetailUserEmail(retailUser, details);
                }
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
            logger.trace("new bvn is {}",details.getBvn());
            retailUserRepo.save(retailUser);
        }
    }

    @Override
    public void updateRetailUserPhoneNo(RetailUser retailUser, CustomerDetails details) throws InternetBankingException {
        String retailUserPhoneNumber = retailUser.getPhoneNumber();
//        logger.info("new phone number is {}",retailUser.getPhoneNumber());
        if((retailUserPhoneNumber == null)||retailUserPhoneNumber.equalsIgnoreCase("")||(!retailUserPhoneNumber.equalsIgnoreCase(details.getPhone()))){
            retailUser.setPhoneNumber(details.getPhone());
            logger.trace("new phone number 2 is {}",details.getPhone());

            retailUserRepo.save(retailUser);
        }
    }

    @Override
    public void updateRetailUserEmail(RetailUser retailUser, CustomerDetails details) throws InternetBankingException {
        String retailUserEmail = retailUser.getEmail();
//        logger.info("new email is {} and username {}",retailUser.getEmail(),retailUser.getUserName());
        if((retailUserEmail == null)||retailUserEmail.equalsIgnoreCase("")||(!retailUserEmail.equalsIgnoreCase(details.getEmail()))){
            retailUser.setEmail(details.getEmail());
            logger.trace("new email 2 is {}",details.getEmail());
            retailUserRepo.save(retailUser);
        }
    }
    @Override
    public boolean updateCorporateUserDetails() throws InternetBankingException {
//        List<CorporateUser>corporateUsers =  corporateUserRepo.findAll();
//        for (CorporateUser corporateUser:corporateUsers) {
//            try {
//                CustomerDetails details = integrationService.viewCustomerDetailsByCif(corporateUser.getCorporate().getCustomerId());
//                updateCorporateUserEmail(corporateUser,details);
//                updateCorporateUserPhoneNo(corporateUser,details);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
        return false;
    }
    public boolean updateCorporateDetails() throws InternetBankingException {
        List<Corporate>corporates =  corporateRepo.findAll();
        for (Corporate corporate:corporates) {
            try {
                CustomerDetails details = integrationService.viewCustomerDetailsByCif(corporate.getCustomerId());
                if(details != null) {
                    updateCorporateUserBVN(corporate, details);
                    updateCorporateUserTaxId(corporate, details);
                    updateCorporateRCNum(corporate, details);
                }
            } catch (Exception e) {
                logger.info("the error {}",e.getMessage());
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
            logger.trace("Updating Corporate BVN for {} to {}",corporate.getCustomerId(),corporate.getBvn());
            try {
                corporateRepo.save(corporate);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateCorporateUserTaxId(Corporate corporate, CustomerDetails details) throws InternetBankingException {
        String taxId = corporate.getTaxId();
        if((taxId == null)||taxId.equalsIgnoreCase("")||(!taxId.equalsIgnoreCase(details.getTaxId()))){
            corporate.setTaxId(details.getTaxId());
            logger.trace("Updating Corporate Tax ID for {} to {}",corporate.getCustomerId(),corporate.getTaxId());
            try {
                corporateRepo.save(corporate);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }
    public void updateCorporateRCNum(Corporate corporate, CustomerDetails details){
        String RCNum = corporate.getRcNumber();
        if((RCNum == null)||RCNum.equalsIgnoreCase("")||(!RCNum.equalsIgnoreCase(details.getRcNo()))){
            corporate.setRcNumber(details.getRcNo());
            logger.trace("Updating Corporate RCNumber for {} to {}",corporate.getCustomerId(),corporate.getRcNumber());
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
            logger.trace("new corp phone is {}",details.getPhone());
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
            logger.trace("new corp email is {}",details.getEmail());
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
    public String getCurrentExpression(String category) throws InternetBankingException {
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findLastByFlagAndCategory("Y",category);

        return cronJobExpression.getCronExpression();
    }
    @Override
    public String getCurrentJobDesc(String category) throws InternetBankingException {
        CronJobExpression cronJobExpression = cronJobExpressionRepo.findLastByFlagAndCategory("Y",category);
        if(cronJobExpression != null) {
            return cronJobExpression.getCronExpressionDesc();
        }else {
            return "No Job has been configured for this category";
        }
    }

    @Override
    public boolean updateRunningJob() throws InternetBankingException {
        CronJobMonitor lastIncompleteJob = cronJobMonitorRepo.findFirstByOrderByStillRunningDesc();
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

    @Override
    public boolean startCronJob() throws InternetBankingException {
        SettingDTO setting = configService.getSettingByName("ENABLE_CRON_JOB");
        if(setting != null && setting.isEnabled()) {
            logger.info("the setting read is {}",setting.isEnabled());
            return true;
        }
        return false;
    }

    @Override
    public boolean stopJob() throws InternetBankingException {
        return false;
    }

    @Override
    public void addNewAccount() throws InternetBankingException {
        List<RetailUser>retailUsers =  retailUserRepo.findAll();
        SettingDTO setting = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        String[] list = StringUtils.split(setting.getValue(), ",");
//        logger.info("the scheme code {}",setting);
        if (setting != null && setting.isEnabled()) {
            for (RetailUser retailUser:retailUsers) {
                try {
                    List<String> existingAccount =  new ArrayList<>();
                logger.trace("old bvn is {}",retailUser.getCustomerId());
                    List<AccountInfo> accountInfos = integrationService.fetchAccounts(retailUser.getCustomerId());
                    List<Account> accounts =  accountService.getCustomerAccounts(retailUser.getCustomerId());
                    for (Account account:accounts) {
                        existingAccount.add(account.getAccountNumber());
                    }

                    for (AccountInfo accountInfo:accountInfos) {
                        if (ArrayUtils.contains(list, accountInfo.getSchemeType()) && "A".equalsIgnoreCase(accountInfo.getAccountStatus())) {
                            if(!existingAccount.contains(accountInfo.getAccountNumber())){
                                logger.info("creating new account {}",accountInfo.getAccountNumber());
                                accountService.AddFIAccount(retailUser.getCustomerId(),accountInfo);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Scheduled(cron="${antifraud.status}")
    public void updateSuspectedFraudTransactionStatus(){
        List<TransRequest> suspectedTransRequest= transferRequestRepo.findByStatus("34");
        logger.info("checking for declined suspected fraud transfers");

        suspectedTransRequest.stream().filter(Objects::nonNull).forEach(transRequest -> {

            String tranType="";

            if(transRequest.getTransferType().toString().equalsIgnoreCase("INTER_BANK_TRANSFER")){
                tranType="NIP";
            }else tranType="INTRABANK";

            logger.info("transferType {}",tranType);

            TransferDetails transferDetails= integrationService.antiFraudStatusCheck(tranType,transRequest.getReferenceNumber());


           if(null!=transferDetails.getResponseCode() && !transferDetails.getResponseCode().equalsIgnoreCase(transRequest.getStatus())){
                TransRequest request=transferRequestRepo.findByReferenceNumberAndStatus(transRequest.getReferenceNumber(),transRequest.getStatus());
                request.setStatusDescription(transferDetails.getResponseDescription());
                request.setStatus(transferDetails.getResponseCode());

                transferRequestRepo.save(request);
           }



            logger.info("transferDetails {}",transferDetails);

        });

    }

    @Override
    @Scheduled(cron="${auto.admin.deactivation}")
    public void executeAutoAdminDeactivation(){
        SettingDTO setting = configService.getSettingByName("ADMIN_DEACTIVATION");
        if(setting != null && setting.isEnabled()) {
            adminUserRepo.updateUserStatus(Double.parseDouble(setting.getValue()));
            logger.info("Inactive AdminUsers deactivated");
        }else
            adminUserRepo.updateUserStatus(60.0);
    }

}
