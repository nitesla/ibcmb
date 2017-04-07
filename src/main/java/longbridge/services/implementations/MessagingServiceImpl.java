package longbridge.services.implementations;

import longbridge.models.MailBox;
import longbridge.models.Message;
import longbridge.models.User;
import longbridge.repositories.MessagesRepo;
import longbridge.services.MessagingService;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class MessagingServiceImpl implements MessagingService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private MessagesRepo messageRepo;

    @Autowired
    public MessagingServiceImpl(MessagesRepo messageRepo) {
        this.messageRepo = messageRepo;
    }


    /**
     * Returns a {@code Message} identified by the given {@code id}
     *
     * @param id the message's id
     * @return the {@code Message} object
     */
    @Override
    public Message getMessage(Long id) {

        return this.messageRepo.getOne(id);
    }


    /**
     * Returns a list of messages for the specified user
     *
     * @param mailBox of a user
     * @return a list of messages
     */
    @Override
    public Iterable<Message> getMessages(MailBox mailBox) {
        return this.messageRepo.getByMailBox(mailBox);
    }

    /**
     * Returns a page of messages specified by the {@code firstRecord} and {@code totalNumOfRecords}
     *
     * @param mailBox the user
     * @param firstRecord       the firstRecord
     * @param totalNumOfRecords the total Number of Records
     * @return  a list of messages
     */
    @Override
    public Pageable getMessages(MailBox mailBox, int firstRecord, int totalNumOfRecords) {
        return null;
    }

    /**
     * Returns a list of messages on the given date
     *
     * @param mailBox
     * @param date the date on the messages
     * @return the list of messages
     */
    @Override
    public Iterable<Message> getMessages(MailBox mailBox, LocalDateTime date) {
        return this.messageRepo.getByMailBoxAndSentTime(mailBox, date);
    }

    /**
     * Returns a list of messages within the given date range
     *
     * @param mailBox
     * @param fromDate the start date
     * @param toDate   the end date
     * @return a list of messages
     */
    @Override
    public Iterable<Message> getMessage(MailBox mailBox, LocalDateTime fromDate, LocalDateTime toDate) {
        return this.messageRepo.getByMailBoxAndSentTimeBetween(mailBox, fromDate, toDate);
    }

    /**
     * Returns a list of messages within the given date range
     *
     * @param fromDate the start date
     * @param toDate   the end date
     * @return a list of messages
     */
    public Iterable<Message> getMessage(LocalDateTime fromDate, LocalDateTime toDate) {
        return this.messageRepo.getBySentTimeBetween(fromDate, toDate);
    }

    /**
     * set the status of the message which can be DELIVERED or UNDELIVERED
     *
     * @param id
     * @param status  the type
     * @return the message as read and unread
     */
    @Override
    public void setStatus(Long id, String status) {
        Message mes = this.messageRepo.getOne(id);
        mes.setStatus(status);
        this.messageRepo.save(mes);
    }

    /**
     * Deletes the message identified by the given {@code id}
     *
     * @param id the message id
     */
    @Override
    public void deleteMessage(Long id) {
        Message mes = this.messageRepo.getOne(id);
        mes.setDelFlag("Y");
        this.messageRepo.save(mes);
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
    public void purge(LocalDateTime fromDate, LocalDateTime toDate) {
        Iterable<Message> messages = getMessage(fromDate, toDate);
        this.messageRepo.delete(messages);
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
