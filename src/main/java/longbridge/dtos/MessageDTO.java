package longbridge.dtos;

import java.util.Date;

/**
 *  Created by Fortune on 4/5/2017.
 */
public class MessageDTO {


    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private Date sentTime;
    private String status;
    private String tag; //a comma separated list of keywords to identify a message


    public MessageDTO() {
    }


    public MessageDTO(String sender, String recipient, String subject, String body, Date Date, String status ) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sentTime = Date;
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
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

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
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
                '}';
    }
}

