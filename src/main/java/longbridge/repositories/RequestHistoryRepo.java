package longbridge.repositories;

import longbridge.models.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface RequestHistoryRepo extends JpaRepository<RequestHistory, Long> {
}
