package longbridge.repositories;

import longbridge.models.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface AccountTypeRepo extends JpaRepository<AccountType, Long> {
}
