package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.Entity;

/**
 * The {@code Message} class is a model that shows a message information
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
//@Table(name = "MESSAGE_TABLE",/*SCHEMA NAME WILL COME IN LATER*/
    //    schema = " "

//)
public class Message extends AbstractEntity{


    private User sender;
    private String recipient;
    private String subject;
    private String body;
    private LocalDateTime sentTime;
    private String status;
    private String location;


    public Message() {
    }


    public Message(User sender, String recipient, String subject, String body, LocalDateTime dateTime, String status, String location) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sentTime = dateTime;
        this.status = status;
        this.location = location;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
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

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", sentTime=" + sentTime +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

