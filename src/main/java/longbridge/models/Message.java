package longbridge.models;


import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.io.IOException;
import java.util.Date;

/**
 * The {@code Message} class is a model that shows a message information
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class Message extends AbstractEntity{


    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private Date sentTime;
    private String status;
    @ManyToOne
    private MailBox mailBox;
    private String tag; //a comma separated list of keywords to identify a message
//    private String location;


    public Message() {
    }


    public Message(String sender, String recipient, String subject, String body, Date Date, String status ) {
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



	@Override
	public OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}

