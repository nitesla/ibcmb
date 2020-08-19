package longbridge.repositories;

import longbridge.models.MailBox;
import longbridge.models.Message;
import longbridge.models.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Message> findByRecipientIgnoreCaseAndRecipientTypeAndTagOrderByIdDesc(String recipient, UserType recipientTye,String tag);

    List<Message> findByRecipientIgnoreCaseAndRecipientTypeAndTagOrderByDateCreatedDesc(String recipient, UserType recipientTye,String tag);


    Page<Message> findByRecipientIgnoreCaseAndRecipientTypeOrderByIdDesc(String recipient, UserType recipientTye, Pageable pageable);


    Iterable<Message> findByMailBox(MailBox mailBox);

    Iterable<Message> findByMailBoxOrderByIdDesc(MailBox mailBox);


    Iterable<Message> findByMailBoxAndDateCreated(MailBox mailBox, Date date);

    Iterable<Message> findByMailBoxAndDateCreatedBetween(MailBox mailBox, Date startDate, Date endDate);

    Iterable<Message> findByDateCreatedBetween(Date startDate, Date endDate);

    @Query("select r from Message r where r.mailBox.userId =:userId and r.mailBox.userType =:userType and r.tag = :tag order by r.dateCreated")
    List<Message> findMessageByUserAndTagOrderByDateCreatedDesc(@Param("userId") Long userId,@Param("userType") UserType userType, @Param("tag") String category);

    @Query("select r from Message r where r.mailBox.userId =:userId and r.mailBox.userType =:userType and r.tag = :tag order by r.dateCreated desc")
    Page<Message> findPagedMessageByUserAndTag(@Param("userId") Long userId,@Param("userType") UserType userType, @Param("tag") String category, Pageable pageable);

    @Query("select count(r.id) from Message r where r.mailBox.userId =:userId and r.mailBox.userType =:userType and r.tag = :tag")
    Long countMessageByUserAndTag(@Param("userId") Long userId,@Param("userType") UserType userType, @Param("tag") String category);


}
