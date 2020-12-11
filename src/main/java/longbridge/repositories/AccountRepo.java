package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface AccountRepo extends CommonRepo<Account,Long> {

//    Account findById(Long id);

    Account findFirstById(Long id);

    Account findFirstByAccountNumber(String accountNumber);

    Account findByAccountNumber(String accountNumber);

    Account findFirstByAccountId(String accountId);

    Account findFirstAccountByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);

    boolean existsByAccountNumber(String accountNumber);

    Page<Account> findAccountByCustomerId(String customerId, Pageable pageable);

    List<Account> findByCustomerId(String customerId);
    List<Account> findByCustomerIdAndSchemeTypeIn(String customerId ,List<String> schmTypes);
    List<Account> findByCustomerIdAndSchemeType(String customerId ,String schmTypes);
    List<Account> findByCustomerIdAndSchemeTypeInIgnoreCase(String customerId ,List<String> schmTypes);
    List<Account> findByCustomerIdAndCurrencyCodeIgnoreCase(String customerId,String currency);
    List<Account>  findByCustomerIdAndHiddenFlagIgnoreCase(String s1,String s2);

    List<Account> findByCustomerIdAndCurrencyCode(String customerId, String currency);

    @Modifying
    @Query("update Account a set a.primaryFlag = 'N' where a.primaryFlag = 'Y' and a.customerId = :customer")
    void unsetPrimaryAccount(@Param("customer") String customerId);
//    @Query("select r from Account r inner join r.account  where r.customerId = :cifId")
//    Page<Account> findEnityByRevisions(Pageable pageable, @Param("class") String cifId);
   
//    @Query("select count(a) > 0 from Account a inner join corporate c where c.corporate_id=:corp and a.id=:acct")
//    boolean accountInCorp(@Param("corp") Corporate corporate, @Param("acct") Account account);
//
    @Query("select a from Account a where a.accountNumber in :accountNumbers")
    Page<Account> getLoanAccounts(@Param("accountNumbers")List<String> accountNumbers,Pageable pageable);

    @Query("select a from Account a where a.accountNumber in :accountNumbers")
    List<Account> getLoanAccounts(@Param("accountNumbers")List<String> accountNumbers);
    @Query("select a from Account a where a.accountNumber in :accountNumbers")
    Page<Account> getFixedDepositAccounts(@Param("accountNumbers")List<String> accountNumbers,Pageable pageable);

    @Query("select a from Account a where a.accountNumber in :accountNumbers")
    List<Account> getFixedDepositAccounts(@Param("accountNumbers")List<String> accountNumbers);

}
