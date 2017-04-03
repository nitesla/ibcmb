package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class RequestHistory extends AbstractEntity{

    @ManyToOne
    private ServiceRequest serviceRequest;
    private String status;
    private String comment;
    private Long createdBy;
    private LocalDateTime date;

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RequestHistory{" +
                "serviceRequest=" + serviceRequest +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", createdBy=" + createdBy +
                ", date=" + date +
                '}';
    }
}
