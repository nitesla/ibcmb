package longbridge.repositories;

import longbridge.models.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */
@Repository
public interface InvestmentRepo extends JpaRepository<Investment, Long> {
}
