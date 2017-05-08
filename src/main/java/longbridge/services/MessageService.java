package longbridge.services;

import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.MessageDTO;
import longbridge.models.*;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import javax.jws.soap.SOAPBinding;

/**
 * The {@code MessagingService} interface provides a service for sending messages
 * @author Fortunatus Ekenachi
 * @see Message
 * @see MailBox
 * Created on 3/29/2017.
 */
public interface MessageService {

    Iterable<MessageDTO> getMessages();

    /**
     * Returns a mailbox of the given id
     * @param id the id
     * @return the mailbox
         */
    MailBox getMailBox(Long id);

    /**
     * Returns the mailbox of the user identified by the userId and userType
     * @param user the user
     * @return the mailbox
     */
    MailBox getMailBox(User user);

    /**
     *Returns a {@code Message} identified by the given {@code id}
     * @param id the message's id
     * @return the {@code Message} object
     */
    MessageDTO getMessage(Long id);


    /**
     * Adds the message created by the specified user
     * @param sender the sender of the message
     * @param recipient the recipient of the message
     * @param message the message object
     */
    void addMessage(User sender, User recipient, MessageDTO message);

    /**
     * Returns a list of messages of the specified user
     * @param user the user
     * @return a list of messages
     */
    Iterable<MessageDTO> getMessages(User user);

    /**
     * Returns a page list of messages on the specified mailboz
     * @param user the user
     * @param pageDetails the page details
     * @return a list of messages
     */
    Page<Message> getMessages(User user, Pageable pageDetails);
    

    /**
     * Returns a list of messages on the given date
     * @param date the date on the messages
     * @return a list of messages
     */
   Iterable<Message> getMessages(User user, Date date);

    /**
     * Returns a page list of messages identified by the mailbox on the given date
     * @param user the user
     * @param date the date to look for
     * @param pageDetails the page details for pagination
     * @return a list of messages
     */
   Page<Message> getMessages(User user, Date date, Pageable pageDetails);


//    MessageDTO getLastSentMessage(String sender);
//
//    MessageDTO getLastReceivedMessage(String recipient);


    /**
     *Returns a list of messages of the specified user within the given date range
     * @param fromDate the start date
     * @param toDate the end date
     * @return a list of messages
     *
     */
    Iterable<Message> getMessages(User user, Date fromDate, Date toDate);

    /**
     * Returns a list of messages sent by the specified user
     * @param user the user
     * @return a list of messages
     */
    List<MessageDTO> getSentMessages(User user);

    /**
     * Returns a list of messages received by the specified user
     * @param user the user
     * @return a list of  messages
     */
    List<MessageDTO> getReceivedMessages(User user);

    /**
     * Returns a page list of messages of the specified user within the given date range
     * @param user the user
     * @param fromDate the start date
     * @param toDate the end date
     * @param pageDetails the page details for pagination
     * @return a list of messages
     */
    Page<Message> getMessages(User user, Date fromDate, Date toDate, Pageable pageDetails);

    /**
     *Sets the status of the message as Read or Unread
     * @param id the id of the message
     * @param status the status (READ OR UNREAD)
     * @return the message as read and unread
     */
    void setStatus(Long id,String status); //Read or Unread

    /**
     * Deletes the sent message identified by the given {@code id}
     * @param id the message id
     */
    void deleteSentMessage(User user, Long id);


    /**
     * Deletes the received message identified by the given {@code id}
     * @param id the message id
     */
    void deleteReceivedMessage(Long id);

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
     * @param recipient the user the receives the message
     * @param message  the message
     */
    void sendMessage(User sender, User recipient, MessageDTO message);

    /** Makes a request to send an email to using the details
     * in the {@link EmailDetail} object
     * @param email EmailDetail object containing all the details required to send an email
     */
    void sendEmail(EmailDetail email);
}
