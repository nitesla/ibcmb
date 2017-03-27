package longbridge.repositories;

import longbridge.models.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface InvestmentRepo extends JpaRepository<Investment, Long> {
}
