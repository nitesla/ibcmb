package longbridge.dtos;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 Created by Fortune on 4/5/2017.
 */


public class InternationalBeneficiaryDTO implements Serializable{

    private Long id;
    private String accountName;
    @NotEmpty(message = "Please enter an Account Number")
    private String accountNumber;
    @NotEmpty(message = "Please enter a Beneficiary Bank")
    private String beneficiaryBank;
    @NotEmpty(message = "Please enter a Swift Code")
    private String swiftCode;
    @NotEmpty(message = "Please enter a Sort Code")
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAcctNo;
    private String preferredName;

    private String currencyCode;
    private String chargeFrom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String name) {
        this.accountName = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

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

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getChargeFrom() {
        return chargeFrom;
    }

    public void setChargeFrom(String chargeFrom) {
        this.chargeFrom = chargeFrom;
    }
}
