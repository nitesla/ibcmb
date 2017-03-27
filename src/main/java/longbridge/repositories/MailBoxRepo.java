package longbridge.repositories;

import longbridge.models.MailBox;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface MailBoxRepo extends JpaRepository<MailBox, Long> {
}
