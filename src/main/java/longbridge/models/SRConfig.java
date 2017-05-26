package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class SRConfig extends AbstractEntity{

    private String requestName;
    private String requestType;
    private boolean authenticate;
    private Long groupId;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<ServiceReqFormField> formFields;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Collection<ServiceReqFormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(Collection<ServiceReqFormField> formFields) {
        this.formFields = formFields;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
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
