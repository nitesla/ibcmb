package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    Account findById(Long id);
    Account findByAccountNumber(String acctNumber);

    Account findByAccountId(String accountId);

    List<Account> findByCustomerId(String customerId);

}
