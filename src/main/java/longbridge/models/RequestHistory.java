package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class RequestHistory extends AbstractEntity{

    @ManyToOne
    private ServiceRequest serviceRequest;
    private String status;
    private String comment;
    @ManyToOne
    private OperationsUser createdBy;
    private Date createdOn;

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OperationsUser getCreatedBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "RequestHistory{" +
                "serviceRequest=" + serviceRequest +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", createdBy=" + createdBy +
                ", createdOn=" + createdOn +
                '}';
    }

    public void setCreatedBy(OperationsUser createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

}
