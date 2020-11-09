package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class QuickBeneficiary extends AbstractEntity{

    private String lastname;
    private String othernames;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getOthernames() {
        return othernames;
    }

    public void setOthernames(String othernames) {
        this.othernames = othernames;
    }

    @Override
    public String toString() {
        return "QuickBeneficiary{" +
                "lastname='" + lastname + '\'' +
                ", othernames='" + othernames + '\'' +
                '}';
    }
}
