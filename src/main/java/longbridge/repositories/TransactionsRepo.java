package longbridge.repositories;

import longbridge.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface TransactionsRepo extends JpaRepository<Transactions, Long> {
}
