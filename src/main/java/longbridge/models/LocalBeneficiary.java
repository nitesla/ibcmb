package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 31/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class LocalBeneficiary extends Beneficiary {

    @Override
    public String toString() {
        return "LocalBeneficiary{" + super.toString() + "}";
    }
}
