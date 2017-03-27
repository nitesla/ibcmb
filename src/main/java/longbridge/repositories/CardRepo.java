package longbridge.repositories;

import longbridge.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface CardRepo extends JpaRepository<Card, Long> {
}
