package longbridge.services.implementations;

import longbridge.models.Account;
import longbridge.models.Customer;
import longbridge.models.FinancialTransaction;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by chigozirim on 3/29/17.
 */
public class AccountServiceImpl implements AccountService{
    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private IntegrationService integrationService;

    @Override
    public Account getAccount(Long accId) {
        Account account = accountRepo.findById(accId);
        return (account == null) ? mockAccount() : account;
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
    public List<AccountStatement> getAccountStatements(Account account, Date fromDate, Date toDate) {
        return integrationService.fetchAccountStatement(account.getAccountId(), fromDate, toDate);
    }


    private static Account mockAccount;

    /** Creates an empty {@link longbridge.models.Account} object which will be
     * returned in place of null
     * @return {@code Account} object containing "null" as account name
     */
    private Account mockAccount(){
        if(mockAccount==null){
            mockAccount = new Account();
            mockAccount.setAccountName("null");
        }
        return mockAccount;
    }


}
