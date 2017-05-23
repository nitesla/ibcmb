package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Entity
@Audited
@Where(clause="del_Flag='N'")
public class CorpLocalBeneficiary extends Beneficiary {
    @ManyToOne
    private CorporateUser user;
    private String preferredName;

    public CorporateUser getUser() {
        return user;
    }

    public void setUser(CorporateUser user) {
        this.user = user;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    @Override
    public String toString() {
        return "CorpLocalBeneficiary{" +
                "user=" + user +
                ", preferredName='" + preferredName + '\'' +
                '}';
    }
}
