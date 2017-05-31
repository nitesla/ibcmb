package longbridge.services;

import longbridge.dtos.MessageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

//import java.awt.print.Pageable;

/**
 * The {@code MessagingService} interface provides a service for sending messages
 * @author Fortunatus Ekenachi
 * @see Message
 * @see MailBox
 * Created on 3/29/2017.
 */
public interface MessageService {

//    @PreAuthorize("hasAuthority('SEND_EMAIL')")
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
     * @param message the message object
     */
    String addMessage(User sender, MessageDTO message) throws InternetBankingException;

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

    

    /**
     * Returns a list of messages on the given date
     * @param date the date on the messages
     * @return a list of messages
     */
   Iterable<Message> getMessages(User user, Date date);




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
     * Returns a page list of maessages recieved
     * @param pageable the pagination
     * @return returns a list of messages
     */
     Page<MessageDTO> getReceivedMessages(String recipient, UserType recipientTye, Pageable pageable);

    Page<MessageDTO> getReceivedMessages(String recipient, UserType recipientTye, java.awt.print.Pageable pageable);

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
    String deleteSentMessage(User user, Long id) throws InternetBankingException;

    int getNumOfUnreadMessages(User user);

    /**
     * Deletes the received message identified by the given {@code id}
     * @param id the message id
     */
    String deleteReceivedMessage(Long id) throws InternetBankingException;

    /**
     *Purges the messages after the specified days
     * @param daysOld the daysOld
     */
    String purge(int daysOld) throws InternetBankingException;

    /**
     *Purges the messages with the given date range
     * @param fromDate the start date
     * @param toDate the end date
     */
    String purge(Date fromDate, Date toDate) throws InternetBankingException;

    /**
     * Creates and sends the message from the sender to the recipient
     * @param sender the user that sends the message
     * @param recipient the user the receives the message
     * @param message  the message
     */
    String sendMessage(User sender, User recipient, MessageDTO message) throws InternetBankingException;

    /** Makes a request to send an email to using the details
     * in the {@link Email} object
     * @param messageDTO MessageDTO object containing all the details required to send an email
     */
    void sendEmail(MessageDTO messageDTO) throws InternetBankingException;

    Page<Message> getMessages(User user, java.awt.print.Pageable pageDetails);

    Page<Message> getMessages(User user, Date date, java.awt.print.Pageable pageDetails);

    Page<Message> getMessages(User user, Date fromDate, Date toDate, java.awt.print.Pageable pageDetails);
}
