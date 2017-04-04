package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 31/03/2017.
 */
@Entity
public class LocalBeneficiary extends Beneficiary {

    @Override
    public String toString() {
        return "LocalBeneficiary{" + super.toString() + "}";
    }
}
