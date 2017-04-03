package longbridge.services.implementations;

import longbridge.models.Message;
import longbridge.models.User;
import longbridge.services.MessagingService;

import java.awt.print.Pageable;
import java.util.Date;

/**
 * Created by Wunmi on 29/03/2017.
 */
public class MessagingServiceImpl implements MessagingService{


    /**
     * Returns a {@code Message} identified by the given {@code id}
     *
     * @param id the message's id
     * @return the {@code Message} object
     */
    @Override
    public Message getMessage(Long id) {
        return null;
    }

    /**
     * Returns a list of messages for the specified user
     *
     * @param user the user
     * @return a list of messages
     */
    @Override
    public Iterable<Message> getMessages(User user) {
        return null;
    }

    /**
     * Returns a page of messages specified by the {@code firstRecord} and {@code totalNumOfRecords}
     *
     * @param user the user
     * @param firstRecord       the firstRecord
     * @param totalNumOfRecords the total Number of Records
     * @return  a list of messages
     */
    @Override
    public Pageable getMessages(User user, int firstRecord, int totalNumOfRecords) {
        return null;
    }

    /**
     * Returns a list of messages on the given date
     *
     * @param user
     * @param date the date on the messages
     * @return the list of messages
     */
    @Override
    public Iterable<Message> getMessages(User user, Date date) {
        return null;
    }

    /**
     * Returns a list of messages within the given date range
     *
     * @param user
     * @param fromDate the start date
     * @param toDate   the end date
     * @return a list of messages
     */
    @Override
    public Iterable<Message> getMessage(User user, Date fromDate, Date toDate) {
        return null;
    }

    /**
     * Marks the message as READ or UNREAD
     *
     * @param message
     * @param asType  the type
     * @return the message as read and unread
     */
    @Override
    public void markMessage(Message message, String asType) {

    }

    /**
     * set the status of the message which can be DELIVERED or UNDELIVERED
     *
     * @param message
     * @param status  the status of the message
     */
    @Override
    public void setStatus(Message message, String status) {

    }

    /**
     * Deletes the message identified by the given {@code id}
     *
     * @param id the message id
     */
    @Override
    public void deleteMessage(Long id) {

    }

    /**
     * Purges the messages after the specified days
     *
     * @param daysOld the daysOld
     */
    @Override
    public void purge(int daysOld) {

    }

    /**
     * Purges the messages with the given date range
     *
     * @param fromDate the start date
     * @param toDate   the end date
     */
    @Override
    public void purge(Date fromDate, Date toDate) {

    }

    /**
     * Creates and sends the message from the sender to the recipient
     *
     * @param sender    the user that sends the message
     * @param recipient the user the recieves the message
     * @param message   the message
     */
    @Override
    public void sendMessage(User sender, User recipient, Message message) {

    }
}
