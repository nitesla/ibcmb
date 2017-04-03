package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The {@code Message} class is a model that shows
 * a service request
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
@Table(name = "Service_Request_TABLE",/*SCHEMA NAME WILL COME IN LATER*/
        schema = " "

)
public class ServiceRequest extends AbstractEntity {


    private User user;
    private String serviceRequestType;
    private String subject;
    private String body;
    private String recipient;
    private UserGroup userGroup;
    private LocalDateTime requestTime;


    public ServiceRequest() {
    }

    public ServiceRequest(User user, String serviceRequestType, String subject, String body, String recipient, UserGroup userGroup, LocalDateTime date) {

        this.user = user;
        this.serviceRequestType = serviceRequestType;
        this.subject = subject;
        this.body = body;
        this.recipient = recipient;
        this.userGroup = userGroup;
        this.requestTime = date;
    }


    @Override
    public String toString() {
        return "ServiceRequest{" +
                "user=" + user +
                ", serviceRequestType='" + serviceRequestType + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recipient='" + recipient + '\'' +
                ", userGroup=" + userGroup +
                ", requestTime=" + requestTime +
                '}';
    }
}
