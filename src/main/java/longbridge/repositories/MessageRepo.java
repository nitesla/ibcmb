package longbridge.repositories;

import longbridge.models.MailBox;
import longbridge.models.Message;
import longbridge.models.UserType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface MessageRepo extends CommonRepo<Message, Long> {

//   Message findFirstBySenderOrderByIdDesc(String sender);
//
//    Message findFirstByRecipientOrderByIdDesc(String recipient);

    List<Message> findByRecipientAndRecipientTypeOrderByIdDesc(String recipient, UserType recipientTye);

    Iterable<Message> findByMailBox(MailBox mailBox);

    Iterable<Message> findByMailBoxOrderByIdDesc(MailBox mailBox);


    Iterable<Message> findByMailBoxAndDateCreated(MailBox mailBox, Date date);

    Iterable<Message> findByMailBoxAndDateCreatedBetween(MailBox mailBox, Date startDate, Date endDate);

    Iterable<Message> findByDateCreatedBetween(Date startDate, Date endDate);

}
