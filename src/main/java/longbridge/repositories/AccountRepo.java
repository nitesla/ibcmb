package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
@Transactional
public interface AccountRepo extends CommonRepo<Account,Long> {

    Account findById(Long id);

    Account findFirstById(Long id);

    Account findFirstByAccountNumber(String accountNumber);

    Account findFirstByAccountId(String accountId);

    Account findFirstAccountByCustomerId(String customerId);

    Page<Account> findAccountByCustomerId(String customerId, Pageable pageable);

    List<Account> findByCustomerId(String customerId);
    List<Account> findByCustomerIdAndCurrencyCodeIgnoreCase(String customerId,String currency);
    List<Account>  findByCustomerIdAndHiddenFlagIgnoreCase(String s1,String s2);

    List<Account> findByCustomerIdAndCurrencyCode(String customerId, String currency);

    @Modifying
    @Query("update Account a set a.primaryFlag = 'N' where a.primaryFlag = 'Y' and a.customerId = :customer")
    void unsetPrimaryAccount(@Param("customer") String customerId);

}
