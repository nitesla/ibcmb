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
	@NotEmpty
    private String requestName;
    private String requestType;
    private String requestUnit;
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

    public String getRequestUnit() {
        return requestUnit;
    }

    public void setRequestUnit(String requestUnit) {
        this.requestUnit = requestUnit;
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

    @Override
    public String toString() {
        return "ServiceReqConfigDTO{" +
                "id=" + id +
                ", requestName='" + requestName + '\'' +
                ", requestType='" + requestType + '\'' +
                ", requestUnit='" + requestUnit + '\'' +
                ", version=" + version +
                ", formFields=" + formFields +
                '}';
    }
}
