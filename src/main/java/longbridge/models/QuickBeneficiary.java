package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class QuickBeneficiary extends Beneficiary{


    @ManyToOne
    private RetailUser user;
    @Column(name = "lst_name")
    private String lastname;
    @Column(name = "othr_name")
    private String othernames;

    public RetailUser getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

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
                "user=" + user +
                ", lastname='" + lastname + '\'' +
                ", othernames='" + othernames + '\'' +
                '}';
    }
}
