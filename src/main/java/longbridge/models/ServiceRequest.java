package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;


/**
 * The {@code Message} class is a model that shows
 * a service request
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
public class ServiceRequest extends AbstractEntity {

    @ManyToOne
    private RetailUser user;

    private String serviceRequestType;
    private String subject;
    private String body;
    private String recepient;
    private String status;
    //private UserGroup userGroup;
    private LocalDateTime requestTime;

    public RetailUser getUser() {
        return user;
    }

    public String getServiceRequestType() {
        return serviceRequestType;
    }

    public void setServiceRequestType(String serviceRequestType) {
        this.serviceRequestType = serviceRequestType;
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

    public String getRecepient() {
        return recepient;
    }

    public void setRecepient(String recepient) {
        this.recepient = recepient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public Collection<RequestHistory> getRequestHistories() {
        return requestHistories;
    }

    public void setRequestHistories(Collection<RequestHistory> requestHistories) {
        this.requestHistories = requestHistories;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    @OneToMany
    private Collection<RequestHistory> requestHistories;


    public ServiceRequest() {
    }

    public ServiceRequest(RetailUser user, String serviceRequestType, String subject, String body, String recepient, UserGroup userGroup, LocalDateTime date) {

        this.user = user;
        this.serviceRequestType = serviceRequestType;
        this.subject = subject;
        this.body = body;
        this.recepient = recepient;
        //this.userGroup = userGroup;
        this.requestTime = date;
    }


    @Override
    public String toString() {
        return "ServiceRequest{" +
                "user=" + user +
                ", serviceRequestType='" + serviceRequestType + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recepient='" + recepient + '\'' +
                //", userGroup=" + userGroup +
                ", requestTime=" + requestTime +
                '}';
    }
}
