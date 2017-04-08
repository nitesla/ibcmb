package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Entity
public class ServiceReqConfig extends AbstractEntity{

    private String requestName;

    @OneToMany
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
