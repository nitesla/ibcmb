package longbridge.repositories;

import longbridge.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */

@Repository
public interface LoanRepo extends JpaRepository<Loan, Long>{

}
