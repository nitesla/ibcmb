package longbridge.services;

import longbridge.models.EmailDetail;
import longbridge.models.MailBox;
import longbridge.models.Message;
import longbridge.models.User;

import java.awt.print.Pageable;
import java.util.Date;

/**
 * The {@code MessagingService} interface provides a service for sending messages
 * @author Fortunatus Ekenachi
 * @see Message
 * Created on 3/29/2017.
 */
public interface MessageService {

    /**
     *Returns a {@code Message} identified by the given {@code id}
     * @param id the message's id
     * @return the {@code Message} object
     */
    Message getMessage(Long id);


    /**
     * Returns a list of messages for the specified user
     * @param mailbox the user
     * @return a list of messages
     */
    Iterable<Message> getMessages(MailBox mailbox);

    /**
     * Returns a page of messages specified by the {@code firstRecord} and {@code totalNumOfRecords}
     * @param firstRecord the firstRecord
     * @param totalNumOfRecords the total Number of Records
     * @return a list of messages
     */

    Pageable getMessages(MailBox mailBox, int firstRecord, int totalNumOfRecords);


    /**
     * Returns a list of messages on the given date
     * @param date the date on the messages
     * @return the list of messages
     */
   Iterable<Message> getMessages(MailBox mailBox, Date date);

    /**
     *Returns a list of messages within the given date range
     * @param fromDate the start date
     * @param toDate the end date
     * @return a list of messages
     */
    Iterable<Message> getMessage(MailBox mailBox, Date fromDate, Date toDate);

    /**
     *Marks the message as READ or UNREAD
     * @param status the type
     * @return the message as read and unread
     */
    void setStatus(Long id,String status); //Read or Unread

    /**
     * Deletes the message identified by the given {@code id}
     * @param id the message id
     */
    void deleteMessage(Long id);

    /**
     *Purges the messages after the specified days
     * @param daysOld the daysOld
     */
    void purge(int daysOld);

    /**
     *Purges the messages with the given date range
     * @param fromDate the start date
     * @param toDate the end date
     */
    void purge(Date fromDate, Date toDate);

    /**
     * Creates and sends the message from the sender to the recipient
     * @param sender the user that sends the message
     * @param recipient the user the recieves the message
     * @param message  the message
     */
    void sendMessage(User sender, User recipient, Message message);

    /** Makes a request to send an email to using the details
     * in the {@link EmailDetail} object
     * @param email EmailDetail object containing all the details required to send an email
     */
    void sendEmail(EmailDetail email);
}
