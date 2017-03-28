package longbridge.repositories;

import longbridge.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */

@Repository
public interface CardRepo extends JpaRepository<Card, Long> {
}
