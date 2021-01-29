package longbridge.dtos;

 import java.io.Serializable;

/**
 * Created by Fortune on 3/5/2018.
 */
public class CorpInternationalTransferRequestDTO extends CorpTransferRequestDTO implements Serializable {

    private String beneficiaryBank;
    private String swiftCode;
    private String sortCode;
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
}
