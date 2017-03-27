package longbridge.repositories;

import longbridge.models.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface MessagesRepo extends JpaRepository<Messages, Long> {
}
