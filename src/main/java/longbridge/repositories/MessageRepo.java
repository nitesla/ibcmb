package longbridge.repositories;

import longbridge.models.MailBox;
import longbridge.models.Message;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface MessageRepo extends CommonRepo<Message, Long> {

    Iterable<Message> getByMailBox(MailBox mailBox);

    Iterable<Message> getByMailBoxAndSentTime(MailBox mailBox, Date sentTime);

    Iterable<Message> getByMailBoxAndSentTimeBetween(MailBox mailBox, Date startTime, Date endTime);

    Iterable<Message> getBySentTimeBetween(Date startTime, Date endTime);

}
