package longbridge.servicerequests.client;

import longbridge.models.AbstractEntity;
import longbridge.models.UserType;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import java.util.Date;
import java.util.List;


@Entity
@Audited(withModifiedFlag = true)
@Where(clause = "del_Flag='N'")
public class ServiceRequest extends AbstractEntity {

    @ElementCollection(fetch = FetchType.EAGER)
    List<Comment> comments;
    private long entityId;
    private String requestName;
    private UserType userType;
    @Lob
    private String body;
    private String currentStatus;
    private Date dateRequested;
    private Long serviceReqConfigId;

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Long getServiceReqConfigId() {
        return serviceReqConfigId;
    }

    public void setServiceReqConfigId(Long serviceReqConfigId) {
        this.serviceReqConfigId = serviceReqConfigId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
