package longbridge.repositories;

import longbridge.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by chigozirim on 3/31/17.
 */
public interface TransferRepo extends JpaRepository<Transfer, Long> {
    Transfer findById(long id);

    Iterable<Transfer> getTransactions(long userId);
}
