package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
@Transactional
public interface AccountRepo extends CommonRepo<Account, Long> {

    Account findById(Long id);

    Account findFirstById(Long id);

    Account findByAccountNumber(String accountNumber);

    Account findByAccountId(String accountId);

    Account findAccountByCustomerId(String customerId);

    List<Account> findByCustomerId(String customerId);

    @Modifying
    @Query("update Account a set a.primaryFlag = 'N' where a.primaryFlag = 'Y' and a.customerId = :customer")
    void unsetPrimaryAccount(@Param("customer") String customerId);

}
