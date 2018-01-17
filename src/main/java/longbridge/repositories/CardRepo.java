package longbridge.repositories;

import longbridge.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface CardRepo extends CommonRepo<Card, Long> {
}
