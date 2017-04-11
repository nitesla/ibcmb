package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 31/03/2017.
 */
@Entity
@Audited
public class LocalBeneficiary extends Beneficiary {

    @Override
    public String toString() {
        return "LocalBeneficiary{" + super.toString() + "}";
    }
}
