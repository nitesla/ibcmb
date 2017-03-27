package longbridge.repositories;

import longbridge.models.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface TransactionTypeRepo extends JpaRepository<TransactionType, Long>{
}
