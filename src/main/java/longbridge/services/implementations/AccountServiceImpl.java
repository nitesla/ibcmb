package longbridge.services.implementations;

import longbridge.models.Account;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by chigozirim on 3/29/17.
 */
@Service
public class AccountServiceImpl implements AccountService{
    private AccountRepo accountRepo;

    private IntegrationService integrationService;

    @Autowired
    public AccountServiceImpl(AccountRepo accountRepo, IntegrationService integrationService){
        this.accountRepo = accountRepo;
        this.integrationService = integrationService;
    }

    @Override
    public Account getAccount(Long accId) {
        return accountRepo.findById(accId);
    }

    @Override
    public Iterable<Account> getAccounts(String customerId) {
        List<Account> accountList = accountRepo.findByCustomerId(customerId);
        return accountList;
    }

    @Override
    public Map<String, BigDecimal> getBalance(Account account) {
       return integrationService.getBalance(account.getAccountId());
    }

    @Override
    public AccountStatement getAccountStatements(Account account, LocalDate fromDate, LocalDate toDate) {
        return integrationService.getAccountStatements(account.getAccountId(), fromDate, toDate);
    }


//    private Account mockAccount;
//
//    /** Creates an empty {@link longbridge.models.Account} object which will be
//     * returned in place of null
//     * @return {@code Account} object containing "null" as account name
//     */
//    private Account mockAccount(){
//        if(mockAccount==null){
//            mockAccount = new Account();
//            mockAccount.setAccountName("null");
//        }
//        return mockAccount;
//    }


}
