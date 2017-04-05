package longbridge.dtos;

import longbridge.models.Beneficiary;
import longbridge.models.RetailUser;

import javax.persistence.Entity;

/**
 Created by Fortune on 4/5/2017.
 */


public class InternationalBeneficiaryDTO {

    private Long id;
    private RetailUser user;
    private String name;
    private String accountNo;
    private String beneficiaryBank;
    private String swiftCode;
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAccountNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RetailUser getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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

    public String getIntermediaryBankAccountNumber() {
        return intermediaryBankAccountNumber;
    }

    public void setIntermediaryBankAccountNumber(String intermediaryBankAccountNumber) {
        this.intermediaryBankAccountNumber = intermediaryBankAccountNumber;
    }
}
