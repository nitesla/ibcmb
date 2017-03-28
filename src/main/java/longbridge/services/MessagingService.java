package longbridge.services;

import longbridge.models.Message;

import java.awt.print.Pageable;
import java.util.Date;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface MessagingService {
    Iterable<Message> getMessages();

    Pageable getMessages(int firstRecord, int totalNumOfRecords);

    Message getMessage(Long id);

    Message getMessage(Date date);

    Iterable<Message> getMessage(Date fromDate, Date toDate);

    void markMessage(String asType); //Read or Unread

    void  setStatus();

    void deleteMessage();

    void purge();

    void purge(int daysOld);

    void purge(Date fromDate, Date toDate);

    void sendMessage();
}
