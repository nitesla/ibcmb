package longbridge.dtos;

import longbridge.models.OperationsUser;
import org.joda.time.LocalDateTime;

/**
 * Created by Fortune on 4/5/2017.
 */
public class RequestHistoryDTO {


    private ServiceRequestDTO serviceRequestDTO;
    private String status;
    private String comment;
    private OperationsUser createdBy;
    private LocalDateTime createdOn;

    public ServiceRequestDTO getServiceRequestDTO() {
        return serviceRequestDTO;
    }

    public void setServiceRequestDTO(ServiceRequestDTO serviceRequestDTO) {
        this.serviceRequestDTO = serviceRequestDTO;
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
                "serviceRequest=" + serviceRequestDTO +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", createdBy=" + createdBy +
                ", createdOn=" + createdOn +
                '}';
    }

    public void setCreatedBy(OperationsUser createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

}
