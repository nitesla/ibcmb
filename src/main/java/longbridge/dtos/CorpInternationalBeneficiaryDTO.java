package longbridge.dtos;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public class CorpInternationalBeneficiaryDTO {
    private Long id;
    @NotEmpty(message = "Please enter a Beneficiary Name")
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
    private String intermediaryBankAccountNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public String getIntermediaryBankAccountNumber() {
        return intermediaryBankAccountNumber;
    }

    public void setIntermediaryBankAccountNumber(String intermediaryBankAccountNumber) {
        this.intermediaryBankAccountNumber = intermediaryBankAccountNumber;
    }
}
