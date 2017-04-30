package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
public interface AccountRepo extends CommonRepo<Account, Long> {

    Account findById(Long id);

    Account findFirstById(Long id);

    Account findByAccountNumber(String accountNumber);

    Account findByAccountId(String accountId);

    List<Account> findByCustomerId(String customerId);

}
