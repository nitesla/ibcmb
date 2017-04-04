package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 30/03/2017.
 */
@Entity
public class InternationalBeneficiary extends Beneficiary {

    private String swiftCode;
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAccountNumber;


}
