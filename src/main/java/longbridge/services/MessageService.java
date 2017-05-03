package longbridge.services;

import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.MessageDTO;
import longbridge.models.*;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

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
     * @param userId the user's id
     * @param userType the type of user
     * @return the mailbox
     */
    MailBox getMailBox(Long userId, UserType userType);

    /**
     *Returns a {@code Message} identified by the given {@code id}
     * @param id the message's id
     * @return the {@code Message} object
     */
    MessageDTO getMessage(Long id);


    void addMessage(MailBox mailBox, MessageDTO messageDTO);

    /**
     * Returns a list of messages on the specified mailbox
     * @param mailbox the mailbox
     * @return a list of messages
     */
    Iterable<MessageDTO> getMessages(MailBox mailbox);

    /**
     * Returns a page list of messages on the specified mailboz
     * @param mailbox the mailbox
     * @param pageDetails the page details
     * @return a list of messages
     */
    Page<Message> getMessages(MailBox mailbox, Pageable pageDetails);
    

    /**
     * Returns a list of messages on the given date
     * @param date the date on the messages
     * @return a list of messages
     */
   Iterable<Message> getMessages(MailBox mailBox, Date date);

    /**
     * Returns a page list of messages identified by the mailbox on the given date
     * @param mailbox the mailbox
     * @param date the date to look for
     * @param pageDetails the page details for pagination
     * @return a list of messages
     */
   Page<Message> getMessages(MailBox mailbox, Date date, Pageable pageDetails);


//    MessageDTO getLastSentMessage(String sender);
//
//    MessageDTO getLastReceivedMessage(String recipient);


    /**
     *Returns a list of messages within the given date range
     * @param fromDate the start date
     * @param toDate the end date
     * @return a list of messages
     *
     */
    Iterable<Message> getMessage(MailBox mailBox, Date fromDate, Date toDate);

    /**
     * Returns a list of sent messages from the given mailbox
     * @param mailBox the mailbox
     * @return a list of messages
     */
    List<MessageDTO> getSentMessages(MailBox mailBox);

    /**
     * Returns a list of received messages from the given mailbox
     * @param mailBox the mailbox
     * @return a  list of  messages
     */
    List<MessageDTO> getReceivedMessages(MailBox mailBox);

    /**
     * Returns a page list of messages within the given date range
     * @param mailbox the mailbox
     * @param fromDate the start date
     * @param toDate the end date
     * @param pageDetails the page details for pagination
     * @return a list of messages
     */
    Page<Message> getMessages(MailBox mailbox, Date fromDate, Date toDate, Pageable pageDetails);

    /**
     *Sets the status of the message
     * @param id the id of the message
     * @param status the status (SENT,SAVED)
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
