package longbridge.services.implementations;

import longbridge.models.Account;
import longbridge.models.Transfer;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Showboy on 04/04/2017.
 */
@Service
public class IntegrationServiceImpl implements IntegrationService {
    @Override
    public Iterable<Account> fetchAccounts(String cifid) {
        return null;
    }

    @Override
    public AccountStatement getAccountStatements(String accountId, LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> getBalance(String accountId) {
        return null;
    }

    @Override
    public void makeTransfer(Transfer transfer) {

    }
}
