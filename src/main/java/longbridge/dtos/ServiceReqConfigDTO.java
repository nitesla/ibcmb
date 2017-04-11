package longbridge.dtos;

import longbridge.models.ServiceReqFormField;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Showboy on 08/04/2017.
 */
public class ServiceReqConfigDTO {

	@JsonProperty("DT_RowId")
    private Long id;
    private String requestName;
    private String version;
    private ArrayList<ServiceReqFormField> formFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public ArrayList<ServiceReqFormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(ArrayList<ServiceReqFormField> formFields) {
        this.formFields = formFields;
    }

    @Override
    public String toString() {
        return "ServiceReqConfigDTO{" +
                "id=" + id +
                ", requestName='" + requestName + '\'' +
                ", version='" + version + '\'' +
                ", formFields=" + formFields +
                '}';
    }
}
