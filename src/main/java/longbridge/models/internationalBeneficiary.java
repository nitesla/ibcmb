package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Showboy on 30/03/2017.
 */
@Entity
public class internationalBeneficiary extends Beneficiary {

    private String swiftCode;
    private String sortCode;

}
