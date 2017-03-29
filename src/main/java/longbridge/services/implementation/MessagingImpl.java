package longbridge.services.implementation;

import longbridge.models.Message;
import longbridge.services.MessagingService;

import java.awt.print.Pageable;
import java.util.Date;

/**
 * Created by Showboy on 29/03/2017.
 */
public class MessagingImpl implements MessagingService{
    @Override
    public Iterable<Message> getMessages() {
        return null;
    }

    @Override
    public Pageable getMessages(int firstRecord, int totalNumOfRecords) {
        return null;
    }

    @Override
    public Message getMessage(Long id) {
        return null;
    }

    @Override
    public Message getMessage(Date date) {
        return null;
    }

    @Override
    public Iterable<Message> getMessage(Date fromDate, Date toDate) {
        return null;
    }

    @Override
    public void markMessage(String asType) {

    }

    @Override
    public void setStatus() {

    }

    @Override
    public void deleteMessage() {

    }

    @Override
    public void purge() {

    }

    @Override
    public void purge(int daysOld) {

    }

    @Override
    public void purge(Date fromDate, Date toDate) {

    }

    @Override
    public void sendMessage() {

    }
}
