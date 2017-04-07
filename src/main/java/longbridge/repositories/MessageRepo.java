package longbridge.repositories;

import longbridge.models.MailBox;
import longbridge.models.Message;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface MessageRepo extends CommonRepo<Message, Long> {

    Iterable<Message> getByMailBox(MailBox mailBox);

    Iterable<Message> getByMailBoxAndSentTime(MailBox mailBox, LocalDateTime sentTime);

    Iterable<Message> getByMailBoxAndSentTimeBetween(MailBox mailBox, LocalDateTime startTime, LocalDateTime endTime);

    Iterable<Message> getBySentTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

}
