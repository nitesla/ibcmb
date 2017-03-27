package longbridge.repositories;

import longbridge.models.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface CardTypeRepo extends JpaRepository<CardType, Long> {

}
