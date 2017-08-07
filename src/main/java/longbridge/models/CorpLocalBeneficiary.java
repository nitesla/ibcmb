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
public class CorpLocalBeneficiary extends Beneficiary {
    @ManyToOne
    private Corporate corporate;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }


}
