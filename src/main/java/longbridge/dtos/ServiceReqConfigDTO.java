package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;

/**
 * Created by Wunmi on 08/04/2017.
 */
public class ServiceReqConfigDTO {

	@JsonProperty("DT_RowId")
    private Long id;
	@NotEmpty(message = "requestName")
    private String requestName;
    private String requestType;
    private boolean authenticate;
    private Long groupId;
    private int version;
    private ArrayList<ServiceReqFormFieldDTO> formFields;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRequestName() {
		return requestName;
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

    public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public ArrayList<ServiceReqFormFieldDTO> getFormFields() {
        return formFields;
    }

    public void setFormFields(ArrayList<ServiceReqFormFieldDTO> formFields) {
        this.formFields = formFields;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    @Override
    public String toString() {
        return "ServiceReqConfigDTO{" +
                "id=" + id +
                ", requestName='" + requestName + '\'' +
                ", requestType='" + requestType + '\'' +
                ", groupId='" + groupId + '\'' +
                ", version=" + version +
                ", formFields=" + formFields +
                '}';
    }
}
