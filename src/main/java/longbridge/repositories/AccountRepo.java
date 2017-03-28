package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    Account findByAccountNumber(String acctNumber);

}
