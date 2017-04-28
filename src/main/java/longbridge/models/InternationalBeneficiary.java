package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Wunmi on 30/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class InternationalBeneficiary extends Beneficiary {

    @ManyToOne
    private RetailUser user;

    private String swiftCode;
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAccountNumber;

    public RetailUser getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    public void setBeneficiaryAddress(String beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress;
    }

    public String getIntermediaryBankName() {
        return intermediaryBankName;
    }

    public void setIntermediaryBankName(String intermediaryBankName) {
        this.intermediaryBankName = intermediaryBankName;
    }

    public String getIntermediaryBankAccountNumber() {
        return intermediaryBankAccountNumber;
    }

    public void setIntermediaryBankAccountNumber(String intermediaryBankAccountNumber) {
        this.intermediaryBankAccountNumber = intermediaryBankAccountNumber;
    }
}
