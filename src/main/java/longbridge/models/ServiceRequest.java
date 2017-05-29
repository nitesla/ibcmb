package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;


/**
 * The {@code ServiceRequest} class is a model that shows
 * a service request
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class ServiceRequest extends AbstractEntity {

    @ManyToOne
    private RetailUser user;
    @ManyToOne
    private Corporate corporate;

    private String requestName;
//    @Column(columnDefinition = "TEXT")
    private String body;
    private String requestStatus;
    private Date dateRequested;
    private Long serviceReqConfigId;

    @OneToMany(mappedBy = "serviceRequest")
    private List<RequestHistory> requestHistories;

    public RetailUser getUser() {
        return user;
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

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public List<RequestHistory> getRequestHistories() {
        return requestHistories;
    }

    public void setRequestHistories(List<RequestHistory> requestHistories) {
        this.requestHistories = requestHistories;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    public Long getServiceReqConfigId() {
        return serviceReqConfigId;
    }

    public void setServiceReqConfigId(Long serviceReqConfigId) {
        this.serviceReqConfigId = serviceReqConfigId;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "user=" + user +
                ", corporate=" + corporate +
                ", requestName='" + requestName + '\'' +
                ", body='" + body + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", dateRequested=" + dateRequested +
                ", serviceReqConfigId=" + serviceReqConfigId +
                ", requestHistories=" + requestHistories +
                '}';
    }

    public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
