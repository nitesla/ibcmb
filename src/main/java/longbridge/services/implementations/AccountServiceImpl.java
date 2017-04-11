package longbridge.services.implementations;

import longbridge.models.Account;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
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
    public boolean AddAccount(String customerId, Account account) {
        if (!customerId.equals(account.getCustomerId())) {
            return false;
        }
        accountRepo.save(account);
        return true;
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
    public AccountStatement getAccountStatements(Account account, Date fromDate, Date toDate) {
        return integrationService.getAccountStatements(account.getAccountId(), fromDate, toDate);
    }

    @Override
    public Iterable<Account> getAccountsForDebit(String customerId) {
        return null;
    }

    @Override
    public Iterable<Account> getAccountsForCredit(String customerId) {
        return null;
    }

	@Override
	public Page<Account> getAccounts(String customerId, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
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
