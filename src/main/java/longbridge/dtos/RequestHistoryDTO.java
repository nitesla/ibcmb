package longbridge.dtos;

import longbridge.models.OperationsUser;

import java.util.Date;
/**
 * Created by Fortune on 4/5/2017.
 */
public class RequestHistoryDTO {

    private Long id;
    private String serviceRequest;
    private String status;
    private String comment;
    private String createdBy;
    private Date createdOn;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(String serviceRequest) {
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

    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "RequestHistoryDTO{" +
                "id=" + id +
                ", serviceRequest='" + serviceRequest + '\'' +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }
}
