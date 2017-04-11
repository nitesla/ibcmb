package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Wunmi on 08/04/2017.
 */
public class ServiceReqConfigDTO {

	@JsonProperty("DT_RowId")
    private Long id;
    private String requestName;
    private String version;
    private ArrayList<ServiceReqFormFieldDTO> formFields;


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
                ", version='" + version + '\'' +
                ", formFields=" + formFields +
                '}';
    }
}
