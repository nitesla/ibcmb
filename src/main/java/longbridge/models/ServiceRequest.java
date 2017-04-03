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
    //private UserGroup userGroup;
    private LocalDateTime requestTime;

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
