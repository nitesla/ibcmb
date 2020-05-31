package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.math.BigDecimal;
import java.util.Date;



/**
 Created by Fortune on 2/8/2018.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_flag='N'")
public class InternationalTransfer extends TransRequest{

    private String beneficiaryBank;
    private String swiftCode;
    private String sortCode;
    @Lob
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAcctNo;
    private String currencyCode;
    private String chargeFrom;


    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getChargeFrom() {
        return chargeFrom;
    }

    public void setChargeFrom(String chargeFrom) {
        this.chargeFrom = chargeFrom;

    }

    @Override
    public String toString() {
        return "InternationalTransfer{" +
                "beneficiaryBank='" + beneficiaryBank + '\'' +
                ", swiftCode='" + swiftCode + '\'' +
                ", sortCode='" + sortCode + '\'' +
                ", beneficiaryAddress='" + beneficiaryAddress + '\'' +
                ", intermediaryBankName='" + intermediaryBankName + '\'' +
                ", intermediaryBankAcctNo='" + intermediaryBankAcctNo + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", chargeFrom='" + chargeFrom + '\'' +
                "} " + super.toString();
    }
}
