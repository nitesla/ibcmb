package longbridge.repositories;

import longbridge.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface LoanRepo extends JpaRepository<Loan, Long>{

}
