package longbridge.dtos;

import longbridge.models.OperationsUser;

import java.util.Date;
/**
 * Created by Fortune on 4/5/2017.
 */
public class RequestHistoryDTO {

    private Long id;
    private String serviceRequestId;
    private String status;
    private String comments;
    private String createdBy;
    private String createdOn;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }


    @Override
    public String toString() {
        return "RequestHistoryDTO{" +
                "id=" + id +
                ", serviceRequestId='" + serviceRequestId + '\'' +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }
}
