package longbridge.dtos;

import longbridge.models.ServiceReqFormFields;

import java.util.ArrayList;

/**
 * Created by Showboy on 08/04/2017.
 */
public class ServiceReqConfigDTO {

    private Long id;
    private String requestName;
    private ArrayList<ServiceReqFormFields> formFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public ArrayList<ServiceReqFormFields> getFormFields() {
        return formFields;
    }

    public void setFormFields(ArrayList<ServiceReqFormFields> formFields) {
        this.formFields = formFields;
    }
}
