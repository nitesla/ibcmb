package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 30/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class InternationalBeneficiary extends Beneficiary {

    private String swiftCode;
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAccountNumber;


}
