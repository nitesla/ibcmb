package longbridge.repositories;

import longbridge.models.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by chigozirim on 3/31/17.
 */
public interface TransferRepo extends JpaRepository<TransferRequest, Long> {
    TransferRequest findById(long id);

    Iterable<TransferRequest> getTransactions(long userId);
}
