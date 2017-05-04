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

//   Message findFirstBySenderOrderByIdDesc(String sender);
//
//    Message findFirstByRecipientOrderByIdDesc(String recipient);


    Iterable<Message> findByMailBox(MailBox mailBox);

    Iterable<Message> findByMailBoxOrderByIdDesc(MailBox mailBox);


    Iterable<Message> findByMailBoxAndDateCreated(MailBox mailBox, Date date);

    Iterable<Message> findByMailBoxAndDateCreatedBetween(MailBox mailBox, Date startDate, Date endDate);

    Iterable<Message> findByDateCreatedBetween(Date startDate, Date endDate);

}
