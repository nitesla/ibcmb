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

    public String getName() {
        return requestName;
    }

    public void setName(String name) {
        this.requestName = name;
    }

    public Collection<ServiceReqFormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(Collection<ServiceReqFormField> formFields) {
        this.formFields = formFields;
    }
}
