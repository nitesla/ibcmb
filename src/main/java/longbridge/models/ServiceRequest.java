package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Date;


/**
 * The {@code Message} class is a model that shows
 * a service request
 * @author ayoade_farooq@yahoo.com
 * Created on 3/28/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class ServiceRequest extends AbstractEntity {

    @ManyToOne
    private RetailUser user;

    private String requestName;
    @Column(columnDefinition = "TEXT")
    private String body;
    private Date requestTime;

    @OneToMany
    private Collection<RequestHistory> requestHistories;

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

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
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

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "user=" + user +
                ", requestName='" + requestName + '\'' +
                ", body='" + body + '\'' +
                ", requestTime=" + requestTime +
                ", requestHistories=" + requestHistories +
                '}';
    }

	@Override
	public OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
