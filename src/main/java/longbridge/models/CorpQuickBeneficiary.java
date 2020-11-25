package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause="del_Flag='N'")
public class CorpQuickBeneficiary extends Beneficiary{
    @ManyToOne
    private Corporate corporate;
    private String lastname;
    private String othernames;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
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
        return "CorpQuickBeneficiary{" +
                "corporate=" + corporate +
                ", lastname='" + lastname + '\'' +
                ", othernames='" + othernames + '\'' +
                '}';
    }
}
