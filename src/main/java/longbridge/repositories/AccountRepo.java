package longbridge.repositories;

import longbridge.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface AccountRepo extends JpaRepository<Account, Long> {

}
