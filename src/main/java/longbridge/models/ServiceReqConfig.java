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
