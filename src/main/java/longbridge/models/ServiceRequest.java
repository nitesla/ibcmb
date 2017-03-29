package longbridge.models;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.*;


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
public class ServiceRequest extends AbstractEntity{


    private Long senderId;
    private String serviceRequestType;
    private String subject;
    private String body;
    private String recepient;
    private Set<String> groupRecepientId;
    private DateTime date;


    public ServiceRequest() {
    }

    public ServiceRequest(Long senderId, String serviceRequestType, String subject, String body, String recepient, Set<String> groupRecepientId, DateTime date) {
        this.senderId = senderId;
        this.serviceRequestType = serviceRequestType;
        this.subject = subject;
        this.body = body;
        this.recepient = recepient;
        this.groupRecepientId = groupRecepientId;
        this.date = date;
    }


    @Override
    public String toString() {
        return "{\"ServiceRequest\":"
                + super.toString()
                + ",                         \"senderId\":\"" + senderId + "\""
                + ",                         \"serviceRequestType\":\"" + serviceRequestType + "\""
                + ",                         \"subject\":\"" + subject + "\""
                + ",                         \"body\":\"" + body + "\""
                + ",                         \"recepient\":\"" + recepient + "\""
                + ",                         \"groupRecepientId\":" + groupRecepientId
                + ",                         \"date\":" + date
                + "}";
    }
}
