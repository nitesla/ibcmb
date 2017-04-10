package longbridge.models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Entity
public class ServiceReqConfig extends AbstractEntity{

    private String requestName;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<ServiceReqFormField> formFields;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public Collection<ServiceReqFormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(Collection<ServiceReqFormField> formFields) {
        this.formFields = formFields;
    }
}
