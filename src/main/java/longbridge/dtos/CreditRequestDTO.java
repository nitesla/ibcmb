package longbridge.dtos;



/**
 * Created by ayoade_farooq@yahoo.com on 6/21/2017.
 */

public class CreditRequestDTO {
    private String description="NAPS TRANSFER";
    private String beneficiaryName;
    private String email;
    private String beneficiaryAccountNumber;
    private String paymentReference;
    private String accountName;
    private String accountNumber;
    private String beneficiaryBankCode;
    private String batchId;
    private String payerName ;
    private String payerAccountNumber;
    private String amount;
    private String narration;

    public CreditRequestDTO() {
    }

    public CreditRequestDTO(String description, String beneficiaryName, String email, String beneficiaryAccountNumber, String paymentReference, String accountName, String accountNumber, String beneficiaryBankCode, String batchId, String payerName, String payerAccountNumber, String amount, String narration) {
        this.description = description;
        this.beneficiaryName = beneficiaryName;
        this.email = email;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.paymentReference = paymentReference;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.beneficiaryBankCode = beneficiaryBankCode;
        this.batchId = batchId;
        this.payerName = payerName;
        this.payerAccountNumber = payerAccountNumber;
        this.amount = amount;
        this.narration = narration;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
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

    public String getBeneficiaryBankCode() {
        return beneficiaryBankCode;
    }

    public void setBeneficiaryBankCode(String beneficiaryBankCode) {
        this.beneficiaryBankCode = beneficiaryBankCode;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerAccountNumber() {
        return payerAccountNumber;
    }

    public void setPayerAccountNumber(String payerAccountNumber) {
        this.payerAccountNumber = payerAccountNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    @Override
    public String toString() {
        return "CreditRequestDTO{" +
                "description='" + description + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", email='" + email + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", paymentReference='" + paymentReference + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", beneficiaryBankCode='" + beneficiaryBankCode + '\'' +
                ", batchId='" + batchId + '\'' +
                ", payerName='" + payerName + '\'' +
                ", payerAccountNumber='" + payerAccountNumber + '\'' +
                ", amount='" + amount + '\'' +
                ", narration='" + narration + '\'' +
                '}';
    }
}
