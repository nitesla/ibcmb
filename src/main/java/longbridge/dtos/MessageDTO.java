package longbridge.dtos;

import longbridge.models.UserType;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 *  Created by Fortune on 4/5/2017.
 */
public class MessageDTO {


    private Long id;
    private int version;
    @NotEmpty(message = "sender")
    private String sender;
    @NotEmpty(message = "recipient")
    private String recipient;
    private UserType recipientType;
    @NotEmpty(message = "subject")
    private String subject;
    @NotEmpty(message = "body")
    private String body;
    private Date dateCreated;
    private String status;
    private String tag; //a comma separated list of keywords to identify a message


    public MessageDTO() {
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getSubject() {
        return subject;
    }

    public UserType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(UserType recipientType) {
        this.recipientType = recipientType;
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
                ", createdOnDate=" + dateCreated +
                ", status='" + status + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}

