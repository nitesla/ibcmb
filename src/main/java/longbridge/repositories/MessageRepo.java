package longbridge.repositories;

import longbridge.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sun.misc.resources.Messages;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface MessageRepo extends CommonRepo<Message, Long> {

}
