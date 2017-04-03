package longbridge.models;

import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * The {@code Message} class is a model that shows a message information
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
@Table(name = "MESSAGE_TABLE",/*SCHEMA NAME WILL COME IN LATER*/
        schema = " "

)
public class Message extends AbstractEntity{


    private User sender;
    private User recipient;
    private Long userId;
    private String recepient;
    private String subject;
    private String body;
    private DateTime dateTime;
    private String status;
    private String location;


    public Message() {
    }

    public Message(User sender, User recipient, String subject, String body, DateTime dateTime, String status, String location) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.dateTime = dateTime;
        this.status = status;
        this.location = location;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", dateTime=" + dateTime +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
