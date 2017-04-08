package longbridge.dtos;

import longbridge.models.ServiceReqFormFields;

import java.util.Collection;

/**
 * Created by Showboy on 08/04/2017.
 */
public class ServiceReqConfigDTO {

    private String requestName;
    private Collection<ServiceReqFormFields> formFields;

    public String getName() {
        return requestName;
    }

    public void setName(String name) {
        this.requestName = name;
    }

    public Collection<ServiceReqFormFields> getFormFields() {
        return formFields;
    }

    public void setFormFields(Collection<ServiceReqFormFields> formFields) {
        this.formFields = formFields;
    }
}
