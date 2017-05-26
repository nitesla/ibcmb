package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@Entity
@Audited
@Where(clause="del_Flag='N")
public class CorpInterBen extends Beneficiary{
@ManyToOne
    private CorporateUser user;
    private String swiftCode;
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAcctNo;

    public CorporateUser getUser() {
        return user;
    }

    public void setUser(CorporateUser user) {
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

    public String getIntermediaryBankAcctNo() {
        return intermediaryBankAcctNo;
    }

    public void setIntermediaryBankAcctNo(String intermediaryBankAcctNo) {
        this.intermediaryBankAcctNo = intermediaryBankAcctNo;
    }
}
