package longbridge.dtos;

import java.util.Date;

/**
 *  Created by Fortune on 4/5/2017.
 */
public class MessageDTO {


    private Long id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private Date dateCreated;
    private String status;
    private String tag; //a comma separated list of keywords to identify a message


    public MessageDTO() {
    }

    public MessageDTO(Long id, String sender, String recipient, String subject, String body, Date dateCreated, String status, String tag) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.dateCreated = dateCreated;
        this.status = status;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    @Override
    public String toString() {
        return "MessageDTO{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", dateCreated=" + dateCreated +
                ", status='" + status + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}

