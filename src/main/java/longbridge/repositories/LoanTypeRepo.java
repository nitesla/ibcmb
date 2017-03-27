package longbridge.repositories;

import longbridge.models.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface LoanTypeRepo extends JpaRepository<LoanType, Long> {
}
