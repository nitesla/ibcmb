package longbridge.repositories;

import longbridge.models.MailBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface MailBoxRepo extends CommonRepo<MailBox, Long> {
}
