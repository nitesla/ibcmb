package longbridge.services;

import longbridge.models.Account;
import longbridge.models.Customer;
import longbridge.models.FinancialTransaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface AccountService {
    Account getAccount(Long accId);

    Iterable<Account> getAccounts(Customer customer);

    Map<String,BigDecimal> getBalance(Account account);

    FinancialTransaction getAccountStatements(Account account, Date fromDate, Date toDate);
}
