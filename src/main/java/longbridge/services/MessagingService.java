package longbridge.services;

import longbridge.models.Message;

import java.awt.print.Pageable;
import java.util.Date;

/**
 * The {@code MessagingService} interface provides a service for sending messages
 * Created on 3/29/2017.
 */
public interface MessagingService {

    Iterable<Message> getMessages();

    /**
     * The getMessages get
     * @param firstRecord the firstRecord
     * @param totalNumOfRecords the total Number of Records
     * @return this will return a list of messages
     */

    Pageable getMessages(int firstRecord, int totalNumOfRecords);

    Message getMessage(Long id);

    Message getMessage(Date date);

    /**
     *
     * @param fromDate the start date
     * @param toDate the end date
     * @return the start date and end date
     */
    Iterable<Message> getMessage(Date fromDate, Date toDate);

    /**
     *
     * @param asType the type
     * @return the message as read and unread
     */
    void markMessage(String asType); //Read or Unread

    /**
     * set the status of the message which can be read and unread
     * @param status the status
     */
    void  setStatus(String status);

    void setStatus();

    /**
     * logical delete of message
     */
    void deleteMessage();

    /**
     * purge method clears message after a specific period
     */
    void purge();

    /**
     *
     * @param daysOld the daysOld
     */
    void purge(int daysOld);

    /**
     *
     * @param fromDate the start date
     * @param toDate the end date
     */
    void purge(Date fromDate, Date toDate);

    /**
     * create message
     */
    void sendMessage();
}
